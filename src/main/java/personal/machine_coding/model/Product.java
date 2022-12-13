package personal.machine_coding.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Product {
    private final String productName;
    private final Address pickUpAddress;
    private final double price;
    private long quantity;

    public synchronized void setQuantity(final long quantity) {
        this.quantity = quantity;
    }
}
