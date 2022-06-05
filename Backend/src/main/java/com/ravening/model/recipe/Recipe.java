package com.ravening.model.recipe;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "recipes")
@EnableJpaAuditing
@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Recipe implements Serializable {

    private static final long serialVersionUID = 5560221391479816650L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private long recipeId;

    @NotBlank
    @Column(columnDefinition = "VARCHAR(255)")
    private String recipeName;

    @Enumerated
    @Column(columnDefinition = "smallint")
    private RecipeCategory recipeCategory;

//    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
//    @JoinColumn(name = "recipe_id")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "recipe_ingredient",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredientsList = new ArrayList<>();

    @NotBlank
    @Column(columnDefinition = "LONGTEXT")
    private String instructions;

    @Column(columnDefinition = "LONGTEXT")
    private String suggestions;

    @Column(name = "servings")
    int servings;

    @NotNull
    @Column(name = "vegetarian")
    boolean vegetarian;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @Column(name = "created_at")
    String createdAt;

    @Column(name = "modified_date")
    @LastModifiedDate
    private Date modifiedDate;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "modified_by")
    @LastModifiedBy
    private String modifiedBy;

    public Recipe(){}

    public Recipe(String recipeName,
                  RecipeCategory recipeCategory,
                  String instructions,
                  String suggestions,
                  int servings,
                  boolean vegetarian) {
        this.recipeName = recipeName;
        this.recipeCategory = recipeCategory;
        this.instructions = instructions;
        this.suggestions = suggestions;
        this.servings = servings;
        this.vegetarian = vegetarian;

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        this.createdAt = sdf.format(date);
    }

    public void setIngredientsList(List<Ingredient> ingredientsList) {
        this.ingredientsList.clear();
        if(ingredientsList != null)
            this.ingredientsList.addAll(ingredientsList);
    }

    @Override
    public String toString() {
        return "Recipe{" +
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
}
