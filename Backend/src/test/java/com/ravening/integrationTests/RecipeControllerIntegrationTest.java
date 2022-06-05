package com.ravening.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ravening.message.request.LoginForm;
import com.ravening.message.request.SignUpForm;
import com.ravening.model.dto.RecipeDto;
import com.ravening.model.recipe.Ingredient;
import com.ravening.model.recipe.RecipeCategory;
import com.ravening.repository.recipe.RecipeRepository;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RecipeControllerIntegrationTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String INVALID_USERNAME = "invalid_username";
    private static final String INVALID_PASSWORD = "invalid_password";

    private MockMvc mockMvc;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    static DataSource dataSource;

    @Before
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @BeforeAll
    static void loadDb() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("data.sql"));
        }
    }

    @Test
    @DisplayName("Login without credentials")
    public void when_noCredentials_expect_badRequestStatus() throws Exception {
        mockMvc.perform(post("/api/auth/login")).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Sign up as existing username")
    public void when_AsignupAsExistingUser_ThenBadRequestStatus() throws Exception {
        SignUpForm signUpForm = new SignUpForm(USERNAME, USERNAME, "test@test.com", PASSWORD);

        mockMvc.perform(post("/api/auth/signup")
                        .content(json(signUpForm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Signup using existing email address")
    public void when_AsignupAsExistingEmail_ThenBadRequest() throws Exception {
        SignUpForm signUpForm = new SignUpForm(USERNAME, USERNAME, "test@test.com", PASSWORD);

        // User with same email was created in above step
        mockMvc.perform(post("/api/auth/signup")
                        .content(json(signUpForm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login as invalid username")
    public void when_wrongUsername_expect_unauthorizedStatus() throws Exception {
        LoginForm loginForm = new LoginForm(INVALID_USERNAME, PASSWORD);
        mockMvc.perform(post("/api/auth/login")
                .content(json(loginForm))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login with wrong password")
    public void when_wrongPassword_expect_unauthorizedStatus() throws Exception {
        LoginForm loginForm = new LoginForm(USERNAME, INVALID_PASSWORD);
        mockMvc.perform(post("/api/auth/login")
                .content(json(loginForm))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Wrong user and password")
    public void when_wrongUsernameAndPassword_expect_unauthorizedStatus() throws Exception {
        LoginForm loginForm = new LoginForm(INVALID_USERNAME, INVALID_PASSWORD);
        mockMvc.perform(post("/api/auth/login")
                .content(json(loginForm))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login with proper credentials")
    public void when_CorrectLoginParameters_expect_okStatus() throws Exception {
        mockMvc.perform(get("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Get all recipes")
    public void when_AllRecipesRequested_expect_okStatus() throws Exception {
        // Get all recipes persisted so far
        MockHttpServletResponse response = mockMvc.perform(get("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(jsonPath("$", hasSize((int) recipeRepository.count())))
                .andReturn()
                .getResponse();

        List<RecipeDto> recipeDtoList = mapper.readValue(response.getContentAsString(), List.class);
        assertTrue(recipeDtoList.size() > 0);
    }

    @Test
    @DisplayName("Get recipe by id")
    public void when_RequestRecipeById_ThenExpectOkStatus() throws Exception {
        mockMvc.perform(get("/api/recipes/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$.recipeName", is("starter")));
    }

    @Test
    @DisplayName("Get all dessert category recipes")
    public void when_RequestRecipeByCategoryDessert_ThenExpectOkStatus() throws Exception {
        mockMvc.perform(get("/api/recipes/category/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    @DisplayName("Get recipe by its name")
    public void when_RequestRecipeByName_ThenExpectOkStatus() throws Exception {
        mockMvc.perform(get("/api/recipes/name/main course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Get recipe by non existing name")
    public void when_RequestRecipeByInvalidName_ThenExpectOkStatus() throws Exception {
        mockMvc.perform(get("/api/recipes/name/doesnotexist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("Get recipe by its creation date")
    public void when_RequestRecipeByCreationDate_ThenExpectOkStatus() throws Exception {
        List<RecipeDto> recipeDtoList =
                mapper.readValue(mockMvc.perform(get("/api/recipes/date/05-06-2022 12:12")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), List.class);

        assertNotNull(recipeDtoList);
        assertEquals(1, recipeDtoList.size());
    }

    @Test
    @DisplayName("Get recipe by its creation date in different date format")
    public void when_RequestRecipeByCreationDateFormat_ThenExpectOkStatus() throws Exception {
        // UI sends the date in YYYY-MM-DDTHH:mm format
        List<RecipeDto> recipeDtoList =
                mapper.readValue(mockMvc.perform(get("/api/recipes/date/2022-06-05T12:12")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), List.class);

        assertNotNull(recipeDtoList);
        assertEquals(1, recipeDtoList.size());
    }

    @Test
    @DisplayName("Create a new recipe")
    public void when_postNewRecipe_thenExpectCreatedStatus() throws Exception {
        //
        // given
        //
        RecipeDto recipeDto = getRecipeDto();
        recipeDto.setRecipeCategory(RecipeCategory.STARTER);
        recipeDto.setRecipeName("Test recipe");
        recipeDto.setInstructions("dont do anything");

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(recipeDto))
                        .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Create recipe with missing parameters")
    public void whenPostInvalidRecipe_ThenBadRequestStatus() throws Exception {
        RecipeDto recipeDto = getRecipeDto();
        recipeDto.setRecipeCategory(RecipeCategory.STARTER);

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(recipeDto))
                        .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Try to delete non existing recipe")
    public void when_DeleteNonExistingRecipe_ThenNotFoundStatus() throws Exception {
        RecipeDto recipeDto = getRecipeDto();
        recipeDto.setRecipeCategory(RecipeCategory.STARTER);

        mockMvc.perform(delete("/api/recipes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update existing recipe")
    public void when_UpdateExistingRecipe_ThenOkStatus() throws Exception {
        RecipeDto recipeDto = getRecipeDto();
        recipeDto.setRecipeCategory(RecipeCategory.STARTER);
        recipeDto.setRecipeName("before update");
        recipeDto.setInstructions("before update");
        recipeDto.setVegetarian(true);
        recipeDto.setServings(5);

        RecipeDto newRecipeDto =
                mapper.readValue(mockMvc.perform(post("/api/recipes")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json(recipeDto))
                                     .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                                    .andDo(print())
                                    .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), RecipeDto.class);

        assertNotNull(newRecipeDto);

        assertTrue(newRecipeDto.isVegetarian());

        recipeDto.setVegetarian(false);

        newRecipeDto =
                mapper.readValue(mockMvc.perform(put("/api/recipes/" + newRecipeDto.getRecipeId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(recipeDto))
                                .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), RecipeDto.class);

        assertNotNull(newRecipeDto);
        assertFalse(newRecipeDto.isVegetarian());

    }


    @Test
    @DisplayName("Update non existing recipe")
    public void when_UpdateNonExistingRecipe_ThenNotFoundStatus() throws Exception {
        RecipeDto recipeDto = getRecipeDto();
        recipeDto.setRecipeCategory(RecipeCategory.STARTER);
        recipeDto.setRecipeCategory(RecipeCategory.STARTER);
        recipeDto.setRecipeName("before update");
        recipeDto.setInstructions("before update");
        recipeDto.setVegetarian(true);
        recipeDto.setServings(5);

        mockMvc.perform(put("/api/recipes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(recipeDto))
                        .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update ingredients list of recipe")
    public void when_updateIngredientsList_ThenOkStatus() throws Exception {

        // create new recipe
        RecipeDto recipeDto = getRecipeDto();
        recipeDto.setRecipeCategory(RecipeCategory.STARTER);
        recipeDto.setRecipeCategory(RecipeCategory.STARTER);
        recipeDto.setRecipeName("before update");
        recipeDto.setInstructions("before update");
        recipeDto.setVegetarian(true);
        recipeDto.setServings(5);

        // add 1 ingredient to it
        recipeDto.setIngredientsList(List.of(Ingredient
                .builder()
                .ingredientName("ingredient1")
                .quantity("1").build()));

        RecipeDto newRecipeDto = mapper.readValue(mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(recipeDto))
                        .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(), RecipeDto.class);

        assertNotNull(newRecipeDto);
        assertEquals(1, newRecipeDto.getIngredientsList().size());
        assertEquals("ingredient1", newRecipeDto.getIngredientsList().get(0).getIngredientName());

        // update ingredients list with one more ingredient
        newRecipeDto.getIngredientsList().add(Ingredient.builder()
                .ingredientName("ingredient2")
                .quantity("2").build());


        newRecipeDto =
                mapper.readValue(mockMvc.perform(put("/api/recipes/" + newRecipeDto.getRecipeId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(newRecipeDto))
                                .header("Authorization", "Bearer " + getAuthenticationToken(USERNAME, PASSWORD)))
                        .andDo(print())
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), RecipeDto.class);

        assertNotNull(newRecipeDto);
        assertEquals(2, newRecipeDto.getIngredientsList().size());
    }

    private String getAuthenticationToken(String username, String password) throws Exception {
        LoginForm loginForm = new LoginForm(username, password);
        MvcResult response = mockMvc.perform(post("/api/auth/login")
                .content(json(loginForm))
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        return conversionResultIntoToken(response.getResponse().getContentAsString());
    }

    private String conversionResultIntoToken(String token) {
        if (token.length() == 0) {
            return "";
        }

        return token
                .replaceAll("[{:}]", "")
                .replaceAll(" ", "")
                .replaceAll("\"", "")
                .substring(5);
    }

    private String json(Object o) throws IOException {
        ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return mapper.writeValueAsString(o);
    }

    private RecipeDto getRecipeDto() {
        return RecipeDto.builder().build();
    }
}
