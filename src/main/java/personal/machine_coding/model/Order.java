package personal.machine_coding.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Order {
    private final String productId;
    private final String customerId;
    private final long orderCreationTime;
    private final int productQuantity;
}
