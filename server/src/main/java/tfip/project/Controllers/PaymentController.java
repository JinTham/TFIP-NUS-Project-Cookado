package tfip.project.Controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.stripe.exception.StripeException;
import tfip.project.Models.CheckoutPayment;
import tfip.project.Services.PaymentService;

@RestController
@RequestMapping(value = "/api/payment")
public class PaymentController {
	
    @Autowired
    private PaymentService paymentSvc;

    private static Gson gson = new Gson();

	@PostMapping
	public String paymentWithCheckoutPage(@RequestBody CheckoutPayment payment) throws StripeException {
        Map<String,String> responseData = paymentSvc.paymentWithCheckoutPage(payment);
		return gson.toJson(responseData);
	}

}
