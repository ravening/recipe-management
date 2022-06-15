package com.ravening.controller.recipe;

import com.ravening.model.dto.RecipeDto;
import com.ravening.model.mappers.MapStructMapper;
import com.ravening.model.recipe.Recipe;
import com.ravening.service.impl.RecipeService;
import com.ravening.service.impl.RecipeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/recipes")
@CrossOrigin(origins = "http://localhost:4200")
public class RecipeController {

    private final RecipeService recipeServiceImpl;

    private final MapStructMapper mapper;

    @Autowired
    public RecipeController(RecipeServiceImpl recipeServiceImpl,
                            MapStructMapper mapStructMapper){
        this.recipeServiceImpl = recipeServiceImpl;
        this.mapper = mapStructMapper;
    }

    @PreAuthorize(value = "hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RecipeDto> getAllDto(){
        assert recipeServiceImpl != null;
        List<RecipeDto> recipeDtoList;

        List<Recipe> recipes = recipeServiceImpl.findAll();

        recipeDtoList = recipes.stream()
                .map(mapper::recipeToRecipeDto)
                .collect(Collectors.toList());

        return recipeDtoList;
    }

    @PreAuthorize(value = "hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(value = "/category/{categoryName}")
    public List<RecipeDto> getAllByCategoryDto(@PathVariable String categoryName){
        List<Recipe> recipes = recipeServiceImpl.findAllByRecipeCategory(categoryName);

        return mapper.recipesToDtos(recipes);
    }

    @PreAuthorize(value = "hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(value = "/name/{name}")
    public ResponseEntity<List<RecipeDto>> getAllByNameDto(@PathVariable String name) {
        List<RecipeDto> list = mapper.recipesToDtos(recipeServiceImpl.findAllByRecipeName(name));
        if (list.size() > 0) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize(value = "hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<RecipeDto> postRecipeDto(@Valid  @RequestBody RecipeDto recipeDto){
        return new ResponseEntity<>(mapper
                .recipeToRecipeDto(recipeServiceImpl
                        .createRecipe(mapper
                            .recipeDtoToRecipe(recipeDto))), HttpStatus.CREATED);
    }

    @PreAuthorize(value = "hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteRecipe(@PathVariable long id){
        boolean status = recipeServiceImpl.deleteRecipeById(id);
        return new ResponseEntity(status ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PreAuthorize(value = "hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<RecipeDto> getRecipeDto(@PathVariable long id){
        Optional<Recipe> optionalRecipe = recipeServiceImpl.findById(id);
        return optionalRecipe.map(recipe ->
                        new ResponseEntity<>(mapper.recipeToRecipeDto(recipe), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize(value = "hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping(value = "/date/{date}")
    public List<RecipeDto> getRecipeByCreationDateDto(@PathVariable String date){
        return mapper.recipesToDtos(recipeServiceImpl.findRecipesByCreationDate(date));
    }

    @PreAuthorize(value = "hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<RecipeDto> updateRecipeDto(@Valid @RequestBody RecipeDto recipeDto, @PathVariable long id) {
        Recipe recipe = mapper.recipeDtoToRecipe(recipeDto);
        recipe = recipeServiceImpl.updateRecipe(recipe, id);

        if (recipe != null) {
            return new ResponseEntity<>(mapper.recipeToRecipeDto(recipe), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize(value = "hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/all")
    public ResponseEntity deleteAllRecipes() {
        recipeServiceImpl.deleteAllRecipes();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
