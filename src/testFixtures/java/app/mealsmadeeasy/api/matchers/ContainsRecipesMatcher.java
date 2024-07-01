package app.mealsmadeeasy.api.matchers;

import app.mealsmadeeasy.api.recipe.Recipe;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;
import java.util.Objects;

public final class ContainsRecipesMatcher extends BaseMatcher<List<Recipe>> {

    private final Recipe[] allExpected;

    public ContainsRecipesMatcher(Recipe[] allExpected) {
        this.allExpected = allExpected;
    }

    @Override
    public boolean matches(Object actual) {
        if (actual instanceof List<?> list) {
            checkExpected:
            for (final Recipe expected : allExpected) {
                for (final Object item : list) {
                    if (item instanceof Recipe o && Objects.equals(o.getId(), expected.getId())) {
                        continue checkExpected;
                    }
                }
                // Did not find the expected in the list
                return false;
            }
            // Found all expected in list
            return true;
        }
        // actual is not a List
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Expected ").appendValue(List.of(this.allExpected));
    }

}
