package app.mealsmadeeasy.api.recipe.spec;

import app.mealsmadeeasy.api.image.Image;
import org.jetbrains.annotations.Nullable;

public class RecipeCreateSpec {

    private String slug;
    private String title;
    private String rawText;
    private boolean isPublic;
    private @Nullable Image mainImage;

    public String getSlug() {
        return this.slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRawText() {
        return this.rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public @Nullable Image getMainImage() {
        return this.mainImage;
    }

    public void setMainImage(@Nullable Image mainImage) {
        this.mainImage = mainImage;
    }

}
