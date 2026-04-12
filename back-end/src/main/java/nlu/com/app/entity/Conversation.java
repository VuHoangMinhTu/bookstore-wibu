package nlu.com.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nlu.com.app.constant.ERole;
import nlu.com.app.constant.EStatusMessage;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversation")
@Getter
@Setter
public class Conversation {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = true)
    private User currentAdmin;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ERole target; // BOT or ADMIN

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EStatusMessage status; // WAITING_ADMIN or HAS_ADMIN or CLOSED
    // Mỗi conversation chỉ có 1 user
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude
    private User client;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "conversation_chat_history", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatHistory> chatHistory = new ArrayList<>();
}
