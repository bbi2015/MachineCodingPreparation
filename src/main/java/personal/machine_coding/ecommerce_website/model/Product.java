package personal.machine_coding.ecommerce_website.model;

import lombok.Builder;
import lombok.Getter;
import personal.machine_coding.ecommerce_website.model.Address;

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
