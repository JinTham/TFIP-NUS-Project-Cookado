import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { Cuisine } from 'src/app/models/cuisine';
import { Recipe } from 'src/app/models/recipe';
import { RecipeRecord } from 'src/app/models/recipeRecord';
import { RecipeService } from 'src/app/services/recipe.service';

@Component({
  selector: 'app-recipe-create',
  templateUrl: './recipe-create.component.html',
  styleUrls: ['./recipe-create.component.css']
})
export class RecipeCreateComponent implements OnInit, OnDestroy{

  form!:FormGroup
  sub$!:Subscription
  userId!:string
  ownRecipeList!:RecipeRecord[]
  recipe!:Recipe
  showRecipeInfo!:boolean
  extendedIngredients!:FormArray
  cuisineOptions:Cuisine = new Cuisine()
  recipeId!:string
  imageUrl!:string

  constructor(private fb:FormBuilder, private recipeSvc:RecipeService, private actRoute:ActivatedRoute) {}

  ngOnInit(): void {
    this.sub$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
        this.ownRecipeList = await this.recipeSvc.getRecipesByUserId(this.userId)
      }
    )
    this.form = this.createForm()
    this.extendedIngredients = this.form.get('extendedIngredients') as FormArray
  }

  createForm(): FormGroup {
    return this.fb.group({
      recipeTitle: this.fb.control('', [Validators.required]),
      readyInMinutes: this.fb.control('', [Validators.required,Validators.min(1)]),
      summary: this.fb.control('', [Validators.required]),
      cuisine: this.fb.control('', [Validators.required]),
      instructions: this.fb.control('', [Validators.required]),
      extendedIngredients: this.fb.array([this.ingredientArrayForm()],[Validators.required])
    })
  }

  ingredientArrayForm() {
    return this.fb.group({
      ingredientName:this.fb.control('',[Validators.required]),
      amount: this.fb.control('',[Validators.required,Validators.min(0.5)]),
      unit:this.fb.control('', [Validators.required])
    })
  }

  async processForm() {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (!fileInput || !fileInput.files || fileInput.files.length === 0) {
      reject(new Error('No image file selected'));
      return;
    }
    await this.recipeSvc.postImage(this.userId, fileInput.files[0]).then(v => { 
      this.recipeId = v["msg"]["string"].substring(0,8)
      this.imageUrl = v["msg"]["string"].substring(8)
      const recipe = new Recipe({
        recipeId: this.recipeId,
        recipeTitle: this.form.value["recipeTitle"],
        author: "",
        readyInMinutes: this.form.value["readyInMinutes"],
        image: this.imageUrl,
        summary: this.form.value["summary"],
        cuisine: this.form.value["cuisine"],
        instructions: this.form.value["instructions"],
        extendedIngredients: this.form.value["extendedIngredients"]
       })
       this.recipeSvc.createRecipe(this.userId, recipe).then(()=>{ this.refreshItems() })
    }).catch(err => {
      console.info('error: ', err)})
    this.form = this.createForm()
    this.extendedIngredients = this.form.get('extendedIngredients') as FormArray
  }

  addIngredient(): void {
    this.extendedIngredients.push(this.ingredientArrayForm())
  }

  removeIngredient(idx: number): void {
    this.extendedIngredients.removeAt(idx)
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe()
  }

  deleteRecipe(recipeId:string, recipeTitle:string) {
    if(window.confirm('Are sure you want to delete this recipe ('+recipeTitle+') ?')){
      this.recipeSvc.deleteRecipe(this.userId, recipeId).then(()=>{ this.refreshItems() })
    }
  }

  refreshItems(): void {
    this.sub$?.unsubscribe()
    this.sub$ = this.actRoute.params.subscribe(async (params) => {
      this.ownRecipeList = await this.recipeSvc.getRecipesByUserId(this.userId)
    });
  }

  showRecipe(idx:number) {
    this.recipe = this.ownRecipeList[idx]["recipe"]
    this.showRecipeInfo = true
  }

  hideRecipe() {
    this.showRecipeInfo = false
  }

}

function reject(arg0: Error) {
  throw new Error('No image attached.')
}

