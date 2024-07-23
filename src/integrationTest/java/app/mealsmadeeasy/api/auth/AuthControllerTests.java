package app.mealsmadeeasy.api.auth;

import app.mealsmadeeasy.api.user.User;
import app.mealsmadeeasy.api.user.UserCreateException;
import app.mealsmadeeasy.api.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private User createTestUser() {
        try {
            return this.userService.createUser("test", "test@test.com", "test");
        } catch (UserCreateException e) {
            throw new RuntimeException(e);
        }
    }

    private MockHttpServletRequestBuilder getLoginRequest() throws Exception {
        final Map<String, ?> body = Map.of(
                "username", "test",
                "password", "test"
        );
        return post("/auth/login")
                .content(this.objectMapper.writeValueAsString(body))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user("test").password("test"));
    }

    @BeforeEach
    public void setup() {
        final User testUser = this.createTestUser();
        System.out.println("Created testUser: " + testUser);
    }

    @Test
    @DirtiesContext
    public void simpleLogin() throws Exception {
        this.mockMvc.perform(this.getLoginRequest())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(cookie().exists("refresh-token"));
    }

    private Cookie getRefreshTokenCookie() throws Exception {
        final MvcResult loginResult = this.mockMvc.perform(this.getLoginRequest()).andReturn();
        final Cookie refreshTokenCookie = loginResult.getResponse().getCookie("refresh-token");
        if (refreshTokenCookie == null) {
            throw new NullPointerException("refreshTokenCookie is null");
        }
        return refreshTokenCookie;
    }

    @Test
    @DirtiesContext
    public void simpleLogout() throws Exception {
        final MockHttpServletRequestBuilder req = post("/auth/logout")
                .cookie(this.getRefreshTokenCookie());
        this.mockMvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("refresh-token", 0));
    }

    @Test
    @DirtiesContext
    public void simpleRefresh() throws Exception {
        final Cookie firstRefreshTokenCookie = this.getRefreshTokenCookie();
        final MockHttpServletRequestBuilder req = post("/auth/refresh")
                .cookie(firstRefreshTokenCookie);
        final MvcResult res = this.mockMvc.perform(req)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(cookie().exists("refresh-token"))
                .andReturn();
        final Cookie secondRefreshTokenCookie = res.getResponse().getCookie("refresh-token");
        if (secondRefreshTokenCookie == null) {
            throw new NullPointerException("secondRefreshTokenCookie is null");
        }
        assertThat(firstRefreshTokenCookie.getValue(), is(not(secondRefreshTokenCookie.getValue())));
    }

}
