package app.mealsmadeeasy.api.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class ContainsItemsMatcher<T, ID> extends BaseMatcher<Iterable<T>> {

    private final List<T> allExpected;
    private final Predicate<Object> isT;
    private final Function<T, ID> idFunction;

    public ContainsItemsMatcher(List<T> allExpected, Predicate<Object> isT, Function<T, ID> idFunction) {
        this.allExpected = allExpected;
        this.isT = isT;
        this.idFunction = idFunction;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object o) {
        if (o instanceof Iterable<?> iterable) {
            checkExpected:
            for (final T expected : this.allExpected) {
                for (final Object item : iterable) {
                    if (
                            this.isT.test(item) && Objects.equals(
                                    this.idFunction.apply((T) item),
                                    this.idFunction.apply(expected)
                            )
                    ) {
                        continue checkExpected;
                    }
                }
                // did not find expected in iterable
                return false;
            }
            // found all expected in iterable
            return true;
        }
        // o is not an iterable
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Expected ").appendValue(this.allExpected);
    }

}
