import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/internal/Subscription';
import { ActivatedRoute } from '@angular/router';
import { GroceryRecord } from 'src/app/models/groceryRecord';
import { GroceryService } from 'src/app/services/grocery.service';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';

imports: [
  MatTableModule,
  MatButtonModule
]

@Component({
  selector: 'app-grocery-record',
  templateUrl: './grocery-record.component.html',
  styleUrls: ['./grocery-record.component.css']
})
export class GroceryRecordComponent implements OnInit{

  constructor(private grocerySvc:GroceryService, private actRoute:ActivatedRoute) {}

  groceryRecords!:GroceryRecord[]
  userId!:string
  sub$!:Subscription
  
  ngOnInit(): void {
    this.sub$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
        this.groceryRecords = await this.grocerySvc.getGroceryRecords(this.userId)
        console.log(this.groceryRecords)
      }
    )
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe()
  }

}
