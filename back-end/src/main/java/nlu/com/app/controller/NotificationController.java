package nlu.com.app.controller;

import lombok.RequiredArgsConstructor;
import nlu.com.app.dto.AppResponse;
import nlu.com.app.dto.response.NotificationResponseDTO;
import nlu.com.app.entity.Notifications;
import nlu.com.app.repository.NotificationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;

    @GetMapping("/user/{userId}")
    public AppResponse<List<NotificationResponseDTO>> getUserNotifications(@PathVariable Long userId) {
        List<Notifications> list = notificationRepository.findByUser_UserId(userId);
        List<NotificationResponseDTO> result = list.stream()
                .map(notification -> new NotificationResponseDTO(
                        notification.getId(),
                        notification.getUser().getUserId(),
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.isRead(),
                        notification.getCreateDate()
                ))
                .toList();
        return AppResponse.<List<NotificationResponseDTO>>builder().result(result).build();
    }


    @GetMapping("/unread-count/{userId}")
    public AppResponse<Long> getUnreadCount(@PathVariable Long userId) {
        long count = notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
        return AppResponse.<Long>builder().result(count).build();
    }

    @PutMapping("/read/{notificationId}")
    public AppResponse<String> markAsRead(@PathVariable Long notificationId) {
        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Thông báo không tồn tại!"));
        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
        return AppResponse.<String>builder().result("Đã đánh dấu đã đọc.").build();
    }

}
