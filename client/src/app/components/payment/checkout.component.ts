import { Component } from '@angular/core';
import { Subscription } from 'rxjs/internal/Subscription';
import { ActivatedRoute } from '@angular/router';
import { PaymentService } from 'src/app/services/payment.service';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent {

  constructor(private actRoute:ActivatedRoute, private paymentSvc:PaymentService) {}

  userId!:string
  sub$!:Subscription
  
  ngOnInit(): void {
    this.sub$ = this.actRoute.params.subscribe(
      async (params) => {
        this.userId = params["userId"]
      }
    )
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe()
  }

  pay() {
    this.paymentSvc.payStripe(this.userId)
  }

}
