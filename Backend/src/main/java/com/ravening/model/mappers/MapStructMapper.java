package com.ravening.model.mappers;

import com.ravening.model.dto.IngredientsDto;
import com.ravening.model.dto.RecipeDto;
import com.ravening.model.recipe.Ingredient;
import com.ravening.model.recipe.Recipe;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface MapStructMapper {

    /**
     * Interface to map entities and DTO's
     * @param recipe
     * @return
     */
    RecipeDto recipeToRecipeDto(Recipe recipe);

    Recipe recipeDtoToRecipe(RecipeDto recipeDto);

    List<RecipeDto> recipesToDtos(List<Recipe> recipes);

    IngredientsDto ingredientToIngredientsDto(Ingredient ingredient);

    Ingredient ingredientsDtoToIngredient(IngredientsDto ingredientsDto);
}
