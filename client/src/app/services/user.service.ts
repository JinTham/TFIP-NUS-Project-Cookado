import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { lastValueFrom } from 'rxjs/internal/lastValueFrom';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient:HttpClient) { }

  SERVER_URL_USER:string = "/api/user"
  headers = new HttpHeaders().set("Content-Type","application/json")

  checkUser(username:string):Promise<any> {
    const params = new HttpParams().set('username', username)
    return lastValueFrom(this.httpClient.get<string>(this.SERVER_URL_USER+'/login', {params:params, headers:this.headers}))
  }

  register(username:string, email:string):Promise<any> {
    const params = new HttpParams().set('username', username)
                                  .set('email',email)
    return lastValueFrom(this.httpClient.get<string>(this.SERVER_URL_USER+'/register', {params:params, headers:this.headers}))
  }

  follow(userId:string, authorId:string):Promise<any> {
    const params = new HttpParams().set('authorId', authorId)
    return lastValueFrom(this.httpClient.get<string>(this.SERVER_URL_USER+'/follow/'+userId, {params:params, headers:this.headers}))
  }

  unfollow(userId:string, authorId:string):Promise<any> {
    const params = new HttpParams().set('authorId', authorId)
    return lastValueFrom(this.httpClient.get<string>(this.SERVER_URL_USER+'/unfollow/'+userId, {params:params, headers:this.headers}))
  }

  checkFollow(userId:string, authorId:string):Promise<any> {
    const params = new HttpParams().set('authorId', authorId)
    return lastValueFrom(this.httpClient.get<boolean>(this.SERVER_URL_USER+'/checkfollow/'+userId, {params:params, headers:this.headers}))
  }

  listFollowee(userId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<string>(this.SERVER_URL_USER+'/listfollowee/'+userId, {headers:this.headers}))
  }

  listFollower(userId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<string>(this.SERVER_URL_USER+'/listfollower/'+userId, {headers:this.headers}))
  }

  checkPrivilege(userId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<boolean>(this.SERVER_URL_USER+'/privilege/'+userId, {headers:this.headers}))
  }

  addPrivilege(userId:string):Promise<any> {
    return lastValueFrom(this.httpClient.get<string>(this.SERVER_URL_USER+'/addprivilege/'+userId, {headers:this.headers}))
  }

}
