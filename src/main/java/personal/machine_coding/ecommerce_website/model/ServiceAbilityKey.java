package personal.machine_coding.ecommerce_website.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class ServiceAbilityKey {
    private final int sourcePinCode;
    private final int destinationPinCode;
}
