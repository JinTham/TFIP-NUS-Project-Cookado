import { Component, OnInit } from '@angular/core';
import { GroceryService } from '../services/grocery.service';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Subscription, lastValueFrom } from 'rxjs';
import { UserService } from '../services/user.service';
import { HttpClient, HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-frontpage',
  templateUrl: './frontpage.component.html',
  styleUrls: ['./frontpage.component.css']
})
export class FrontpageComponent implements OnInit{

  subParam$!:Subscription
  subQueryParam$!:Subscription
  username!:string
  userId!:string
  privilege!:boolean

  constructor(private userSvc:UserService, private actRoute:ActivatedRoute, private router:Router, private httpClient:HttpClient) { }

  async ngOnInit():Promise<void> {
    this.subParam$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
      }
    )
    this.subQueryParam$ = this.actRoute.queryParams.subscribe(
      async (queryParams) => {
        this.username = queryParams["username"]
      }
    )
    const checkPrivilege = await this.userSvc.checkPrivilege(this.userId)
            if (checkPrivilege['msg']['valueType'] == "TRUE") {
              this.privilege = true
            } else {
              this.privilege = false
            }
  }

  gotoRecipe() {
    const queryParams: Params = { privilege: this.privilege }
    this.router.navigate(['/recipe',this.userId], {queryParams : queryParams})
  }

  ngOnDestroy(): void {
    this.subParam$.unsubscribe()
    this.subQueryParam$.unsubscribe()
  }

}
