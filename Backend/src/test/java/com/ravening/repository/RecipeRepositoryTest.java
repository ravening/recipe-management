package com.ravening.repository;

import com.ravening.model.recipe.Ingredient;
import com.ravening.model.recipe.Recipe;
import com.ravening.model.recipe.RecipeCategory;
import com.ravening.repository.recipe.IngredientRepository;
import com.ravening.repository.recipe.RecipeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecipeRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    IngredientRepository ingredientRepository;


    @BeforeAll
     public void persistData() {

        Ingredient banana = new Ingredient("Banana", "10");
        Ingredient salt = new Ingredient("Salt", "1 tea spoon");
        Ingredient sugar = new Ingredient("Sugar", "5 table spoons");
        Ingredient chilli = new Ingredient("Chilli powder", "2 spoons");
        Ingredient pepper = new Ingredient("Pepper", "5 pieces");
        Ingredient soySauce = new Ingredient("Soy sauce", "3 spoons");
        Ingredient saffron = new Ingredient("Saffron", "3 strands");

        List<Ingredient> ingredients = List.of(banana, salt, sugar, chilli, pepper, soySauce, saffron);
        ingredientRepository.saveAll(ingredients);
        ingredientRepository.flush();

        Recipe recipe = new Recipe("Banana split",
                RecipeCategory.DESSERT, "split the banana", "serve cold",
                5, true);
        recipe.setIngredientsList(List.of(banana, sugar, saffron));

        Recipe recipe2 = new Recipe("Sizzlers",
                RecipeCategory.MAIN_COURSE, "Cook in wok",
                "Tastes good when its sizzling", 10, false);
        recipe2.setIngredientsList(List.of(salt, soySauce, chilli, saffron, pepper));
        Recipe recipe3 = new Recipe("Panneer",
                RecipeCategory.STARTER, "Marinate with spices",
                "Add spices to enhance taste", 1, true);
        recipe3.setIngredientsList(List.of(salt, chilli, saffron, soySauce));

        List<Recipe> recipes = new ArrayList<>(List.of(recipe, recipe2, recipe3));
         recipe.setCreatedAt("05-06-2022 11:11");
         recipe2.setCreatedAt("05-06-2022 12:12");
         recipe3.setCreatedAt("05-06-2022 13:13");
         recipeRepository.saveAll(recipes);
         recipeRepository.flush();
    }


    @Test
    @DisplayName("Get All recipes")
    public void whenFindAll() {
         // given persisted data

        // when
        List<Recipe> result = recipeRepository.findAll();

        result.stream().forEach(r -> System.out.println(r.getRecipeName()));

        //then
        assertEquals(6, result.size());
    }

    @Test
    @DisplayName("Get all Starter recipes")
    public void whenFindAllStarterRecipes() {
        // given persisted data

        // when
        List<Recipe> starterRecipes = recipeRepository.findAllByRecipeCategory(RecipeCategory.STARTER);

        // then
        assertEquals(2, starterRecipes.size());
        assertFalse(starterRecipes.get(0).isVegetarian());
    }

    @Test
    @DisplayName("Get all non vegetarian recipes")
    public void whenFindAllNonVegetarianRecipes() {
        // given persisted data

        // when
        List<Recipe> nonVegetarianRecipe = recipeRepository.findAll()
                .stream()
                .filter(r -> ! r.isVegetarian())
                .collect(Collectors.toList());

        assertEquals(3, nonVegetarianRecipe.size());
    }

    @Test
    @DisplayName("Get Recipe by name")
    public void whenFindRecipeByNameThenReturnOne() {
        // given persisted data

        // when
        List<Recipe> recipes = recipeRepository.findAllByRecipeNameContainingIgnoreCase("panneer");

        // then
        assertEquals(1, recipes.size());
        assertEquals(RecipeCategory.STARTER, recipes.get(0).getRecipeCategory());
        assertEquals(1, recipes.get(0).getServings());
    }

    @Test
    @DisplayName("Get all recipes by creation date")
    public void whenFindRecipeByCreationDateThenReturnOne() {
        // given
        String creationDate = "05-06-2022 12:12";

        // when
        List<Recipe> recipes = recipeRepository.findAllByCreatedAtEquals(creationDate);

        // then

        assertEquals(2, recipes.size());
        assertEquals(RecipeCategory.MAIN_COURSE, recipes.get(0).getRecipeCategory());
        assertEquals("main course", recipes.get(0).getRecipeName().toLowerCase(Locale.ROOT));
        assertFalse(recipes.get(0).isVegetarian());
    }

    @Test
    @DisplayName("Get recipes by serving size")
    public void whenFindByServingSizeThenReturnOne() {
        // given
        int servingSize = 5;

        // when
        Optional<Recipe> recipes = recipeRepository
                .findAll()
                .stream()
                .filter(r -> r.getServings() == servingSize)
                .findFirst();

        // then

        assertTrue(recipes.isPresent());
        assertEquals(RecipeCategory.DESSERT, recipes.get().getRecipeCategory());
        assertTrue(recipes.get().isVegetarian());
    }

    @Test
    @DisplayName("Get Ingredient by name")
    public void whenFindByIngredientName_thenReturnOne() {
        // given
        String ingredientName = "salt";

        // when
        Ingredient ingredient = ingredientRepository.findByIngredientNameEqualsIgnoreCase(ingredientName);

        // then
        assertNotNull(ingredient);
        assertTrue(ingredient.getIngredientName().equalsIgnoreCase(ingredientName));
    }


    @Test
    @DisplayName("Get recipe names containing ingredient")
    public void shouldReturnRecipesContainingGivenIngredientName() {
        //
        // given
        //
        String ingredientName = "saffron";

        //
        // when
        //
        List<Recipe> recipes = recipeRepository.findAll()
                .stream()
                        .filter(r -> {
                            return r.getIngredientsList()
                                    .stream()
                                    .anyMatch(i -> i.getIngredientName().equalsIgnoreCase(ingredientName));
                        }).collect(Collectors.toList());

        //
        // then
        //
        assertEquals(3, recipes.size());
    }

    @Test
    @DisplayName("Create new recipe")
    public void shouldBeAbleToCreateNewRecipe() {
        //
        // given
        //
        Recipe recipe = new Recipe("Noodles", RecipeCategory.MAIN_COURSE, "Boil noodles", "serve hot", 2, true);

        //
        // when
        //
        recipeRepository.save(recipe);


        //
        // then
        //
        assertEquals(7, recipeRepository.findAll().size());
    }

    @Test
    @DisplayName("Delete a recipe by name")
    public void shouldDeleteRecipeByItsName() {
        //
        // given
        //
        String recipeName = "sizzlers";

        //
        // when
        //
        List<Recipe> recipes = recipeRepository.findAllByRecipeNameContainingIgnoreCase(recipeName);

        //
        // then
        //
        assertNotNull(recipes);
        assertEquals(1, recipes.size());

        recipeRepository.delete(recipes.get(0));

        recipes = recipeRepository.findAllByRecipeNameContainingIgnoreCase(recipeName);

        assertTrue(recipes.isEmpty());
    }

    @Test
    @DisplayName("Get recipes with non existing ingredient")
    public void whenNonExistingIngredient_thenReturnNone() {
        List<Recipe> recipes = recipeRepository.findAll();
        int count = recipes.stream()
                .mapToInt(r -> (int) r.getIngredientsList()
                        .stream()
                        .filter(i -> i.getIngredientName().equalsIgnoreCase("dummy"))
                        .count())
                .sum();
        assertEquals(0, count);
    }
}
