package personal.machine_coding.ecommerce_website.activity.impl;

import personal.machine_coding.ecommerce_website.activity.ECommerceActivity;
import personal.machine_coding.ecommerce_website.dao.ECommerceDao;
import personal.machine_coding.ecommerce_website.dao.impl.ECommerceInMemoryDaoImpl;
import personal.machine_coding.ecommerce_website.exception.BadRequestException;
import personal.machine_coding.ecommerce_website.model.CreateOrderRequest;
import personal.machine_coding.ecommerce_website.model.PaymentMode;

import java.io.IOException;
import java.util.Arrays;

public class OrderActivity extends ECommerceActivity {

    private final ECommerceDao eCommerceDao = ECommerceInMemoryDaoImpl.getInstance();

    @Override
    public String createOrder(final String customerId, final String productId, final int quantity,
                              final String paymentMode) throws IOException {

        if (isInValidRequest(customerId, productId, paymentMode)) {
            throw new BadRequestException("Invalid product name provided");
        }
        final PaymentMode modeOfPayment = Arrays.stream(PaymentMode.values())
                .filter(paymentMode1 -> paymentMode1.name().equals(paymentMode))
                .findFirst().get();
        return eCommerceDao.createOrder(CreateOrderRequest.builder()
                .customerId(customerId)
                .productId(productId)
                .paymentMode(modeOfPayment)
                .quantity(quantity)
                .build());
    }

    private boolean isInValidRequest(final String customerId, final String productId, final String paymentMode) {

        // add payment validation
        final String modOfPayment = Arrays.stream(PaymentMode.values())
                .map(PaymentMode::name)
                .filter(s -> s.equals(paymentMode))
                .findFirst()
                .orElse(null);
        return customerId == null || productId == null || customerId.length() == 0 || productId.length() == 0 || modOfPayment == null;
    }
}
