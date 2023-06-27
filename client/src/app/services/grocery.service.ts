import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs/internal/lastValueFrom';
import { GroceryList } from '../models/groceryList';
import { Item } from '../models/item';
import { GroceryRecord } from '../models/groceryRecord';
import { ItemRecord } from '../models/itemRecord';

@Injectable({
  providedIn: 'root'
})
export class GroceryService {

  constructor(private httpClient:HttpClient) { }

  SERVER_URL_GROCERY:string = "/api/grocery"
  SERVER_URL_GROCERY_ITEM:string = "/api/grocery/item"
  headers = new HttpHeaders().set("Content-Type","application/json")

  items!:Item[]

  getGrocery(userId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<GroceryList>(this.SERVER_URL_GROCERY+'/'+userId,{headers:this.headers}))
  }

  postGrocery(userId:string, groceryList:GroceryList):Promise<any> {
    console.log(groceryList)
    return lastValueFrom(this.httpClient.post<GroceryList>(this.SERVER_URL_GROCERY+'/'+userId, JSON.stringify(groceryList), {headers:this.headers}))
  }

  getItems(userId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<Item[]>(this.SERVER_URL_GROCERY_ITEM+'/'+userId,{headers:this.headers}))
  }

  postItem(userId:string, item:Item):Promise<any> {
    return lastValueFrom(this.httpClient.post<Item>(this.SERVER_URL_GROCERY_ITEM+'/'+userId, JSON.stringify(item), {headers:this.headers}))
  }

  deleteItem(userId:string, itemId:string):Promise<any> {
    const params = new HttpParams().set('itemId', itemId)
    return lastValueFrom(this.httpClient.delete<Item>(this.SERVER_URL_GROCERY_ITEM+'/'+userId, {params:params,headers:this.headers}))
  }

  getGroceryRecords(userId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<GroceryRecord>(this.SERVER_URL_GROCERY+'/records/'+userId,{headers:this.headers}))
  }

  getItemsRecords(userId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<ItemRecord>(this.SERVER_URL_GROCERY_ITEM+'/records/'+userId,{headers:this.headers}))
  }
  
}
