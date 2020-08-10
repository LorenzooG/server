package store.wckd.server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import store.wckd.server.entity.User;
import store.wckd.server.factory.Factory;
import store.wckd.server.factory.UserFactory;
import store.wckd.server.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTests {

    @MockBean
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Factory<User> userFactory;
    private final MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TESTING_ENDPOINT = "/me";

    @Autowired
    public AuthenticationTests(MockMvc mockMvc, UserRepository userRepository) {
        this.mockMvc = mockMvc;

        userFactory = new UserFactory(userRepository);
    }

    @Test
    @DisplayName("It should login properly and return a valid jwt token to be used in the headers")
    public void testLogin() throws Exception {
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        User user = userFactory.createOne().block();

        assertNotNull(user);

        MockHttpServletRequestBuilder request =
                post(String.format("/login?username=%s&password=%s", user.getUsername(), user.getPassword()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200));
    }
}
