package tfip.project.Services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import tfip.project.Models.CheckoutPayment;

@Service
public class PaymentService {

    @Value("${STRIPE_APIKEY}")
    private String stripeApiKey;
    
    @Transactional
    public Map<String, String> paymentWithCheckoutPage(CheckoutPayment payment) throws StripeException {
        // initilize stripe object with the api key
		Stripe.apiKey = stripeApiKey;
		// create a stripe session
		SessionCreateParams params = SessionCreateParams.builder()
				// use the credit card payment method
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.setMode(SessionCreateParams.Mode.PAYMENT).setSuccessUrl(payment.getSuccessUrl())
				.setCancelUrl(payment.getCancelUrl())
				.addLineItem(
						SessionCreateParams.LineItem.builder().setQuantity(payment.getQuantity())
								.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
												.setCurrency(payment.getCurrency())
                                                .setUnitAmount(payment.getAmount())
												.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder().setName(payment.getName()).build())
												.build())
								.build())
				.build();
		Session session = Session.create(params);

		Map<String, String> responseData = new HashMap<>();
		responseData.put("id", session.getId());
		return responseData;
    }
    
}
