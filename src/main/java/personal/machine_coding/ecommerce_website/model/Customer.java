package personal.machine_coding.ecommerce_website.model;

import lombok.Builder;
import lombok.Getter;
import personal.machine_coding.ecommerce_website.model.Address;

@Builder
@Getter
public class Customer {

    private final String customerName;
    private final String customerId;
    private final Address address;
}
