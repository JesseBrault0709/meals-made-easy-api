package app.mealsmadeeasy.api.user;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Objects;

public final class IsUserMatcher extends BaseMatcher<User> {

    private final User expected;

    public IsUserMatcher(User expected) {
        this.expected = expected;
    }

    public static IsUserMatcher isUser(User expected) {
        return new IsUserMatcher(expected);
    }

    @Override
    public boolean matches(Object actual) {
        return actual instanceof User o && Objects.equals(o.getId(), this.expected.getId());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Expected ").appendValue(this.expected);
    }

}
