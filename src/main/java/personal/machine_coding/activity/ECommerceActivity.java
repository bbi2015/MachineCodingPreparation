package personal.machine_coding.activity;

import personal.machine_coding.model.PaymentMode;

import java.io.IOException;

public abstract class ECommerceActivity {

    public String createCustomer(final String customerName, final int pinCode) throws IOException {
        return null;
    }

    public String createProduct(final String productName, final long quantity, final int pickUpPinCode,
                                final double price) throws IOException {
        return null;
    }

    public String createOrder(final String customerId, final String productId, final int quantity,
                              final String paymentMode) throws IOException {
        return null;
    }

    public void makePinCodeServiceAble(final int sourceCode, final int destPinCode, final PaymentMode paymentMode) throws IOException {};
}
