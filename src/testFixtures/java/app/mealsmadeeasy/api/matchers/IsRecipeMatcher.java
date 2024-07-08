package app.mealsmadeeasy.api.matchers;

import app.mealsmadeeasy.api.recipe.Recipe;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Objects;

public class IsRecipeMatcher extends BaseMatcher<Recipe> {

    private final Recipe expected;

    public IsRecipeMatcher(Recipe expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object actual) {
        return actual instanceof Recipe o && Objects.equals(this.expected.getId(), o.getId());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Expected ").appendValue(this.expected);
    }

}
