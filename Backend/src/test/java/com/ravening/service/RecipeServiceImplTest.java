package com.ravening.service;

import com.ravening.model.recipe.Ingredient;
import com.ravening.model.recipe.Recipe;
import com.ravening.model.recipe.RecipeCategory;
import com.ravening.repository.recipe.IngredientRepository;
import com.ravening.repository.recipe.RecipeRepository;
import com.ravening.service.impl.RecipeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    IngredientRepository ingredientRepository;

    @InjectMocks
    private RecipeServiceImpl instance;

    @Spy
    private List<Recipe> recipes = new ArrayList<>();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        recipes = getRecipeList();
    }

    @Test
    public void testNewRecipe() {
        Recipe recipe = recipes.get(0);

        when(recipeRepository.save(recipe)).thenReturn(recipe);

        instance.newRecipe(recipe);

        verify(recipeRepository, atLeastOnce()).save(recipe);
    }

    @Test
    public void testGetRecipe() {
        Recipe recipe = recipes.get(0);

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));
        when(recipeRepository.save(recipe)).thenReturn(recipe);

        Recipe recipeToReturn = instance.getRecipe(anyLong());
        assertEquals(recipeToReturn.getRecipeId(), 0);

        verify(recipeRepository, atLeastOnce()).findById(anyLong());
        verify(recipeRepository, atLeastOnce()).save(recipe);
    }

    @Test
    public void testGetAllRecipes() {
        when(recipeRepository.findAll()).thenReturn(getRecipeList());

        List<Recipe> recipeList = instance.findAll();

        assertEquals(1, recipeList.size());

        verify(recipeRepository, atLeastOnce()).findAll();
    }

    @Test
    public void testUpdateRecipe() {
        Recipe recipe = recipes.get(0);

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));
        when(recipeRepository.save(recipe)).thenReturn(recipe);

        assertEquals(instance.updateRecipe(recipe,anyLong()), recipe);

        verify(recipeRepository, atLeastOnce()).findById(anyLong());
        verify(recipeRepository, atLeastOnce()).save(recipe);
    }

    @Test
    public void testDeleteRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean status = instance.deleteRecipeById(anyLong());

        assertFalse(status);
        verify(recipeRepository, atLeastOnce()).findById(anyLong());
    }

    private List<Recipe> getRecipeList() {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(0);
        recipe.setRecipeName("testName");
        recipe.setRecipeCategory(RecipeCategory.DESSERT);
        recipe.setInstructions("testInstructions");
        recipe.setSuggestions("testSuggestions");
        recipe.setVegetarian(true);
        recipe.setServings(10);
        recipe.getIngredientsList().add(new Ingredient("testIngredientName", "testIngredientQuantity"));

        return new ArrayList<>(List.of(recipe));
    }
}
