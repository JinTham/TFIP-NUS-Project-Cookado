import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { loadStripe } from '@stripe/stripe-js';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  constructor(private httpClient:HttpClient) { }

  // load Stripe
  stripePromise = loadStripe(environment.stripe);

  async payStripe(userId:string): Promise<void> {
    // create a payment object
    const payment = {
      name: 'Privilege',
      currency: 'sgd',
      amount: 100, // amount on cents *10 => to be on dollar
      quantity: '1',
      cancelUrl: 'https://tfipcookado-production.up.railway.app/payment/'+userId,
      successUrl: 'https://tfipcookado-production.up.railway.app/payment/success/'+userId,
    };

    const stripe = await this.stripePromise;

    this.httpClient.post(`${environment.serverUrl}`, payment)
      .subscribe((data: any) => {
        // redirect To Checkout page of Stripe platform
        stripe?.redirectToCheckout({
          sessionId: data.id,
        })
      })
  }
}
