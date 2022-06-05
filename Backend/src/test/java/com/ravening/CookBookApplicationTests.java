package com.ravening;

import com.ravening.controller.recipe.RecipeControllerTest;
import com.ravening.integrationTests.RecipeControllerIntegrationTest;
import com.ravening.repository.RecipeRepositoryTest;
import com.ravening.service.RecipeServiceImplTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RecipeControllerTest.class,
        RecipeRepositoryTest.class,
        RecipeControllerIntegrationTest.class,
        RecipeServiceImplTest.class
})
@SpringBootTest
public class CookBookApplicationTests {

    @Test
    public void contextLoads() { }
}
