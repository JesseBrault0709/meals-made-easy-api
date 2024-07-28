package app.mealsmadeeasy.api.recipe.spec;

import app.mealsmadeeasy.api.image.Image;
import org.jetbrains.annotations.Nullable;

public class RecipeUpdateSpec {

    private @Nullable String title;
    private @Nullable String rawText;
    private @Nullable Boolean isPublic;
    private @Nullable Image mainImage;

    public @Nullable String getTitle() {
        return this.title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    public @Nullable String getRawText() {
        return this.rawText;
    }

    public void setRawText(@Nullable String rawText) {
        this.rawText = rawText;
    }

    public @Nullable Boolean getPublic() {
        return this.isPublic;
    }

    public void setPublic(@Nullable Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public @Nullable Image getMainImage() {
        return this.mainImage;
    }

    public void setMainImage(@Nullable Image mainImage) {
        this.mainImage = mainImage;
    }

}
