import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material.module';
import { ReactiveFormsModule } from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import { GroceryAddComponent } from './components/grocery/grocery-add.component';
import { GroceryRecordComponent } from './components/grocery/grocery-record.component';
import { GroceryListComponent } from './components/grocery/grocery-list.component';
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
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatSelectModule } from '@angular/material/select';
import { CalendarComponent } from './components/recipe/calendar.component';

@NgModule({
  declarations: [
    AppComponent,
    FrontpageComponent,
    GroceryListComponent,
    FrontpageComponent,
    GroceryAddComponent,
    GroceryRecordComponent,
    GroceryItemRecordComponent,
    RecipeSearchComponent,
    RecipeCreateComponent,
    RecipeRecordComponent,
    LoginComponent,
    FollowComponent,
    AuthorComponent,
    CheckoutComponent,
    SuccessComponent,
    CalendarComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }),
    BrowserAnimationsModule,
    MaterialModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatGridListModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
