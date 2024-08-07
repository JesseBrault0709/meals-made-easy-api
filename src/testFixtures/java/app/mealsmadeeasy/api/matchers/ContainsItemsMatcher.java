package app.mealsmadeeasy.api.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class ContainsItemsMatcher<T, E, ID> extends BaseMatcher<Iterable<T>> {

    private final List<E> allExpected;
    private final Predicate<Object> isT;
    private final Function<T, ID> givenToIdFunction;
    private final Function<E, ID> expectedToIdFunction;
    private final BiPredicate<ID, ID> equalsFunction;

    public ContainsItemsMatcher(
            List<E> allExpected,
            Predicate<Object> isT,
            Function<T, ID> givenToIdFunction,
            Function<E, ID> expectedToIdFunction,
            BiPredicate<ID, ID> equalsFunction
    ) {
        this.allExpected = allExpected;
        this.isT = isT;
        this.givenToIdFunction = givenToIdFunction;
        this.expectedToIdFunction = expectedToIdFunction;
        this.equalsFunction = equalsFunction;
    }

    public ContainsItemsMatcher(
            List<E> allExpected,
            Predicate<Object> isT,
            Function<T, ID> givenToIdFunction,
            Function<E, ID> expectedToIdFunction
    ) {
        this(allExpected, isT, givenToIdFunction, expectedToIdFunction, Objects::equals);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object o) {
        if (o instanceof Iterable<?> iterable) {
            checkExpected:
            for (final E expected : this.allExpected) {
                for (final Object item : iterable) {
                    if (
                            this.isT.test(item) && this.equalsFunction.test(
                                    this.givenToIdFunction.apply((T) item),
                                    this.expectedToIdFunction.apply(expected)
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
