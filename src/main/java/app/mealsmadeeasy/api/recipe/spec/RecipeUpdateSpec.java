package app.mealsmadeeasy.api.recipe.spec;

import app.mealsmadeeasy.api.image.Image;
import app.mealsmadeeasy.api.recipe.Recipe;
import org.jetbrains.annotations.Nullable;

// For now, we cannot change slug after creation.
// In the future, we may be able to have redirects from
// old slugs to new slugs.
public class RecipeUpdateSpec {

    public static class MainImageUpdateSpec {

        private String username;
        private String filename;

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFilename() {
            return this.filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

    }

    private String title;
    private @Nullable Integer preparationTime;
    private @Nullable Integer cookingTime;
    private @Nullable Integer totalTime;
    private String rawText;
    private boolean isPublic;
    private @Nullable MainImageUpdateSpec mainImage;

    public RecipeUpdateSpec() {}

    /**
     * Convenience constructor for testing purposes.
     *
     * @param recipe the Recipe to copy from
     */
    public RecipeUpdateSpec(Recipe recipe) {
        this.title = recipe.getTitle();
        this.preparationTime = recipe.getPreparationTime();
        this.cookingTime = recipe.getCookingTime();
        this.totalTime = recipe.getTotalTime();
        this.rawText = recipe.getRawText();
        this.isPublic = recipe.isPublic();
        final @Nullable Image mainImage = recipe.getMainImage();
        if (mainImage != null) {
            this.mainImage = new MainImageUpdateSpec();
            this.mainImage.setUsername(mainImage.getOwner().getUsername());
            this.mainImage.setFilename(mainImage.getUserFilename());
        }
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

    public boolean getIsPublic() {
        return this.isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public @Nullable MainImageUpdateSpec getMainImage() {
        return this.mainImage;
    }

    public void setMainImage(@Nullable MainImageUpdateSpec mainImage) {
        this.mainImage = mainImage;
    }

}
