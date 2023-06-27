import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GroceryRecordComponent } from './components/grocery/grocery-record.component';
import { GroceryListComponent } from './components/grocery/grocery-list.component';
import { GroceryAddComponent } from './components/grocery/grocery-add.component';
import { GroceryItemRecordComponent } from './components/grocery/grocery-item-record.component';
import { RecipeSearchComponent } from './components/recipe/recipe-search.component';
import { RecipeCreateComponent } from './components/recipe/recipe-create.component';
import { FrontpageComponent } from './components/frontpage.component';
import { LoginComponent } from './components/login.component';
import { RecipeRecordComponent } from './components/recipe/recipe-record.component';
import { FollowComponent } from './components/recipe/follow.component';
import { AuthorComponent } from './components/recipe/author.component';
import { CheckoutComponent } from './components/payment/checkout.component';
import { SuccessComponent } from './components/payment/success.component';
import { IndexComponent } from './components/index.component';
import { CalendarComponent } from './components/recipe/calendar.component';

const routes: Routes = [
  {path:"",component:LoginComponent},
  {path:"frontpage/:userId",component:FrontpageComponent},
  {path:"grocery/:userId",component:GroceryListComponent},
  {path:"grocery/item/:userId",component:GroceryAddComponent},
  {path:"grocery/record/:userId",component:GroceryRecordComponent},
  {path:"grocery/item/record/:userId",component:GroceryItemRecordComponent},
  {path:"recipe/:userId",component:RecipeSearchComponent},
  {path:"recipe/create/:userId",component:RecipeCreateComponent},
  {path:"recipe/cookRecord/:userId",component:RecipeRecordComponent},
  {path:"recipe/follow/:userId",component:FollowComponent},
  {path:"recipe/author/:userId",component:AuthorComponent},
  {path:"payment/:userId",component:CheckoutComponent},
  {path:"payment/success/:userId",component:SuccessComponent},
  {path:"event",component:CalendarComponent},
  {path:"**",redirectTo:"/",pathMatch:"full"}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
