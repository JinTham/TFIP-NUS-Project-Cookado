import { Component, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { Grocery } from 'src/app/models/grocery';
import { GroceryList } from 'src/app/models/groceryList';
import { Item } from 'src/app/models/item';
import { GroceryService } from 'src/app/services/grocery.service';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

imports: [
  MatGridListModule,
  MatIconModule,
  MatButtonModule,
  MatTableModule,
  MatFormFieldModule,
  MatInputModule,
  MatSelectModule
]

@Component({
  selector: 'app-grocery-list',
  templateUrl: './grocery-list.component.html',
  styleUrls: ['./grocery-list.component.css']
})
export class GroceryListComponent implements OnInit, OnDestroy{

  userId!:string
  username!:string
  groceries!:Grocery[]
  form!:FormGroup
  ingredientsArr!:FormArray
  subParam$!:Subscription
  subQueryParam$!:Subscription
  items!:Item[]
  selectedItems: string[] = [];

  constructor(private grocerySvc:GroceryService, private fb:FormBuilder, private actRoute:ActivatedRoute) { }

  ngOnInit():void {
    this.subParam$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
        const groceriesList = await this.grocerySvc.getGrocery(this.userId)
        if (groceriesList) {
          this.groceries = groceriesList.groceries
        }
        this.items = await this.grocerySvc.getItems(this.userId)
        console.log(this.items)
      }
    )
    this.subQueryParam$ = this.actRoute.queryParams.subscribe(
      async (queryParams) => {
        this.username = queryParams["username"]
      }
    )
    this.form = this.createForm()
    this.ingredientsArr = this.form.get('ingredientsArr') as FormArray
  }

  createForm(): FormGroup {
    return this.fb.group({
      ingredientsArr: this.fb.array([this.ingredientArrayForm()],[Validators.required])
    });
  }

  ingredientArrayForm() {
    return this.fb.group({
      itemId:this.fb.control('',[Validators.required]),
      itemName: this.fb.control(''),
      quantity:this.fb.control('', [Validators.required])
    })
  }

  processForm() {
    const groceryList:GroceryList = {
      userId:this.userId,
      username:this.username,
      groceries:this.form.value["ingredientsArr"]
    }
    this.grocerySvc.postGrocery(this.userId, groceryList).then(()=>{ this.refreshGroceries() })
    this.form = this.createForm()
    this.ingredientsArr = this.form.get('ingredientsArr') as FormArray
  }

  refreshGroceries(): void {
    this.subQueryParam$?.unsubscribe()
    this.subQueryParam$ = this.actRoute.params.subscribe(async (params) => {
      const groceriesList = await this.grocerySvc.getGrocery(this.userId)
      if (groceriesList) {
        this.groceries = groceriesList.groceries
      } else {
        this.groceries = []
      }
      this.items = await this.grocerySvc.getItems(this.userId)
    })
  }

  addIngredient(): void {
    this.ingredientsArr.push(this.ingredientArrayForm())
  }

  removeIngredient(idx: number): void {
    this.ingredientsArr.removeAt(idx)
  }

  checkLowStock(itemId:string, quantity:number):boolean {
    const item = this.items.find(item => item.itemId === itemId)
    if (item?.safetyStock!=null){
      return quantity<item.safetyStock
    }
    return false
  }

  bulkUpdateForLowStock() {
    for (let grocery of this.groceries) {
      if (this.checkLowStock(grocery.itemId, grocery.quantity)) {
        const item = this.items.find(item => item.itemId === grocery.itemId)
        this.ingredientsArr.push(this.fb.group({
          itemId:this.fb.control(grocery.itemId,[Validators.required]),
          itemName: this.fb.control(grocery.itemName),
          quantity:this.fb.control(item?.topupAmount, [Validators.required])
        }))
      }
    }
  }

  ngOnDestroy(): void {
    this.subParam$.unsubscribe()
    this.subQueryParam$.unsubscribe()
  }

  onItemChange(selectedItemId: string, idx: number): void {
    // Update the selected item for the current ingredient
    const ingredientFormGroup = this.ingredientsArr.at(idx) as FormGroup
    ingredientFormGroup.patchValue({ itemId: selectedItemId })
    // Bind itemName of the selectedItem to form value
    const selectedItem = this.items.find(item => item.itemId === selectedItemId)
    if (selectedItem) {
      ingredientFormGroup.patchValue({ itemName: selectedItem.itemName })
    }
    // Update the selected items array
    this.selectedItems[idx] = selectedItemId
    // Reset the selection for subsequent ingredients
    for (let i = idx + 1; i < this.selectedItems.length; i++) {
      const ingredientGroup = this.ingredientsArr.at(i) as FormGroup;
      const itemIdControl = ingredientGroup.get('itemId')
      if (itemIdControl) {
        itemIdControl.setValue('')
      }
    }
  }
  
  getFilteredItems(idx: number): Item[] {
    // Filter the items based on the selected items so far
    const selectedItems = this.selectedItems.slice(0, idx);
    const filteredItems = this.items.filter((item) => !selectedItems.includes(item.itemId));
    return filteredItems;
  }
  
}
