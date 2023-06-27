import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, lastValueFrom } from 'rxjs';
import { CalendarService } from 'src/app/services/calendar.service';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit, OnDestroy{
  
  oauthCode!:string
  form!:FormGroup
  userId!:string
  recipeTitle!:string
  sub$!:Subscription

  constructor(private httpClient:HttpClient, private actRoute:ActivatedRoute, private fb:FormBuilder, private router:Router, private calendarSvc:CalendarService) {}

  ngOnInit(): void {
    this.sub$ = this.actRoute.queryParams.subscribe(
      async params => {
        this.oauthCode = params['code']
        this.userId = params["state"].substring(0,8)
        this.recipeTitle = params["state"].substring(8)
        console.log('OAuth code:', this.oauthCode)
        console.log('UserId:', this.userId)
    })
    this.form = this.createForm()
  }

  createForm() {
    return this.fb.group({
      title: this.fb.control(this.recipeTitle, [Validators.required]),
      description: this.fb.control(''),
      cookDate: this.fb.control('',[Validators.required]),
      oauthCode: this.fb.control('')
    })
  }

  processForm() {
    const eventInfo = this.form.value;
    eventInfo.oauthCode = this.oauthCode;
    this.calendarSvc.createEvent(this.userId, eventInfo)
      .then((response: any) => {
        console.info(response.response)
        this.router.navigate(['/frontpage',this.userId])
      })
      .catch(error => {
        console.log('Error creating event:', error)
      })
  }

  ngOnDestroy(): void {
      this.sub$.unsubscribe()
  }
}
