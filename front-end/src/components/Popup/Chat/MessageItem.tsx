import { ListItem, ListItemAvatar, Avatar, Box, Typography } from "@mui/material";
import { keyframes } from "@mui/system"; // Import keyframes để tạo animation

export interface Message {
  sender: string;
  text: string;
  sentAt: string;
}

// 1. Tạo hiệu ứng nhấp nháy cho 3 dấu chấm (Bouncing dots)
const bounceAnimation = keyframes`
  0% { transform: translateY(0); opacity: 0.5; }
  50% { transform: translateY(-4px); opacity: 1; }
  100% { transform: translateY(0); opacity: 0.5; }
`;

// 2. Component hiển thị trạng thái đang gõ (Typing Indicator)
export  function TypingMessage({ avatarSrc }: { avatarSrc?: string }) {
  // Style bong bóng giống bong bóng chat của Bot
  const bubbleStyle = { 
    borderRadius: "16px 16px 16px 4px",
    bgcolor: "background.paper", // Hoặc màu xám nhạt #e4e6eb giống Messenger
    boxShadow: 1,
    p: 1.5,
    minWidth: "60px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: 0.5
  };

  // Style cho từng dấu chấm
  const dotStyle = {
    width: 6,
    height: 6,
    bgcolor: "text.secondary",
    borderRadius: "50%",
    display: "inline-block",
  };

  return (
    <ListItem
      sx={{
        display: "flex",
        justifyContent: "flex-start",
        alignItems: "flex-start",
        py: 0.5,
        px: 1.5,
        mb: 1,
      }}
      disableGutters
    >
      <ListItemAvatar>
        <Avatar alt="Bot" src={avatarSrc}>B</Avatar>
      </ListItemAvatar>

      <Box sx={bubbleStyle}>
        {/* 3 dấu chấm với delay khác nhau để tạo hiệu ứng sóng */}
        <Box sx={{ ...dotStyle, animation: `${bounceAnimation} 0.6s infinite`, animationDelay: "0s" }} />
        <Box sx={{ ...dotStyle, animation: `${bounceAnimation} 0.6s infinite`, animationDelay: "0.2s" }} />
        <Box sx={{ ...dotStyle, animation: `${bounceAnimation} 0.6s infinite`, animationDelay: "0.4s" }} />
      </Box>
    </ListItem>
  );
}

// --- Component MessageItem chính (giữ nguyên logic của bạn) ---
export default function MessageItem({ msg, isUser, avatarSrc }: { msg: Message, isUser: boolean, avatarSrc?: string }) {

  if (msg.sender === "SYSTEM") {
    return (
      <ListItem sx={{ display: "flex", justifyContent: "center", py: 1, mb: 1 }}>
        <Typography 
          variant="caption" 
          sx={{ 
            color: "text.secondary",
            fontStyle: "italic",
            textAlign: "center",
            fontSize: "0.75rem"
          }}
        >
          {msg.text}
        </Typography>
      </ListItem>
    );
  }

  const time = msg.sentAt;
  const bubbleStyle = isUser
    ? { borderRadius: "16px 16px 4px 16px" }
    : { borderRadius: "16px 16px 16px 4px" };

  return (
    <ListItem
      sx={{
        display: "flex",
        justifyContent: isUser ? "flex-end" : "flex-start",
        alignItems: "flex-start",
        py: 0.5,
        px: 1.5,
        marginBottom: 1,
      }}
      disableGutters
    >
      {!isUser && (
        <ListItemAvatar>
          <Avatar alt={msg.sender} src={avatarSrc}>
            {msg.sender?.[0]}
          </Avatar>
        </ListItemAvatar>
      )}

      <Box
        sx={{
          maxWidth: "72%",
          bgcolor: isUser ? "primary.main" : "background.paper",
          color: isUser ? "primary.contrastText" : "text.primary",
          p: 1,
          ml: !isUser ? 1 : 0,
          mr: isUser ? 1 : 0,
          ...bubbleStyle,
          boxShadow: 1,
          whiteSpace: "pre-line",
        }}
      >
        <Typography variant="body2">{msg.text}</Typography>
        <Typography variant="caption" sx={{ display: "block", mt: 0.5, opacity: 0.7, textAlign: isUser ? "right" : "left" }}>
          {time}
        </Typography>
      </Box>

      {isUser && (
        <ListItemAvatar>
          <Avatar alt={msg.sender} src={avatarSrc}>
            {msg.sender?.[0]}
          </Avatar>
        </ListItemAvatar>
      )}
    </ListItem>
  );
}