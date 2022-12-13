package personal.machine_coding.dao;

import personal.machine_coding.model.CreateOrderRequest;
import personal.machine_coding.model.Customer;
import personal.machine_coding.model.PaymentMode;
import personal.machine_coding.model.Product;
import personal.machine_coding.model.ServiceAbilityKey;

import java.io.IOException;

public interface ECommerceDao {
    String createCustomer(Customer customer) throws IOException;
    String createProduct(Product product) throws IOException;
    void makePinCodeServiceAble(ServiceAbilityKey serviceAbilityKey, PaymentMode paymentMode) throws IOException;
    String createOrder(CreateOrderRequest createOrderRequest) throws IOException;
}
