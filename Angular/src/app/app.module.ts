import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavigationComponent } from './navigation/navigation.component';
import { NewRecipeComponent } from './new-recipe/new-recipe.component';
import { RecipesComponent } from './recipes/recipes.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { RecipeComponent } from './recipe/recipe.component';
import { HomeComponent } from './home/home.component';
import { UpdateRecipeComponent } from './update-recipe/update-recipe.component';
import { SearchRecipeComponent } from './search-recipe/search-recipe.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';

import { httpInterceptorProviders } from './auth/auth-interceptor';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    NewRecipeComponent,
    RecipesComponent,
    NotFoundComponent,
    RecipeComponent,
    HomeComponent,
    UpdateRecipeComponent,
    SearchRecipeComponent,
    LoginComponent,
    RegisterComponent
  ],
    imports: [
        BrowserModule,
        HttpClientModule,
        FormsModule,
        AppRoutingModule,
        ReactiveFormsModule
    ],
  providers: [httpInterceptorProviders, HomeComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
