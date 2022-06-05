import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {Recipe} from '../model/recipe';
import {RecipeCategory} from '../model/recipe-category';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private BASE_URL = 'http://localhost:8080/api/recipes';

  constructor(private http: HttpClient) { }

  addRecipe(recipe: Recipe): Observable<any> {
    const url = `${this.BASE_URL}`;
    return this.http.post(url, recipe, httpOptions);
  }

  getRecipes(): Observable<Recipe[]> {
    const url = `${this.BASE_URL}`;
    return this.http.get<Recipe[]>(url, httpOptions);
  }

  getRecipesByCategory(recipeCategory: RecipeCategory): Observable<Recipe[]> {
    const url = `${this.BASE_URL}/category/${recipeCategory}`;
    return this.http.get<Recipe[]>(url, httpOptions);
  }

  getRecipe(id: number): Observable<Recipe> {
    const url = `${this.BASE_URL}/${id}`;
    return this.http.get<Recipe>(url, httpOptions);
  }

  updateRecipe(recipe: Recipe, id: number): Observable<any> {
    const url = `${this.BASE_URL}/${id}`;
    return this.http.put(url, recipe, httpOptions);
  }

  deleteRecipe(id: number): Observable<any> {
    const url = `${this.BASE_URL}/${id}`;
    return this.http.delete(url, httpOptions);
  }

  searchRecipes(term: string): Observable<Recipe[]> {
    const url = `${this.BASE_URL}/name/${term}`;
    if (!term.trim()) {
      return of([]);
    }
    return this.http.get<Recipe[]>(url, httpOptions);
  }

  searchRecipesByCreationDate(date: string): Observable<Recipe[]> {
    const url = `${this.BASE_URL}/date/${date}`;
    if (!date.trim()) {
      return of([]);
    }
    return this.http.get<Recipe[]>(url, httpOptions);
  }
}
