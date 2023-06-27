import { Component } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import { Params, Router } from '@angular/router';
import firebase from 'firebase/compat/app';
import { GroceryService } from '../services/grocery.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent {

  constructor(private afAuth: AngularFireAuth, private router:Router, private grocerySvc:GroceryService) {}

  loginWithGoogle() {
    this.afAuth.signInWithPopup(new firebase.auth.GoogleAuthProvider())
      .then((result) => {
        console.log(result)
        const user = result.user
        const email = user?.email
        const username = user?.displayName
        const queryParams: Params = { email:email, username: username }
        this.router.navigate(['/frontpage'], {queryParams : queryParams})
      })
      .catch((error) => {
        console.error(error)
      });
  }

}
