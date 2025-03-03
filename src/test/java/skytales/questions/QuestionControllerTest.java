package skytales.questions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.elasticsearch.search.SearchService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindingResult;
import skytales.auth.dto.SessionResponse;
import skytales.auth.service.JwtService;
import skytales.common.configuration.SecurityConfig;
import skytales.common.security.JwtAuthenticationFilter;
import skytales.common.security.SessionService;
import skytales.questions.dto.AnswerRequest;
import skytales.questions.dto.PostQuestionRequest;
import skytales.questions.model.Question;
import skytales.questions.service.QuestionService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(QuestionController.class)
@Import(SecurityConfig.class)
public class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionService questionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private BindingResult bindingResult;

    @InjectMocks
    private QuestionController questionController;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String token;
    private SessionResponse mockSessionResponse;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        Question question1 = new Question();
        Question question2 = new Question();
        token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImNhcnRJZCI6IjRhZWY3OWRjLTg1MzUtNDRkZi04ZmNiLTc2OTg5MWQwZjkxZSIsInVzZXJJZCI6IjczZmRlNDY1LWMwOWItNDljZi1iNTgxLThlZDE0NWE4ODdmZSIsImVtYWlsIjoibXlFbWFpbDBAYWJ2LmJnIiwidXNlcm5hbWUiOiJ1c2VybmFtZUZpbGwiLCJzdWIiOiJteUVtYWlsMEBhYnYuYmciLCJpYXQiOjE3NDA2NDc3NjksImV4cCI6MTc0MDczNDE2OX0.mCKdMRLZcGjtOht9G74i0pQFfzPqLGvJ0GKTOzpa258";

        question1.setText("Sample Question 1");
        question1.setAnswer("Sample Answer 1");

        question2.setText("Sample Question 2");
        question2.setAnswer("Sample Answer 2");

        List<Question> mockQuestions = Arrays.asList(
            question1,question2
        );

        when(bindingResult.hasErrors()).thenReturn(false);

        mockSessionResponse = new SessionResponse("user1@example.com", "user1", "123e4567-e89b-12d3-a456-426614174000", "ADMIN", "cart456");

        when(sessionService.getSessionData(any(HttpServletRequest.class))).thenReturn(mockSessionResponse);
        when(questionService.fetchQuestions( UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))).thenReturn(mockQuestions);
        when(questionService.getAll()).thenReturn(mockQuestions);

    }

    @Test
    void testGetAllQuestions() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/faq/all")
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("Sample Question 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].text").value("Sample Question 2"));

    }

    @Test
    void testGetAllQuestionsEmpty() throws Exception {
        when(questionService.getAll()).thenReturn(Arrays.asList());

        mockMvc.perform(MockMvcRequestBuilders.get("/faq/all"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testAnswerQuestion() throws Exception {
        UUID questionId = UUID.randomUUID();
        UUID adminId = UUID.fromString("73fded46-c09b-49cf-b581-8ed145a887fe");
        AnswerRequest answerRequest = new AnswerRequest("Sample Answer");

        Mockito.doNothing().when(questionService).sendAnswer(Mockito.eq(questionId), Mockito.any(AnswerRequest.class), Mockito.eq(adminId));

        String answerRequestJson = objectMapper.writeValueAsString(answerRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/faq/{id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(answerRequestJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testAnswerQuestionWithBindingErrors() throws Exception {

        when(bindingResult.hasErrors()).thenReturn(true);
        UUID questionId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.put("/faq/{id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\": \"Sample Answer\"}")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testCreateQuestion() throws Exception {
        PostQuestionRequest request = new PostQuestionRequest("How was your day?");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/faq/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        verify(questionService).createQuestion(Mockito.any(PostQuestionRequest.class), Mockito.eq(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")));
    }

    @Test
    void testGetUserQuestions() throws Exception {
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(MockMvcRequestBuilders.get("/faq/{userId}/questions", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))  // Check if there are 2 questions
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text", Matchers.is("Sample Question 1"))) // First question text
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].text", Matchers.is("Sample Question 2")));

        verify(questionService).fetchQuestions(Mockito.eq(userId));
    }

}
