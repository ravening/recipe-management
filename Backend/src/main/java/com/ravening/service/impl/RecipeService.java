package com.ravening.service.impl;

import com.ravening.model.recipe.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeService {

    List<Recipe> findAll();

    List<Recipe> findAllByRecipeCategory(String recipeCategory);

    List<Recipe> findAllByRecipeName(String name);

    Recipe createRecipe(Recipe recipe);

    Optional<Recipe> findById(Long id);

    boolean deleteRecipeById(Long id);

    List<Recipe> findRecipesByCreationDate(String date);

    Recipe updateRecipe(Recipe recipe, Long id);

    void deleteAllRecipes();

    void saveRecipes(List<Recipe> recipes);
}
