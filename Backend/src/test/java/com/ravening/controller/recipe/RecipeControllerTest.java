package com.ravening.controller.recipe;

import com.ravening.model.dto.RecipeDto;
import com.ravening.model.mappers.MapStructMapper;
import com.ravening.model.recipe.Ingredient;
import com.ravening.model.recipe.Recipe;
import com.ravening.model.recipe.RecipeCategory;
import com.ravening.repository.recipe.RecipeRepository;
import com.ravening.service.impl.RecipeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@WebMvcTest
public class RecipeControllerTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeServiceImpl recipeServiceImpl;

    @Mock
    private MapStructMapper mapper;

    @InjectMocks
    private RecipeController instance;

    @Spy
    List<Recipe> recipes = new ArrayList<>();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        recipes =  getRecipeList();
    }

    @Test
    public void testGetAll() {
        when(recipeServiceImpl.findAll()).thenReturn(recipes);

        instance.getAllDto();

        verify(recipeServiceImpl, atLeastOnce()).findAll();
    }

    @Test
    public void testGetAllByCategory() {
        Recipe recipe = recipes.get(0);

        when(recipeServiceImpl.findCategory(anyString())).thenReturn(recipe.getRecipeCategory());
        when(recipeRepository.findAllByRecipeCategory(recipeServiceImpl.findCategory(anyString()))).thenReturn(recipes);

        instance.getAllByCategoryDto(anyString());

        verify(recipeServiceImpl, atLeastOnce()).findCategory(anyString());
    }

    @Test
    public void testGetAllByName() {
        when(recipeRepository.findAllByRecipeNameContainingIgnoreCase(anyString())).thenReturn(recipes);

        instance.getAllByNameDto(anyString());

        verify(recipeServiceImpl, atLeastOnce()).findAllByRecipeName(anyString());
    }

    @Test
    public void testPostRecipe() {
        Recipe recipe = recipes.get(0);
        RecipeDto recipeDto = mapper.recipeToRecipeDto(recipe);

        when(mapper.recipeDtoToRecipe(any())).thenReturn(recipe);
        when(recipeServiceImpl.createRecipe(recipe)).thenCallRealMethod();
        when(recipeServiceImpl.newRecipe(recipe)).thenReturn(recipe);

        instance.postRecipeDto(recipeDto);

        verify(recipeServiceImpl, atLeastOnce()).newRecipe(recipe);
    }

    @Test
    public void testDeleteRecipeWhenPresent() {
        Recipe recipe = recipes.get(0);

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));
        when(recipeServiceImpl.deleteRecipeById(anyLong())).thenReturn(recipe != null);
        doNothing().when(recipeRepository).deleteById(anyLong());

        assertEquals(instance.deleteRecipe(anyLong()), new ResponseEntity(HttpStatus.OK));

        verify(recipeServiceImpl, atLeastOnce()).deleteRecipeById(anyLong());
    }

    @Test
    public void testDeleteRecipeWhenNotPresent() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertEquals(instance.deleteRecipe(anyLong()), new ResponseEntity(HttpStatus.NOT_FOUND));

        verify(recipeRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testGetRecipeWhenPresent() {
        Recipe recipe = recipes.get(0);
        RecipeDto recipeDto = mapper.recipeToRecipeDto(recipe);

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));
        when(recipeServiceImpl.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));

        assertEquals(instance.getRecipeDto(anyLong()), new ResponseEntity<>(recipeDto, HttpStatus.OK));

        verify(recipeServiceImpl, atLeastOnce()).findById(anyLong());
    }

    @Test
    public void testGetRecipeWhenNotPresent() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertEquals(instance.getRecipeDto(anyLong()), new ResponseEntity<>(HttpStatus.NOT_FOUND));

        verify(recipeServiceImpl, never()).getRecipe(anyLong());
    }

    private List<Recipe> getRecipeList() {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(0);
        recipe.setRecipeName("testName");
        recipe.setRecipeCategory(RecipeCategory.DESSERT);
        recipe.setInstructions("testInstructions");
        recipe.setSuggestions("testSuggestions");
        recipe.setServings(10);
        recipe.setVegetarian(true);
        recipe.getIngredientsList().add(new Ingredient("testIngredientName", "testIngredientQuantity"));

        return new ArrayList<>(List.of(recipe));
    }
}
