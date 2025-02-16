package skytales.questions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skytales.questions.model.Question;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/faq")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Question>> getUserQuestions(@PathVariable UUID userId) {
        List<Question> questions = questionService.fetchQuestions(userId);
        return ResponseEntity.ok(questions);
    }
}