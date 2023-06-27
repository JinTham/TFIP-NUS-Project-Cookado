import { Component, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { GroceryService } from 'src/app/services/grocery.service';
import { Item } from 'src/app/models/item';

@Component({
  selector: 'app-grocery-add',
  templateUrl: './grocery-add.component.html',
  styleUrls: ['./grocery-add.component.css']
})
export class GroceryAddComponent implements OnInit, OnDestroy{

  form!:FormGroup
  sub$!:Subscription
  userId!:string
  items!:Item[]

  constructor(private grocerySvc:GroceryService, private fb:FormBuilder, private actRoute:ActivatedRoute) { }

  ngOnInit(): void {
    this.sub$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
        this.items = await this.grocerySvc.getItems(this.userId)
        console.log(this.items)
      }
    )
    this.form = this.createForm();
  }

  createForm(): FormGroup {
    return this.fb.group({
      itemId: this.fb.control(''),
      itemName: this.fb.control('', [Validators.required]),
      topupAmount: this.fb.control('', [Validators.required,Validators.min(1)]),
      safetyStock: this.fb.control('', [Validators.required,Validators.min(0)]),
      unit: this.fb.control('', [Validators.required])
    })
  }

  processForm() {
    const item = this.form.value
    console.log(this.userId)
    this.grocerySvc.postItem(this.userId, item).then(()=>{ this.refreshItems() })
    this.form = this.createForm()
  }

  editItem(idx:number):void {
    const item = this.items[idx]
    this.form = this.fb.group({
      itemId:this.fb.control(item.itemId),
      itemName: this.fb.control(item.itemName, [Validators.required]),
      topupAmount: this.fb.control(item.topupAmount, [Validators.required,Validators.min(1)]),
      safetyStock: this.fb.control(item.safetyStock, [Validators.required,Validators.min(0)]),
      unit: this.fb.control(item.unit, [Validators.required])
    })
  }

  deleteItem(idx:number):void {
    const item = this.items[idx]
    if(window.confirm('This item in inventory will also be removed. Are sure you want to delete '+item.itemName+'?')){
      this.grocerySvc.deleteItem(this.userId, item.itemId).then(()=>{ this.refreshItems() })
    }
  }

  refreshItems(): void {
    this.sub$?.unsubscribe()
    this.sub$ = this.actRoute.params.subscribe(async (params) => {
      this.items = await this.grocerySvc.getItems(this.userId)
    });
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe()
  }

}
