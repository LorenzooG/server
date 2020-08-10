package store.wckd.server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import store.wckd.server.entity.User;
import store.wckd.server.factory.Factory;
import store.wckd.server.factory.UserFactory;
import store.wckd.server.repository.UserRepository;
import store.wckd.server.service.JwtService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtFilterTests {

    private final JwtService jwtService;
    private final Factory<User> userFactory;
    private final MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TESTING_ENDPOINT = "/me";

    @Autowired
    public JwtFilterTests(JwtService jwtService, MockMvc mockMvc, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.mockMvc = mockMvc;

        userFactory = new UserFactory(userRepository);
    }

    @Test
    @DisplayName("It should login when JWT Token is valid")
    public void testAuthenticationFilter() throws Exception {
        User user = userFactory.createOne().block();

        assertNotNull(user);

        String jwtToken = jwtService.encodeJwt(user);
        String userJson = objectMapper.writeValueAsString(user); // TODO: change to DTO

        MockHttpServletRequestBuilder request =
                get(TESTING_ENDPOINT)
                        .header(JwtFilter.AUTHENTICATION_HEADER, JwtFilter.AUTHENTICATION_HEADER_PREFIX + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(content().json(userJson));
    }

}
