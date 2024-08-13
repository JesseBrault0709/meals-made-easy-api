package app.mealsmadeeasy.api.recipe.spec;

import app.mealsmadeeasy.api.image.Image;
import org.jetbrains.annotations.Nullable;

public class RecipeUpdateSpec {

    private @Nullable String slug;
    private @Nullable String title;
    private @Nullable Integer preparationTime;
    private @Nullable Integer cookingTime;
    private @Nullable Integer totalTime;
    private @Nullable String rawText;
    private @Nullable Boolean isPublic;
    private @Nullable Image mainImage;

    public @Nullable String getSlug() {
        return this.slug;
    }

    public void setSlug(@Nullable String slug) {
        this.slug = slug;
    }

    public @Nullable String getTitle() {
        return this.title;
    }

    public void setTitle(@Nullable String title) {
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
