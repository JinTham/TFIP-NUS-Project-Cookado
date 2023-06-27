import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { CookRecord } from 'src/app/models/cookRecord';
import { RecipeService } from 'src/app/services/recipe.service';

@Component({
  selector: 'app-recipe-record',
  templateUrl: './recipe-record.component.html',
  styleUrls: ['./recipe-record.component.css']
})
export class RecipeRecordComponent {
  
  constructor(private recipeSvc:RecipeService, private actRoute:ActivatedRoute) {}

  cookRecords!:CookRecord[]
  userId!:string
  sub$!:Subscription
  

  ngOnInit(): void {
    this.sub$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
        this.cookRecords = await this.recipeSvc.getCookRecords(this.userId)
      }
    )
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe()
  }
}
