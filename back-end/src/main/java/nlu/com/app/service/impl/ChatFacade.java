package nlu.com.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nlu.com.app.constant.ERole;
import nlu.com.app.constant.EStatusMessage;
import nlu.com.app.dto.request.MessageRequestDTO;
import nlu.com.app.dto.response.AdminConversationInboxResponseDTO;
import nlu.com.app.dto.response.CanChatUserResponseDTO;
import nlu.com.app.dto.response.MessageResponseDTO;
import nlu.com.app.entity.ChatHistory;
import nlu.com.app.entity.Conversation;
import nlu.com.app.entity.User;
import nlu.com.app.repository.ChatHistoryRepository;
import nlu.com.app.repository.ConversationRepository;
import nlu.com.app.service.ConversationService;
import nlu.com.app.service.IChatService;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ChatFacade {
    static final String CHAT_KEY_PREFIX = "chat:history:";
    final ChatHistoryRepository chatHistoryRepository;
    final SimpMessagingTemplate simpMessagingTemplate;
    final ConversationRepository conversationRepository;
    final ConversationService conversationService;
    final IChatService chatService;
    final StringRedisTemplate redisTemplate;

    // Trong ChatFacade.java
    @MessageExceptionHandler(Exception.class)
    public void handleUserMessage(User user, MessageRequestDTO messageRequestDTO) {

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // 1️⃣ Lấy hoặc tạo conversation
        Conversation conversation = conversationRepository.findByClient_UserId(user.getUserId()).orElse(null);
        if (conversation == null) {
            conversationService.createNewConversation(user.getUserId());
        }
        // Refresh lại entity để đảm bảo đồng bộ
        conversation = conversationRepository.findByClient_UserId(user.getUserId()).get();
        try {

            // 2️⃣ Lưu USER message vào DB
            ChatHistory userMessage = saveUserMessage(user, conversation, messageRequestDTO.getMessage());

            // 3️⃣ Push USER message vào Redis (GPT context)
            redisTemplate.opsForList()
                    .leftPush(CHAT_KEY_PREFIX + user.getUserId(), "Đây là tin nhắn của Client: " + messageRequestDTO.getMessage() + "\n");

            MessageResponseDTO userResponse = MessageResponseDTO.builder()
                    .sender("USER") // Hoặc "USER" tùy quy ước FE
                    .text(messageRequestDTO.getMessage())
                    .sentAt(userMessage.getCreatedDate())
                    .build();

            simpMessagingTemplate.convertAndSend(
                    "/receive/message/conversation/" + conversation.getId(),
                    userResponse
            );
            // -----------------------------------------------------------------------

            //  Nếu target là ADMIN → dừng tại đây (Admin tự trả lời, Bot không rep)
            if (conversation.getTarget() == ERole.ADMIN) {
                // Vì đã gửi socket ở trên rồi, nên return ở đây là an toàn, Admin đã nhận được tin nhắn User
                return;
            }

            //  BOT trả lời (Logic cũ giữ nguyên)
            String botReply = chatService.reply(user.getUserId());

            //  Push BOT reply vào Redis
            redisTemplate.opsForList()
                    .leftPush(CHAT_KEY_PREFIX + user.getUserId(), botReply);

            //  Lưu BOT message
            ChatHistory botMessage = saveBotMessage(user, conversation, botReply);

            //  Gửi tin nhắn BOT ra Socket
            MessageResponseDTO botResponse = MessageResponseDTO.builder()
                    .sender("BOT")
                    .text(botReply)
                    .sentAt(botMessage.getCreatedDate())
                    .build();

            simpMessagingTemplate.convertAndSend(
                    "/receive/message/conversation/" + conversation.getId(),
                    botResponse
            );

            // Notify inbox update (để danh sách bên trái nhảy lên đầu)
            notifyAdminInbox(conversation, botMessage);
        } catch (Exception e) {
            Throwable rootCause = e;
            while (rootCause.getCause() != null && rootCause != rootCause.getCause()) {
                rootCause = rootCause.getCause();
            }

            if (rootCause instanceof NonTransientAiException) {
                System.out.println("Đã 'đào' được lỗi NonTransientAiException!");
                String errorMessage = "Hiện tại chủ web đã hết token để duy trì chức năng chat vs AI, mong mọi người thông cảm nhiều :(((((";
                simpMessagingTemplate.convertAndSend("/topic/errors/conversation/" + conversation.getId(), errorMessage);
            } else {
                log.error("Lỗi WebSocket khác: ", e);
            }
        }
    }

    private ChatHistory saveBotMessage(User user, Conversation conversation, String botReply) {
        ChatHistory botMessage = new ChatHistory();
        botMessage.setUser(user);
        botMessage.setRole(ERole.BOT);
        botMessage.setMessage(botReply);
        botMessage.setCreatedDate(LocalDateTime.now());
        botMessage.setIsRead(false);
        botMessage.setSenderId("BOTCHAT");
        botMessage.setConversation_chat_history(conversation);

        chatHistoryRepository.save(botMessage);
        conversation.getChatHistory().add(botMessage);

        return botMessage;
    }

    private ChatHistory saveUserMessage(User user, Conversation conversation, String message) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setUser(user);
        chatHistory.setRole(ERole.USER);
        chatHistory.setMessage(message);
        chatHistory.setCreatedDate(LocalDateTime.now());
        chatHistory.setIsRead(false);
        chatHistory.setSenderId(user.getUserId().toString());
        chatHistory.setConversation_chat_history(conversation);

        chatHistoryRepository.save(chatHistory);
        conversation.getChatHistory().add(chatHistory);

        notifyAdminInbox(conversation, chatHistory);

        return chatHistory; // <--- Return entity
    }

    public void handleAdminMessage(
            User admin,
            MessageRequestDTO dto,
            Long conversationId
    ) throws AccessDeniedException {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        if (
                conversation.getCurrentAdmin() == null ||
                        !conversation.getCurrentAdmin().getUserId().equals(admin.getUserId())
        ) {
            MessageResponseDTO response = new MessageResponseDTO();
            response.setSender("INVALID_HANDLE_CHAT");
            response.setText(admin.getRole() + " " + admin.getUsername().toUpperCase() + " không sở hữu conversation này để chat với user");
            response.setSentAt(LocalDateTime.now());

            simpMessagingTemplate.convertAndSend(
                    "/receive/message/conversation/" + conversation.getId(),
                    response
            );
            return;
        }

        conversation.setStatus(EStatusMessage.HAS_ADMIN);
        conversation.setTarget(ERole.ADMIN);
        User user = conversation.getClient();
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setUser(user);
        chatHistory.setRole(ERole.ADMIN);
        chatHistory.setMessage(dto.getMessage());
        chatHistory.setCreatedDate(LocalDateTime.now());
        chatHistory.setIsRead(false);
        chatHistory.setSenderId(admin.getUserId().toString());
        chatHistory.setConversation_chat_history(conversation);

        conversation.getChatHistory().add(chatHistory);
        chatHistoryRepository.save(chatHistory);


        redisTemplate.opsForList().leftPush(
                CHAT_KEY_PREFIX + conversation.getClient().getUserId(),
                "Đây là tin nhắn của quản lý nhà sách Wibu Bookstore: " + dto.getMessage()
        );

        MessageResponseDTO response = new MessageResponseDTO();
        response.setSender("ADMIN");
        response.setText(dto.getMessage());
        response.setSentAt(LocalDateTime.now());

        simpMessagingTemplate.convertAndSend(
                "/receive/message/conversation/" + conversation.getId(),
                response
        );
        notifyAdminInbox(conversation, chatHistory);
    }

    //    admin nhường chat cho BOT
    public CanChatUserResponseDTO leaveConversation(
            Long conversationId,
            User admin
    ) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        if (
                conversation.getCurrentAdmin() == null ||
                        !conversation.getCurrentAdmin().getUserId().equals(admin.getUserId())
        ) {
            return CanChatUserResponseDTO.builder()
                    .code(9999L)
                    .status("FAIL")
                    .message("Bạn không phải admin đang xử lý conversation này")
                    .build();
        }

        conversation.setCurrentAdmin(null);
        conversation.setStatus(EStatusMessage.WAITING_ADMIN);
        conversation.setTarget(ERole.BOT);
        conversationRepository.save(conversation);

        // (Optional) gửi system message
        MessageResponseDTO systemMsg = new MessageResponseDTO();
        systemMsg.setSender("SYSTEM");
        systemMsg.setText(admin.getRole() + " " + admin.getUsername().toUpperCase() + " đã rời cuộc trò chuyện. BOT sẽ tiếp tục hỗ trợ.");
        systemMsg.setSentAt(LocalDateTime.now());

        simpMessagingTemplate.convertAndSend(
                "/receive/message/conversation/" + conversation.getId(),
                systemMsg
        );
        simpMessagingTemplate.convertAndSend(
                "/topic/admin/inbox",
                "AMIN_LEAVED"
        );

        String text = admin.getRole() + " " + admin.getUsername().toUpperCase() + " đã rời cuộc trò chuyện. BOT sẽ tiếp tục hỗ trợ.";
        User user = conversation.getClient();
        ChatHistory systemMessage = new ChatHistory();
        systemMessage.setUser(user);
        systemMessage.setRole(ERole.SYSTEM); // Hoặc set Role riêng nếu muốn
        systemMessage.setMessage(text);
        systemMessage.setCreatedDate(LocalDateTime.now());
        systemMessage.setIsRead(true);
        systemMessage.setSenderId("SYSTEM"); // Đánh dấu đây là System message
        systemMessage.setConversation_chat_history(conversation);

        chatHistoryRepository.save(systemMessage);

        return CanChatUserResponseDTO.builder()
                .code(1000L)
                .status("Fail")
                .message("Đã nhường conversation cho BOT")
                .build();
    }

    private void notifyAdminInbox(Conversation conversation, ChatHistory lastMessage) {
        AdminConversationInboxResponseDTO dto = new AdminConversationInboxResponseDTO();
        dto.setConversationId(conversation.getId());
        dto.setUserId(conversation.getClient().getUserId());
        dto.setUsername(conversation.getClient().getUsername());
        dto.setLastMessage(lastMessage.getMessage());
        dto.setLastMessageTime(lastMessage.getCreatedDate());
        dto.setStatus(
                conversation.getCurrentAdmin() == null ? "BOT" : "ADMIN"
        );
        dto.setCurrentAdmin(
                conversation.getCurrentAdmin() == null
                        ? null
                        : conversation.getCurrentAdmin().getUsername()
        );
        simpMessagingTemplate.convertAndSend("/topic/admin/inbox", dto);
    }
}
