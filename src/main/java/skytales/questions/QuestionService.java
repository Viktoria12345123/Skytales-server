package skytales.questions;

import org.springframework.stereotype.Service;
import skytales.questions.model.Question;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {


    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> fetchQuestions(UUID userId) {
        return questionRepository.findByAuthorIdAndAnswerIsNotNull(userId);
    }

}
