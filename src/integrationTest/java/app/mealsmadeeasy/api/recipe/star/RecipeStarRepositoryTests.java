package app.mealsmadeeasy.api.recipe.star;

import app.mealsmadeeasy.api.recipe.RecipeEntity;
import app.mealsmadeeasy.api.recipe.RecipeRepository;
import app.mealsmadeeasy.api.user.UserEntity;
import app.mealsmadeeasy.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
public class RecipeStarRepositoryTests {

    @Autowired
    private RecipeStarRepository recipeStarRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity getOwnerUser() {
        final UserEntity draft = UserEntity.getDefaultDraft();
        draft.setUsername("test-user");
        draft.setEmail("test-user@test.com");
        draft.setPassword("test");
        return this.userRepository.save(draft);
    }

    private RecipeEntity getTestRecipe(UserEntity owner) {
        final RecipeEntity recipeDraft = new RecipeEntity();
        recipeDraft.setCreated(LocalDateTime.now());
        recipeDraft.setSlug("test-recipe");
        recipeDraft.setOwner(owner);
        recipeDraft.setTitle("Test Recipe");
        recipeDraft.setRawText("Hello, World!");
        return this.recipeRepository.save(recipeDraft);
    }

    @Test
    @DirtiesContext
    public void returnsTrueIfStarer() {
        final UserEntity owner = this.getOwnerUser();
        final RecipeEntity recipe = this.getTestRecipe(owner);

        final RecipeStarEntity starDraft = new RecipeStarEntity();
        final RecipeStarId starId = new RecipeStarId();
        starId.setRecipeId(recipe.getId());
        starId.setOwnerUsername(owner.getUsername());
        starDraft.setId(starId);
        this.recipeStarRepository.save(starDraft);

        assertThat(
                this.recipeStarRepository.isStarer(
                        recipe.getOwner().getUsername(),
                        recipe.getSlug(),
                        owner.getUsername()
                ),
                is(true)
        );
    }

    @Test
    @DirtiesContext
    public void returnsFalseIfNotStarer() {
        final UserEntity owner = this.getOwnerUser();
        final RecipeEntity recipe = this.getTestRecipe(owner);
        assertThat(
                this.recipeStarRepository.isStarer(
                        recipe.getOwner().getUsername(),
                        recipe.getSlug(),
                        owner.getUsername()
                ),
                is(false)
        );
    }

}
