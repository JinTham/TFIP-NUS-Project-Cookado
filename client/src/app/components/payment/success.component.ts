import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-success',
  templateUrl: './success.component.html',
  styleUrls: ['./success.component.css']
})
export class SuccessComponent implements OnInit{

  constructor(private actRoute:ActivatedRoute, private userSvc:UserService) {}

  userId!:string
  sub$!:Subscription
  
  ngOnInit(): void {
    this.sub$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
        this.userSvc.addPrivilege(this.userId)
      }
    )
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe()
  }
}
