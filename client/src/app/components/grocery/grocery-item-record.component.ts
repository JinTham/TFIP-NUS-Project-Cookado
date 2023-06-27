import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { ItemRecord } from 'src/app/models/itemRecord';
import { GroceryService } from 'src/app/services/grocery.service';

@Component({
  selector: 'app-grocery-item-record',
  templateUrl: './grocery-item-record.component.html',
  styleUrls: ['./grocery-item-record.component.css']
})
export class GroceryItemRecordComponent implements OnInit{

  constructor(private grocerySvc:GroceryService, private actRoute:ActivatedRoute) {}

  itemRecords!:ItemRecord[]
  userId!:string
  sub$!:Subscription

  ngOnInit(): void {
    this.sub$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
        this.itemRecords = await this.grocerySvc.getItemsRecords(this.userId)
        console.log(this.itemRecords)
      }
    )
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe()
  }

}
