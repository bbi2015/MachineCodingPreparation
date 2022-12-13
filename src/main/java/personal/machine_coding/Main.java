package personal.machine_coding;

import personal.machine_coding.activity.ECommerceActivity;
import personal.machine_coding.activity.impl.CustomerActivity;
import personal.machine_coding.activity.impl.OrderActivity;
import personal.machine_coding.activity.impl.ProductActivity;
import personal.machine_coding.activity.impl.ServiceAbilityActivity;
import personal.machine_coding.model.PaymentMode;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final ECommerceActivity customerActivity = new CustomerActivity();
        final ECommerceActivity productActivity = new ProductActivity();
        final ECommerceActivity orderActivity = new OrderActivity();
        final ECommerceActivity serviceAbilityActivity = new ServiceAbilityActivity();

        final String customerId1 = customerActivity.createCustomer("Arijit", 799250);
        final String customerId2 = customerActivity.createCustomer("Pulkit", 799350);

        System.out.println("CustomerId1 : " + customerId1);
        System.out.println("CustomerId2 : " + customerId2);

        final String productId1 = productActivity.createProduct("Book", 10, 799300, 200.0);
        final String productId2 = productActivity.createProduct("Pen", 100, 799300, 10.0);

        System.out.println("ProductId1 : " + productId1);
        System.out.println("ProductId2 : " + productId2);

        serviceAbilityActivity.makePinCodeServiceAble(799300, 799250, PaymentMode.COD);
        serviceAbilityActivity.makePinCodeServiceAble(799300, 799250, PaymentMode.PRE_PAID);

        final String orderId1 = orderActivity.createOrder(customerId1, productId1, 5, "COD");
        System.out.println("OrderId1 : " + orderId1);

        final String orderId2 = orderActivity.createOrder(customerId1, productId1,  7, "PRE_PAID");
        System.out.println("OrderId2 : " + orderId2);

        orderActivity.createOrder(customerId2, productId2, 10, "PRE_PAID");
    }
}
