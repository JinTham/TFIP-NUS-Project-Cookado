import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Cuisine } from 'src/app/models/cuisine';
import { Recipe } from 'src/app/models/recipe';
import { CalendarService } from 'src/app/services/calendar.service';
import { RecipeService } from 'src/app/services/recipe.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-recipe-search',
  templateUrl: './recipe-search.component.html',
  styleUrls: ['./recipe-search.component.css']
})
export class RecipeSearchComponent implements OnInit, OnDestroy {

  form!:FormGroup
  sub$!:Subscription
  userId!:string
  SpoonacularRecipes!:Recipe[]
  MongoRecipes!:Recipe[]
  recipeInfo!:Recipe
  cuisineOptions:Cuisine = new Cuisine()
  checkFollow:boolean = false
  showRecipeInfo:boolean = false
  init:boolean = true
  privilege!:boolean

  constructor(private fb:FormBuilder, private recipeSvc:RecipeService, private userSvc:UserService, private actRoute:ActivatedRoute, private router:Router, private calendarSvc:CalendarService) {}

  async ngOnInit(): Promise<void> {
    this.sub$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
        const checkPrivilege = await this.userSvc.checkPrivilege(this.userId)
            if (checkPrivilege['msg']['valueType'] == "TRUE") {
              this.privilege = true
            } else {
              this.privilege = false
            }
      }
    )
    this.form = this.createForm()
  }

  createForm(): FormGroup {
    return this.fb.group({
      searchText: this.fb.control('', [Validators.required]),
      cuisine: this.fb.control(''),
      maxReadyTime: this.fb.control('', [Validators.min(0)])
    })
  }

  async processForm() {
    const searchText = this.form.value["searchText"]
    const cuisine = this.form.value["cuisine"]
    const maxReadyTime = this.form.value["maxReadyTime"]
    this.SpoonacularRecipes = await this.recipeSvc.searchRecipeSpoonacular(searchText, cuisine, maxReadyTime)
    this.MongoRecipes = await this.recipeSvc.searchRecipeMongo(searchText, cuisine, maxReadyTime)
    this.showRecipeInfo = false
    this.init = false;
  }

  async getRecipeInfoSpoonacular(idx:number) {
    const recipeId = this.SpoonacularRecipes[idx]['recipeId']
    this.recipeInfo = await this.recipeSvc.getRecipeInfoSpoonacular(recipeId)
    this.showRecipeInfo = true
  }

  async getRecipeInfoMongo(idx:number) {
    this.recipeInfo = this.MongoRecipes[idx]
    this.showRecipeInfo = true
  }

  cook(recipeId:string, recipeTitle:string) {
    this.recipeSvc.cook(this.userId, recipeId, recipeTitle)
    if(window.confirm('Want to navigate to grocery list to deduct ingredient?')){
      this.router.navigate(['/grocery', this.userId])
    }
  }

  notSpoonacularRecipe(author:string) {
    return author != "Spoonacular"
  }

  async toAuthorPage(author:string) {
    const authorId = await this.userSvc.checkUser(author)
    const queryParams: Params = { authorId: authorId["msg"]["string"], authorName: this.recipeInfo.author }
    this.router.navigate(['/recipe/author',this.userId],{queryParams:queryParams})
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe()
  }

  insertGoogleCalendar(recipeTitle:string) {
    this.calendarSvc.getOauthUrl(this.userId, recipeTitle).then((result: any) => {
        window.location.href = result.response
    })
  }

}
