import { Component, OnInit } from '@angular/core';
import {ApiService} from '../services/api.service';
import {Observable, Subject} from 'rxjs';
import {Recipe} from '../model/recipe';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs/operators';
import {FormControl, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-search-recipe',
  templateUrl: './search-recipe.component.html',
  styleUrls: ['./search-recipe.component.css']
})
export class SearchRecipeComponent implements OnInit {

  form = new FormGroup({
    name : new FormControl()
  });
  recipes$: Observable<Recipe[]>;

  newRecipes$: Observable<Recipe[]>;

  private searchTerms = new Subject<string>();

  private searchDate = new Subject<string>();

  constructor(private apiService: ApiService) { }

  search(term: string): void {
    this.searchTerms.next(term);
  }

  searchCreationDate(term: string): void {
    this.searchDate.next(term);
  }

  ngOnInit(): void {
    this.recipes$ = this.searchTerms.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((term: string) => this.apiService.searchRecipes(term)),
    );

    this.newRecipes$ = this.searchDate.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((term: string) => this.apiService.searchRecipesByCreationDate(term)),
    );
  }
}
