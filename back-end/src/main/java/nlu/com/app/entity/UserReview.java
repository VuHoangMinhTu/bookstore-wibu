package nlu.com.app.entity;

import jakarta.persistence.*;
import lombok.*;
import nlu.com.app.constant.ReviewType;

import java.time.LocalDate;

@Entity
@Table(name = "user_reviews")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "review_type")
    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = true)
    private Book book; // NULL if review collection

    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = true)
    private BookCollection collection; // NULL if review book

    @Column(name = "rating")
    private double rating;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "review_date")
    private LocalDate reviewDate;
}

