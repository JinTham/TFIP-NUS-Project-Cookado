import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GroceryService } from '../services/grocery.service';
import { Params, Router } from '@angular/router';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  form!:FormGroup
  resp:string = ""

  constructor(private userSvc:UserService, private fb:FormBuilder, private router:Router) { }

    async ngOnInit() {
      this.form = this.createForm();
    }

    createForm(): FormGroup {
      return this.fb.group({
        username: this.fb.control('', [Validators.required]),
        email: this.fb.control('',[Validators.email])
      });
    }

  register() {
    const username = this.form.value["username"]
    const email = this.form.value["email"]
    this.userSvc.register(username, email)
      .then(v => { 
        console.info('resolved: ', v)
        const userId = v["msg"]["string"]
        const queryParams: Params = { username: username }
        this.router.navigate(['/frontpage',userId], {queryParams : queryParams})})
      .catch(err => {
        this.resp = err["error"]["msg"]["string"]
        console.info('error: ', err)})
  }

  login() {
    const username = this.form.value["username"]
    this.userSvc.checkUser(username)
      .then(v => { 
        console.info('resolved: ', v)
        const userId = v["msg"]["string"]
        const queryParams: Params = { username: username }
        this.router.navigate(['/frontpage',userId], {queryParams : queryParams})})
      .catch(err => {
        this.resp = err["error"]["msg"]["string"]
        console.info('error: ', err)})
  }

  registerInvalid():boolean{
    return this.form.invalid || this.form.value['email']==""
  }

}
