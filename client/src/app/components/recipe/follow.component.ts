import { Component } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Recipe } from 'src/app/models/recipe';
import { User } from 'src/app/models/user';
import { RecipeService } from 'src/app/services/recipe.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-follow',
  templateUrl: './follow.component.html',
  styleUrls: ['./follow.component.css']
})
export class FollowComponent {

  constructor(private userSvc:UserService, private recipeSvc:RecipeService, private actRoute:ActivatedRoute, private router:Router) {}

  followees!:User[]
  followers!:User[]
  userId!:string
  sub$!:Subscription
  latestRecipe!:Recipe
  showRecipeInfo:boolean = false
  
  ngOnInit(): void {
    this.sub$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
        this.followees = await this.userSvc.listFollowee(this.userId)
        this.followers = await this.userSvc.listFollower(this.userId)
      }
    )
  }

  async showLatestRecipe(authorId:string) {
    const recipes = await this.recipeSvc.getRecipesByUserId(authorId)
    if (recipes!=null) {
      this.latestRecipe = recipes[0].recipe
      this.showRecipeInfo = true
    }
  }

  hideRecipe() {
    this.showRecipeInfo = false
  }

  toAuthorPage(idx:number) {
    const authorId = this.followees[idx].userId
    const authorName = this.followees[idx].username
    const queryParams: Params = { authorId: authorId, authorName: authorName }
    this.router.navigate(['/recipe/author',this.userId],{queryParams:queryParams})
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe()
  }
}
