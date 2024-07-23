package app.mealsmadeeasy.api.image;

import app.mealsmadeeasy.api.matchers.ContainsItemsMatcher;

import java.util.List;

public final class ContainsImagesMatcher extends ContainsItemsMatcher<Image, Long> {

    public static ContainsImagesMatcher containsImages(Image... expected) {
        return new ContainsImagesMatcher(expected);
    }

    private ContainsImagesMatcher(Image... allExpected) {
        super(List.of(allExpected), o -> o instanceof Image, Image::getId);
    }

}
