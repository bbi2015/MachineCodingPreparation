package personal.machine_coding.ecommerce_website.dao;

import personal.machine_coding.ecommerce_website.model.CreateOrderRequest;
import personal.machine_coding.ecommerce_website.model.Customer;
import personal.machine_coding.ecommerce_website.model.PaymentMode;
import personal.machine_coding.ecommerce_website.model.Product;
import personal.machine_coding.ecommerce_website.model.ServiceAbilityKey;

import java.io.IOException;

public interface ECommerceDao {
    String createCustomer(Customer customer) throws IOException;
    String createProduct(Product product) throws IOException;
    void makePinCodeServiceAble(ServiceAbilityKey serviceAbilityKey, PaymentMode paymentMode) throws IOException;
    String createOrder(CreateOrderRequest createOrderRequest) throws IOException;
}
