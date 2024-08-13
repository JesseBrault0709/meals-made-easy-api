package app.mealsmadeeasy.api.recipe.view;

import app.mealsmadeeasy.api.image.view.ImageView;
import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.user.view.UserInfoView;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public final class RecipeInfoView {

    public static RecipeInfoView from(Recipe recipe, int starCount, @Nullable ImageView mainImage) {
        final RecipeInfoView view = new RecipeInfoView();
        view.setId(recipe.getId());
        view.setCreated(recipe.getCreated());
        view.setModified(recipe.getModified());
        view.setSlug(recipe.getSlug());
        view.setTitle(recipe.getTitle());
        view.setPreparationTime(recipe.getPreparationTime());
        view.setCookingTime(recipe.getCookingTime());
        view.setTotalTime(recipe.getTotalTime());
        view.setOwner(UserInfoView.from(recipe.getOwner()));
        view.setIsPublic(recipe.isPublic());
        view.setStarCount(starCount);
        view.setMainImage(mainImage);
        return view;
    }

    private long id;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String slug;
    private String title;
    private @Nullable Integer preparationTime;
    private @Nullable Integer cookingTime;
    private @Nullable Integer totalTime;
    private UserInfoView owner;
    private boolean isPublic;
    private int starCount;
    private @Nullable ImageView mainImage;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return this.modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

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

    public UserInfoView getOwner() {
        return this.owner;
    }

    public void setOwner(UserInfoView owner) {
        this.owner = owner;
    }

    public boolean getIsPublic() {
        return this.isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getStarCount() {
        return this.starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public @Nullable ImageView getMainImage() {
        return this.mainImage;
    }

    public void setMainImage(@Nullable ImageView mainImage) {
        this.mainImage = mainImage;
    }

}
