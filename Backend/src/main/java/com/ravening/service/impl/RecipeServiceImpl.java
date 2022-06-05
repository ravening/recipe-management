package com.ravening.service.impl;

import com.ravening.model.recipe.Ingredient;
import com.ravening.model.recipe.Recipe;
import com.ravening.model.recipe.RecipeCategory;
import com.ravening.repository.recipe.IngredientRepository;
import com.ravening.repository.recipe.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    private RecipeRepository recipeRepository;
    private IngredientRepository ingredientRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository,
                             IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Find recipe by category name
     * @param category
     * @return
     */
    public RecipeCategory findCategory(String category) {
        RecipeCategory recipeCategory = RecipeCategory.STARTER;
        if(category.equals("1"))
            recipeCategory = RecipeCategory.MAIN_COURSE;
        if(category.equals("2"))
            recipeCategory = RecipeCategory.DESSERT;

        return recipeCategory;
    }

    /**
     * Creates a new recipe
     * @param recipe
     * @return
     */
    public Recipe newRecipe(Recipe recipe) {
        List<Ingredient> ingredients = recipe.getIngredientsList();
        if (ingredients.size() > 0) {
            ingredientRepository.saveAll(ingredients);
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        recipe.setCreatedAt(sdf.format(date));
        recipeRepository.save(recipe);

        return recipe;
    }

    public Recipe getRecipe(long id) {
        Recipe recipeToSave = recipeRepository.findById(id).orElse(null);
        Recipe recipeToReturn = new Recipe();

        if (recipeToSave != null) {
            recipeToReturn.setRecipeId(recipeToSave.getRecipeId());
            copyRecipe(recipeToReturn, recipeToSave);

            recipeRepository.save(recipeToSave);

            return recipeToReturn;
        }
        return null;
    }

    /**
     * Function to copy data from old to new object
     * @param to
     * @param from
     */
    private void copyRecipe(Recipe to, Recipe from) {
        to.setRecipeName(from.getRecipeName());
        to.setRecipeCategory(from.getRecipeCategory());
        to.setIngredientsList(from.getIngredientsList());
        to.setInstructions(from.getInstructions());
        to.setSuggestions(from.getSuggestions());
        to.setServings(from.getServings());
        to.setCreatedAt(from.getCreatedAt());
        to.setVegetarian(from.isVegetarian());
    }

    @Override
    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    @Override
    public List<Recipe> findAllByRecipeCategory(String recipeCategory) {
        return recipeRepository
                .findAllByRecipeCategory(findCategory(recipeCategory));
    }

    @Override
    public List<Recipe> findAllByRecipeName(String name) {
        return recipeRepository.findAllByRecipeNameContainingIgnoreCase(name);
    }

    @Override
    public Recipe createRecipe(Recipe recipe) {
        return newRecipe(recipe);
    }

    @Override
    public Optional<Recipe> findById(Long id) {
        return recipeRepository.findById(id);
    }

    @Override
    public boolean deleteRecipeById(Long id) {
        if (findById(id).isPresent()) {
            recipeRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    public List<Recipe> findRecipesByCreationDate(String date) {
        if (date.contains("T")) {
            date = getDateForFroentEndFormat(date);
        }

        return recipeRepository.findAllByCreatedAtEquals(date);
    }

    @Override
    public Recipe updateRecipe(Recipe recipe, Long id) {
        Optional<Recipe> recipeToCheck = recipeRepository.findById(id);

        if (recipeToCheck.isPresent()) {
            Recipe recipeToSave = recipeToCheck.get();
            copyRecipe(recipeToSave, recipe);

            ingredientRepository.saveAll(recipeToSave.getIngredientsList());
            recipeRepository.save(recipeToSave);
            return recipeToSave;
        } else {
            return null;
        }
    }

    /**
     * Function to process the date string passed from UI in the form of
     * 2022-06-23T11:12
     * @param date
     * @return
     */
    private String getDateForFroentEndFormat(String date) {
        String[] dateArray = date.split("T");
        String[] year = dateArray[0].split("-");
        date = year[2] + "-" + year[1] + "-" + year[0] + " " + dateArray[1];

        return date;
    }
}
