package com.ravening.dbseeder;

import com.ravening.model.recipe.Ingredient;
import com.ravening.model.recipe.Recipe;
import com.ravening.model.recipe.RecipeCategory;
import com.ravening.model.user.Role;
import com.ravening.model.user.RoleName;
import com.ravening.model.user.User;
import com.ravening.repository.recipe.IngredientRepository;
import com.ravening.repository.recipe.RecipeRepository;
import com.ravening.repository.user.RoleRepository;
import com.ravening.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


@Component
public class DatabaseSeeder implements CommandLineRunner {

    private String[] recipeNames = {
            "Pizza",
            "Noodles",
            "Pasta",
            "Fries",
            "Garlic bread",
            "Chips",
            "Ice cream",
            "Smoothie",
            "Milk shake"
    };

    private RecipeCategory[] recipeCategories = {
            RecipeCategory.MAIN_COURSE,
            RecipeCategory.MAIN_COURSE,
            RecipeCategory.MAIN_COURSE,
            RecipeCategory.STARTER,
            RecipeCategory.STARTER,
            RecipeCategory.STARTER,
            RecipeCategory.DESSERT,
            RecipeCategory.DESSERT,
            RecipeCategory.DESSERT
    };

    private String[][] ingredientNames = {
            {"wheat flour", "tomato sauce", "cheese", "vegetables", "chilli flakes"},
            {"soy sauce", "salt", "olive oil", "oregano"},
            {"soy sauce", "cheese", "salt", "vegetables"},
            {"potato", "cooking oil", "salt", "chilli flakes", "ketchup", "mayonnaise"},
            {"Bread", "Garlic", "chilli flakes", "oregano"},
            {"potato", "chilli powder", "ketchup", "salt"},
            {"ice cubes", "milk", "yogurt", "sugar", "chocolate"},
            {"fruits", "sugar", "milk"},
            {"milk", "sugar", "fruits", "saffron"},
    };

    private String[][] ingredientQuantities = {
            {"800 g", "200 g", "1 level", "2x7 g", "1 tablespoon"},
            {"1 teaspoon", "1 bunch", "", "1x400 g"},
            {"2 teaspoons", "50 g", "450 g", "2 tablespoons"},
            {"1 small", "6 cloves", "3 sprigs", "1 teaspoon", "1 small", "125 g"},
            {"1 large", "6 cm piece", "6 large sticks", "2"},
            {"750 g", "1 spoon", "2", "120 g"},
            {"12", "25 g", "120 g", "200 g", "25 g"},
            {"500 g", "500 g", "1 litre",},
            { "50 ml", "50 g", "4", "1 pinch"}
    };

    private String[] instructions = {
            "Cook in oven",
            "Cook in pan",
            "cook in pan",
            "Fry with oil",
            "Cook with bread",
            "Fry with oil",
            "Blend it",
            "Crush it",
            "Stir it"
    };

    private String[] suggestions = {
            "Prepare the dough, chop the vegetables",
            "Cook with raw noodles",
            "Cook with raw pasta",
            "Use hard potatoes",
            "Use lot of garlic with bread",
            "Chop the potatoes",
            "Use vanilla flavoring",
            "Crush the ice cubes",
            "Stir the milk thoroughly"
    };

    private final RecipeRepository recipeRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final IngredientRepository ingredientRepository;
    @Autowired
    public DatabaseSeeder(RecipeRepository recipeRepository,
                          RoleRepository roleRepository,
                          UserRepository userRepository,
                          PasswordEncoder encoder,
                          IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        Create a dummy user first
         */
        User user = new User("test", "username", "test1@test.test", encoder.encode("password"));
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByRoleName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Failed: User role not found!"));
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);

        // Load the db with predefined data
        for (int i = 0; i < recipeNames.length; i++) {

            Recipe recipe = new Recipe(recipeNames[i],
                                        recipeCategories[i],
                                        instructions[i],
                                        suggestions[i],
                                        5,
                            true);

            for (int j = 0; j < ingredientNames[i].length; j++) {
                Ingredient ingredient  =new Ingredient(ingredientNames[i][j], ingredientQuantities[i][j]);
                this.ingredientRepository.save(ingredient);
                recipe.getIngredientsList().add(ingredient);
            }

            recipeRepository.save(recipe);
        }
    }
}
