package com.foro.hub.foro_hub_api;

import com.foro.hub.foro_hub_api.controller.TopicsController;
import com.foro.hub.foro_hub_api.domain.course.Course;
import com.foro.hub.foro_hub_api.domain.course.CourseRepository;
import com.foro.hub.foro_hub_api.domain.topic.Topic;
import com.foro.hub.foro_hub_api.domain.topic.TopicRepository;
import com.foro.hub.foro_hub_api.domain.user.User;
import com.foro.hub.foro_hub_api.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ForoHubTests {

    @InjectMocks
    private TopicsController topicsController;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void contextLoads() {
        // Verify that the controller is properly initialized
        assertThat(topicsController).isNotNull();
    }

    @Test
    @WithMockUser
    void testGetTopicsEndpoint() throws Exception {
        // Test the GET /topics endpoint returns 200 OK
        mockMvc.perform(get("/topics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testCreateTopicWithValidData() throws Exception {
        // Mock course and user repositories to return valid data
        Course course = new Course(1L, "Curso Test", "Categoria Test");
        User author = new User(1L, "User Test", "test@gmail.com", "123456");

        when(courseRepository.findByName(anyString())).thenReturn(Optional.of(course));
        when(userRepository.findByName(anyString())).thenReturn(Optional.of(author));
        
        // This is the key fix - ensure existsByTitleAndMessage returns false to allow creation
        when(topicRepository.existsByTitleAndMessage(anyString(), anyString())).thenReturn(false);

        // Mock topic repository to accept new topics
        Topic newTopic = new Topic("Test Title", "Test Message", course, author);

        when(topicRepository.save(any(Topic.class))).thenReturn(newTopic);

        // Test creating a topic with valid data
        String topicJson = "{\"title\":\"Test Title\",\"message\":\"Test Message\",\"authorName\":\"User Test\",\"courseName\":\"Curso Test\"}";

        mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(topicJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    @WithMockUser
    void testCourseValidationFailure() throws Exception {
        // Mock user repository to return valid data but course not found
        User author = new User(1L, "User Test", "test@gmail.com", "123456");

        when(userRepository.findByName(anyString())).thenReturn(Optional.of(author));
        when(courseRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Test creating a topic with non-existent course
        String topicJson = "{\"title\":\"Test Title\",\"message\":\"Test Message\",\"authorName\":\"User Test\",\"courseName\":\"Non Existent Course\"}";

        mockMvc.perform(post("/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(topicJson))
                .andExpect(status().isBadRequest());
    }
}