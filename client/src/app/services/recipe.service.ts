import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { ElementRef, Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs/internal/lastValueFrom';
import { Recipe } from '../models/recipe';
import { RecipeRecord } from '../models/recipeRecord';
import { CookRecord } from '../models/cookRecord';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {

  constructor(private httpClient:HttpClient) { }

  SERVER_URL_RECIPE:string = "/api/recipe"
  headers = new HttpHeaders().set("Content-Type","application/json")

  searchRecipeSpoonacular(searchText:string, cuisine:string, maxReadyTime:number):Promise<any> {
    let params = new HttpParams().set('searchText', searchText)
    if (cuisine!=""){
      params = params.set('cuisine', cuisine)
    }
    if (maxReadyTime!=null){
      params = params.set('maxReadyTime', maxReadyTime) 
    }
    return lastValueFrom(this.httpClient.get<Recipe>(this.SERVER_URL_RECIPE+'/api',{params:params,headers:this.headers}))
  }

  searchRecipeMongo(searchText:string, cuisine:string, maxReadyTime:number):Promise<any> {
    let params = new HttpParams().set('searchText', searchText)
    if (cuisine!=""){
      params = params.set('cuisine', cuisine)
    }
    if (maxReadyTime!=null){
      params = params.set('maxReadyTime', maxReadyTime) 
    }
    return lastValueFrom(this.httpClient.get<Recipe>(this.SERVER_URL_RECIPE+'/mongo',{params:params,headers:this.headers}))
  }

  getRecipeInfoSpoonacular(recipeId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<Recipe>(this.SERVER_URL_RECIPE+'/'+recipeId,{headers:this.headers}))
  }

  postImage(userId:string, imageFile:Blob):Promise<any> {
    const formData = new FormData()
    formData.append("image",imageFile)
    return lastValueFrom(this.httpClient.post(this.SERVER_URL_RECIPE+'/image/'+userId, formData))
  }

  createRecipe(userId:string, recipe:Recipe):Promise<any> {
    console.log(recipe)
    return lastValueFrom(this.httpClient.post<Recipe>(this.SERVER_URL_RECIPE+'/'+userId, JSON.stringify(recipe), {headers:this.headers}))
  }

  getRecipesByUserId(userId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<RecipeRecord>(this.SERVER_URL_RECIPE+'/own/'+userId,{headers:this.headers}))
  }

  deleteRecipe(userId:string, recipeId:string):Promise<any> {
    const params = new HttpParams().set('recipeId', recipeId)
    return lastValueFrom(this.httpClient.delete<Recipe>(this.SERVER_URL_RECIPE+'/'+userId, {params:params,headers:this.headers}))
  }

  cook(userId:string, recipeId:string, recipeTitle:string):Promise<any> {
    const params = new HttpParams().set('recipeId', recipeId)
                                  .set('recipeTitle',recipeTitle)
    return lastValueFrom(this.httpClient.get<Recipe>(this.SERVER_URL_RECIPE+'/cook/'+userId, {params:params,headers:this.headers}))
  }

  getCookRecords(userId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<CookRecord>(this.SERVER_URL_RECIPE+'/cookrecord/'+userId,{headers:this.headers}))
  }

}
