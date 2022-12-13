package personal.machine_coding.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Customer {

    private final String customerName;
    private final String customerId;
    private final Address address;
}
