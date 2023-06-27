import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { lastValueFrom } from 'rxjs/internal/lastValueFrom';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit {

  oauthCode!:string
  form!:FormGroup
  userId!:string

  constructor(private httpClient:HttpClient, private actRoute:ActivatedRoute, private fb:FormBuilder, private router:Router) {}

  ngOnInit(): void {
    this.actRoute.queryParams.subscribe(params => {
      this.oauthCode = params['code']
      this.userId = params["state"]
      console.log('OAuth code:', this.oauthCode)
      console.log('UserId:', this.userId)
    })
    this.form = this.createForm()
  }

  createForm() {
    return this.fb.group({
      title: this.fb.control('', [Validators.required]),
      description: this.fb.control(''),
      oauthCode: this.fb.control('')
    })
  }

  createEvent() {
    const eventInfo = this.form.value;
    eventInfo.oauthCode = this.oauthCode;
    lastValueFrom(this.httpClient.post<string>('/api/calender/event/'+this.userId, eventInfo))
      .then((response: any) => {
        console.info(response.response)
        this.router.navigate(['/frontpage',this.userId])
      })
      .catch(error => {
        console.log('Error creating event:', error)
      })
  }

}
