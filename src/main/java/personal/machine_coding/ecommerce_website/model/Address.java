package personal.machine_coding.ecommerce_website.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Address {
    private final String city;
    private final String state;
    private final String country;
    private final int pinCode;
}
