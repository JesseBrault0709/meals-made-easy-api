package app.mealsmadeeasy.api.matchers;

import app.mealsmadeeasy.api.recipe.star.RecipeStar;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Objects;
import java.util.Set;

public class ContainsStarsMatcher extends BaseMatcher<Set<RecipeStar>> {

    private final RecipeStar[] allExpected;

    public ContainsStarsMatcher(RecipeStar[] allExpected) {
        this.allExpected = allExpected;
    }

    @Override
    public boolean matches(Object actual) {
        if (actual instanceof Set<?> set) {
            checkExpected:
            for (final RecipeStar expected : allExpected) {
                for (final Object item : set) {
                    if (item instanceof RecipeStar o && Objects.equals(o.getId(), expected.getId())) {
                        continue checkExpected;
                    }
                }
                // Did not find the expected in the set
                return false;
            }
            // Found all expected in set
            return true;
        }
        // actual is not a Set
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Expected ").appendValue(Set.of(this.allExpected));
    }

}
