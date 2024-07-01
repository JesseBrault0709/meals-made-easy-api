package app.mealsmadeeasy.api.recipe;

import app.mealsmadeeasy.api.user.UserEntity;
import app.mealsmadeeasy.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RecipeRepositoryTests {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity getOwnerUser() {
        final UserEntity recipeUser = UserEntity.getDefaultDraft();
        recipeUser.setUsername("recipeUser");
        recipeUser.setEmail("recipe@user.com");
        recipeUser.setPassword("test");
        return this.userRepository.save(recipeUser);
    }

    private UserEntity getViewerUser() {
        final UserEntity viewerUser = UserEntity.getDefaultDraft();
        viewerUser.setUsername("recipeViewerUser");
        viewerUser.setEmail("recipe-viewer@user.com");
        viewerUser.setPassword("test");
        return this.userRepository.save(viewerUser);
    }

    @Test
    @DirtiesContext
    public void findsAllPublicRecipes() {
        final RecipeEntity publicRecipe = new RecipeEntity();
        publicRecipe.setPublic(true);
        publicRecipe.setOwner(this.getOwnerUser());
        publicRecipe.setTitle("Public Recipe");
        publicRecipe.setRawText("Hello, World!");
        this.recipeRepository.save(publicRecipe);

        final List<RecipeEntity> publicRecipes = this.recipeRepository.findAllByIsPublicIsTrue();
        assertThat(publicRecipes.size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    public void doesNotFindNonPublicRecipe() {
        final RecipeEntity nonPublicRecipe = new RecipeEntity();
        nonPublicRecipe.setOwner(this.getOwnerUser());
        nonPublicRecipe.setTitle("Non-Public Recipe");
        nonPublicRecipe.setRawText("Hello, World!");
        this.recipeRepository.save(nonPublicRecipe);

        final List<RecipeEntity> publicRecipes = this.recipeRepository.findAllByIsPublicIsTrue();
        assertThat(publicRecipes.size()).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    public void findsAllForViewer() {
        final RecipeEntity recipe = new RecipeEntity();
        recipe.setOwner(this.getOwnerUser());
        recipe.setTitle("Test Recipe");
        recipe.setRawText("Hello, World!");
        final RecipeEntity saved = this.recipeRepository.save(recipe);

        final UserEntity viewer = this.getViewerUser();
        final Set<UserEntity> viewers = new HashSet<>(recipe.getViewerEntities());
        viewers.add(viewer);
        saved.setViewers(viewers);

        this.recipeRepository.save(saved);

        final List<RecipeEntity> viewable = this.recipeRepository.findAllByViewersContaining(viewer);
        assertThat(viewable.size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    public void doesNotIncludeNonViewable() {
        final RecipeEntity recipe = new RecipeEntity();
        recipe.setOwner(this.getOwnerUser());
        recipe.setTitle("Test Recipe");
        recipe.setRawText("Hello, World!");
        this.recipeRepository.save(recipe);

        final UserEntity viewer = this.getViewerUser();
        final List<RecipeEntity> viewable = this.recipeRepository.findAllByViewersContaining(viewer);
        assertThat(viewable.size()).isEqualTo(0);
    }

}
