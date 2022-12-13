package personal.machine_coding.ecommerce_website.activity.impl;

import org.apache.commons.lang3.StringUtils;
import personal.machine_coding.ecommerce_website.activity.ECommerceActivity;
import personal.machine_coding.ecommerce_website.dao.ECommerceDao;
import personal.machine_coding.ecommerce_website.dao.impl.ECommerceInMemoryDaoImpl;
import personal.machine_coding.ecommerce_website.exception.BadRequestException;
import personal.machine_coding.ecommerce_website.model.Address;
import personal.machine_coding.ecommerce_website.model.Customer;

import java.io.IOException;

public class CustomerActivity extends ECommerceActivity {

    private final ECommerceDao eCommerceDao = ECommerceInMemoryDaoImpl.getInstance();

    @Override
    public String createCustomer(final String customerName, final int pinCode) throws IOException {
        if (StringUtils.isBlank(customerName)) {
            System.out.println("Invalid customer name provided");
            throw new BadRequestException("Invalid customer name provided");
        }
        return eCommerceDao.createCustomer(Customer.builder().customerName(customerName)
                .address(Address.builder().pinCode(pinCode).build()).build());
    }

}
