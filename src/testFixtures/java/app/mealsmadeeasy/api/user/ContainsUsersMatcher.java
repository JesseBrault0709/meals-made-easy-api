package app.mealsmadeeasy.api.user;

import app.mealsmadeeasy.api.matchers.ContainsItemsMatcher;

import java.util.List;

public class ContainsUsersMatcher extends ContainsItemsMatcher<User, Long> {

    public static ContainsUsersMatcher containsUsers(User... allExpected) {
        return new ContainsUsersMatcher(allExpected);
    }

    private ContainsUsersMatcher(User... allExpected) {
        super(List.of(allExpected), o -> o instanceof User, User::getId);
    }

}
