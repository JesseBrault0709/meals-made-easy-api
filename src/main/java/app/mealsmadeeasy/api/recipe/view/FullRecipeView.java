package app.mealsmadeeasy.api.recipe.view;

import app.mealsmadeeasy.api.image.view.ImageView;
import app.mealsmadeeasy.api.recipe.Recipe;
import app.mealsmadeeasy.api.user.view.UserInfoView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public class FullRecipeView {

    public static FullRecipeView from(
            Recipe recipe,
            String renderedText,
            boolean includeRawText,
            int starCount,
            int viewerCount,
            @Nullable ImageView mainImage
    ) {
        final FullRecipeView view = new FullRecipeView();
        view.setId(recipe.getId());
        view.setCreated(recipe.getCreated());
        view.setModified(recipe.getModified());
        view.setSlug(recipe.getSlug());
        view.setTitle(recipe.getTitle());
        view.setPreparationTime(recipe.getPreparationTime());
        view.setCookingTime(recipe.getCookingTime());
        view.setTotalTime(recipe.getTotalTime());
        view.setText(renderedText);
        if (includeRawText) {
            view.setRawText(recipe.getRawText());
        }
        view.setOwner(UserInfoView.from(recipe.getOwner()));
        view.setStarCount(starCount);
        view.setViewerCount(viewerCount);
        view.setMainImage(mainImage);
        view.setIsPublic(recipe.isPublic());
        return view;
    }

    private long id;
    private LocalDateTime created;
    private @Nullable LocalDateTime modified;
    private String slug;
    private String title;
    private @Nullable Integer preparationTime;
    private @Nullable Integer cookingTime;
    private @Nullable Integer totalTime;
    private String text;
    private @Nullable String rawText;
    private UserInfoView owner;
    private int starCount;
    private int viewerCount;
    private @Nullable ImageView mainImage;
    private boolean isPublic;

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

    public @Nullable LocalDateTime getModified() {
        return this.modified;
    }

    public void setModified(@Nullable LocalDateTime modified) {
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

    public @Nullable String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonInclude(Include.NON_NULL)
    public @Nullable String getRawText() {
        return this.rawText;
    }

    public void setRawText(@Nullable String rawText) {
        this.rawText = rawText;
    }

    public UserInfoView getOwner() {
        return this.owner;
    }

    public void setOwner(UserInfoView owner) {
        this.owner = owner;
    }

    public int getStarCount() {
        return this.starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public int getViewerCount() {
        return this.viewerCount;
    }

    public void setViewerCount(int viewerCount) {
        this.viewerCount = viewerCount;
    }

    public @Nullable ImageView getMainImage() {
        return this.mainImage;
    }

    public void setMainImage(@Nullable ImageView mainImage) {
        this.mainImage = mainImage;
    }

    public boolean getIsPublic() {
        return this.isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

}
