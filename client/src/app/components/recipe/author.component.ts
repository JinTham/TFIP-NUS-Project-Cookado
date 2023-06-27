import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { Recipe } from 'src/app/models/recipe';
import { RecipeRecord } from 'src/app/models/recipeRecord';
import { RecipeService } from 'src/app/services/recipe.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-author',
  templateUrl: './author.component.html',
  styleUrls: ['./author.component.css']
})
export class AuthorComponent {

  constructor(private recipeSvc:RecipeService, private userSvc:UserService, private actRoute:ActivatedRoute) {}

  recipeList!:RecipeRecord[]
  userId!:string
  authorId!:string
  authorName!:string
  subParam$!:Subscription
  subQueryParam$!:Subscription
  recipeInfo!:Recipe
  showRecipeInfo:boolean = false
  checkFollow!:boolean
  
  ngOnInit(): void {
    this.subParam$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
        this.subQueryParam$ = this.actRoute.queryParams.subscribe(
          async (queryParams) => {
            this.authorId = queryParams["authorId"]
            this.authorName = queryParams["authorName"]
            this.recipeList = await this.recipeSvc.getRecipesByUserId(this.authorId)
            const checkFollow = await this.userSvc.checkFollow(this.userId, this.authorId)
            if (checkFollow['msg']['valueType'] == "TRUE") {
              this.checkFollow = true
            } else {
              this.checkFollow = false
            }
          }
        )
      }
    )
  }

  async showRecipe(idx:number) {
    this.recipeInfo = this.recipeList[idx].recipe
    this.showRecipeInfo = true
  }

  hideRecipe() {
    this.showRecipeInfo = false
  }

  follow() {
    this.userSvc.follow(this.userId, this.authorId).then(()=>{ this.refreshCheckFollow() })
  }

  unfollow() {
    this.userSvc.unfollow(this.userId, this.authorId).then(()=>{ this.refreshCheckFollow() })
  }

  refreshCheckFollow(): void {
    this.subQueryParam$?.unsubscribe()
    this.subQueryParam$ = this.actRoute.params.subscribe(async (params) => {
      const checkFollow = await this.userSvc.checkFollow(this.userId, this.authorId)
            if (checkFollow['msg']['valueType'] == "TRUE") {
              this.checkFollow = true
            } else {
              this.checkFollow = false
            }
    })
  }

  ngOnDestroy(): void {
    this.subParam$.unsubscribe()
    this.subQueryParam$.unsubscribe()
  }

}
