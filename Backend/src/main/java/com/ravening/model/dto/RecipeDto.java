package com.ravening.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ravening.model.recipe.Ingredient;
import com.ravening.model.recipe.RecipeCategory;
import lombok.*;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecipeDto {
    @JsonProperty("recipeId")
    private Long recipeId;

    @NotBlank
    @JsonProperty("recipeName")
    private String recipeName;

    @Enumerated
    @JsonProperty("recipeCategory")
    private RecipeCategory recipeCategory;

    @JsonProperty("ingredientsList")
    private List<Ingredient> ingredientsList;

    @NotBlank
    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("suggestions")
    private String suggestions;

    @NotNull
    @JsonProperty("servings")
    int servings;

    @NotNull
    @JsonProperty("vegetarian")
    boolean vegetarian;

    @JsonProperty("createdAt")
    String createdAt;

    @Override
    public String toString() {
        return "RecipeDto{" +
                "recipeId=" + recipeId +
                ", recipeName='" + recipeName + '\'' +
                ", recipeCategory=" + recipeCategory +
                ", ingredientsList=" + ingredientsList +
                ", instructions='" + instructions + '\'' +
                ", suggestions='" + suggestions + '\'' +
                ", servings=" + servings +
                ", isVegetarian=" + vegetarian +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }
}
