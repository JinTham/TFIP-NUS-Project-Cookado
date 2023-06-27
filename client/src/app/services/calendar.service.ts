import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs/internal/lastValueFrom';

@Injectable({
  providedIn: 'root'
})
export class CalendarService {

  constructor(private httpClient:HttpClient) { }

  getOauthUrl(userId:string, recipeTitle:string): Promise<any> {
    const params = new HttpParams()
                        .set("userId", userId)
                        .set("recipeTitle", recipeTitle)
    return lastValueFrom(this.httpClient.get<any>("/api/calender/oauth", { params: params }))
  }

  createEvent(userId:string, eventInfo:any):Promise<any> {
    return lastValueFrom(this.httpClient.post<string>('/api/calender/event/'+userId, eventInfo))
  }
}
