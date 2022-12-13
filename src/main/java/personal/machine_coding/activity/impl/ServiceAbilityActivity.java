package personal.machine_coding.activity.impl;

import personal.machine_coding.activity.ECommerceActivity;
import personal.machine_coding.dao.ECommerceDao;
import personal.machine_coding.dao.impl.ECommerceInMemoryDaoImpl;
import personal.machine_coding.model.PaymentMode;
import personal.machine_coding.model.ServiceAbilityKey;

import java.io.IOException;

public class ServiceAbilityActivity extends ECommerceActivity {

    private final ECommerceDao eCommerceDao = ECommerceInMemoryDaoImpl.getInstance();

    @Override
    public void makePinCodeServiceAble(final int sourceCode, final int destPinCode, final PaymentMode paymentMode) throws IOException {
        eCommerceDao.makePinCodeServiceAble(ServiceAbilityKey.builder()
                .sourcePinCode(sourceCode)
                .destinationPinCode(destPinCode)
                .build(), paymentMode);
    }
}
