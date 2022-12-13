package personal.machine_coding.dao.impl;

import personal.machine_coding.dao.ECommerceDao;
import personal.machine_coding.exception.BadRequestException;
import personal.machine_coding.model.CreateOrderRequest;
import personal.machine_coding.model.Customer;
import personal.machine_coding.model.Order;
import personal.machine_coding.model.PaymentMode;
import personal.machine_coding.model.Product;
import personal.machine_coding.model.ServiceAbilityKey;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ECommerceInMemoryDaoImpl implements ECommerceDao {

    private static final ECommerceDao E_COMMERCE_DAO = new ECommerceInMemoryDaoImpl();

    public static ECommerceDao getInstance() {
        return E_COMMERCE_DAO;
    }

    private final Map<String, Customer> customerById = new HashMap<>();
    private final Map<String, Product> productById = new HashMap<>();
    private final Map<ServiceAbilityKey, PaymentMode> serviceAbility = new HashMap<>();
    private final Map<String, Order> orderById = new HashMap<>();


    @Override
    public String createCustomer(Customer customer) {
        final String customerId = UUID.randomUUID().toString();
        this.customerById.putIfAbsent(customerId, customer);
        return customerId;
    }

    @Override
    public String createProduct(Product product) {
        final String productId = UUID.randomUUID().toString();
        this.productById.putIfAbsent(productId, product);
        return productId;
    }

    @Override
    public void makePinCodeServiceAble(ServiceAbilityKey serviceAbilityKey, PaymentMode paymentMode) throws IOException {
        if (isValidServiceAbilityRequest(serviceAbilityKey, paymentMode)) {
            throw new BadRequestException("Service ability request with key : " + serviceAbilityKey.getSourcePinCode()
                    + " " + serviceAbilityKey.getDestinationPinCode() + " " + paymentMode.name() + " is already " +
                    "present");
        }

        final PaymentMode updatedPaymentMode = getUpdatedPaymentMode(paymentMode,
                serviceAbility.get(serviceAbilityKey));
        serviceAbility.put(serviceAbilityKey, updatedPaymentMode);
    }

    @Override
    public String createOrder(CreateOrderRequest request) throws IOException {
        final Product product = productById.get(request.getProductId());
        final Customer customer = customerById.get(request.getCustomerId());

        this.isValidRequest(product, customer, request);
        this.isQuantityServicAble(product.getQuantity(), request.getQuantity());
        this.isPinCodeServiceAble(product, customer, request.getPaymentMode());

        final String orderId = UUID.randomUUID().toString();
        this.orderById.putIfAbsent(orderId, Order.builder()
                        .customerId(request.getCustomerId())
                        .productId(request.getProductId())
                        .orderCreationTime(Instant.now().getEpochSecond())
                        .productQuantity(request.getQuantity())
                .build());
        product.setQuantity(product.getQuantity() - request.getQuantity());
        return orderId;
    }

    private boolean isValidServiceAbilityRequest(final ServiceAbilityKey key, final PaymentMode paymentMode) {
        return paymentMode.equals(serviceAbility.get(key));
    }

    private PaymentMode getUpdatedPaymentMode(final PaymentMode latestPaymentMode,
                                              final PaymentMode currentPaymentMode) {
        if (PaymentMode.ANY.equals(currentPaymentMode)) {
            return PaymentMode.ANY;
        }
        return currentPaymentMode == null ? latestPaymentMode : PaymentMode.ANY;
    }

    private void isValidRequest(final Product product, final Customer customer, final CreateOrderRequest request) throws IOException {
        if (product == null) {
            throw new BadRequestException("Product with Id : " + request.getProductId() + " is not valid");
        }

        if (customer == null) {
            throw new BadRequestException("Customer with Id : " + request.getCustomerId() + " is not valid");
        }
    }

    private void isQuantityServicAble(final long availableQuantity, final long requestedQuantity) throws IOException {
        if (availableQuantity < requestedQuantity) {
            throw new BadRequestException("Order failed because product stock is insufficient");
        }
    }

    private void isPinCodeServiceAble(final Product product, final Customer customer, final PaymentMode paymentMode) throws IOException {
        final ServiceAbilityKey key = ServiceAbilityKey.builder()
                .sourcePinCode(product.getPickUpAddress().getPinCode())
                .destinationPinCode(customer.getAddress().getPinCode())
                .build();
        if (!serviceAbility.containsKey(key)
                || (!paymentMode.equals(serviceAbility.get(key)) && !PaymentMode.ANY.equals(serviceAbility.get(key)))) {
            throw new BadRequestException("order failed because pincode is not service able with this payment mode");
        }
    }

}
