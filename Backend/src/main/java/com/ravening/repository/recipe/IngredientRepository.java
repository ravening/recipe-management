package com.ravening.repository.recipe;

import com.ravening.model.recipe.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Ingredient findByIngredientNameEqualsIgnoreCase(String name);
}
