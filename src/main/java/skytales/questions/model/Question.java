package skytales.questions.model;

import jakarta.persistence.*;
import lombok.*;
import skytales.auth.model.User;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    private String answer;

    @Column(nullable = false)
    private String status = "pending";

    @ManyToOne
    private User author;

    @ManyToOne
    private User admin;

}
