package app.mealsmadeeasy.api.signup;

import app.mealsmadeeasy.api.user.UserCreateException.Type;
import app.mealsmadeeasy.api.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SignUpControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private MockHttpServletRequestBuilder getCheckUsernameRequest(String usernameToCheck)
            throws JsonProcessingException {
        final Map<String, Object> body = Map.of("username", usernameToCheck);
        return MockMvcRequestBuilders.get("/sign-up/check-username")
                .content(this.objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DirtiesContext
    public void checkUsernameExpectAvailable() throws Exception {
        this.mockMvc.perform(this.getCheckUsernameRequest("isAvailable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(true));
    }

    @Test
    @DirtiesContext
    public void checkUsernameExpectNotAvailable() throws Exception {
        this.userService.createUser("notAvailable", "not-available@notavailable.com", "test");
        this.mockMvc.perform(this.getCheckUsernameRequest("notAvailable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(false));
    }


    private MockHttpServletRequestBuilder getCheckEmailRequest(String emailToCheck) throws JsonProcessingException {
        final Map<String, Object> body = Map.of("email", emailToCheck);
        return MockMvcRequestBuilders.get("/sign-up/check-email")
                .content(this.objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DirtiesContext
    public void checkEmailExpectAvailable() throws Exception {
        this.mockMvc.perform(this.getCheckEmailRequest("available@available.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(true));
    }

    @Test
    @DirtiesContext
    public void checkEmailExpectNotAvailable() throws Exception {
        this.userService.createUser("notAvailable", "not-available@notavailable.com", "test");
        this.mockMvc.perform(this.getCheckEmailRequest("not-available@notavailable.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(false));
    }

    @Test
    @DirtiesContext
    public void simpleSignUp() throws Exception {
        final SignUpBody body = new SignUpBody();
        body.setUsername("newUser");
        body.setEmail("new@user.com");
        body.setPassword("test");
        final MockHttpServletRequestBuilder req = post("/sign-up")
                .content(this.objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(req)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newUser"));
    }

    @Test
    @DirtiesContext
    public void signUpBadRequestWhenUsernameTaken() throws Exception {
        this.userService.createUser("taken", "taken@taken.com", "test");
        final SignUpBody body = new SignUpBody();
        body.setUsername("taken");
        body.setEmail("not-taken@taken.com"); // n.b.
        body.setPassword("test");
        final MockHttpServletRequestBuilder req = post("/sign-up")
                .content(this.objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(req)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.type").value(Type.USERNAME_TAKEN.toString()))
                .andExpect(jsonPath("$.error.message").value(containsString("taken")));
    }

    @Test
    @DirtiesContext
    public void signUpBadRequestWhenEmailTaken() throws Exception {
        this.userService.createUser("taken", "taken@taken.com", "test");
        final SignUpBody body = new SignUpBody();
        body.setUsername("notTaken"); // n.b.
        body.setEmail("taken@taken.com");
        body.setPassword("test");
        final MockHttpServletRequestBuilder req = post("/sign-up")
                .content(this.objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON);
        this.mockMvc.perform(req)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.type").value(Type.EMAIL_TAKEN.toString()))
                .andExpect(jsonPath("$.error.message").value(containsString("taken@taken.com")));
    }

}
