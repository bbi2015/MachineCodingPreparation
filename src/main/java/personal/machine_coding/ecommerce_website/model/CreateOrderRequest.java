package personal.machine_coding.ecommerce_website.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateOrderRequest {
    private final String productId;
    private final String customerId;
    private final int quantity;
    private final PaymentMode paymentMode;
}
