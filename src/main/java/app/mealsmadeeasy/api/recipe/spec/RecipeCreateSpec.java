package app.mealsmadeeasy.api.recipe.spec;

import app.mealsmadeeasy.api.image.Image;
import org.jetbrains.annotations.Nullable;

public class RecipeCreateSpec {

    private String slug;
    private String title;
    private @Nullable Integer preparationTime;
    private @Nullable Integer cookingTime;
    private @Nullable Integer totalTime;
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

    public @Nullable Integer getPreparationTime() {
        return this.preparationTime;
    }

    public void setPreparationTime(@Nullable Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public @Nullable Integer getCookingTime() {
        return this.cookingTime;
    }

    public void setCookingTime(@Nullable Integer cookingTime) {
        this.cookingTime = cookingTime;
    }

    public @Nullable Integer getTotalTime() {
        return this.totalTime;
    }

    public void setTotalTime(@Nullable Integer totalTime) {
        this.totalTime = totalTime;
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
