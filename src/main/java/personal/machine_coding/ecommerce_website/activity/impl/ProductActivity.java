package personal.machine_coding.ecommerce_website.activity.impl;

import org.apache.commons.lang3.StringUtils;
import personal.machine_coding.ecommerce_website.activity.ECommerceActivity;
import personal.machine_coding.ecommerce_website.dao.ECommerceDao;
import personal.machine_coding.ecommerce_website.dao.impl.ECommerceInMemoryDaoImpl;
import personal.machine_coding.ecommerce_website.exception.BadRequestException;
import personal.machine_coding.ecommerce_website.model.Address;
import personal.machine_coding.ecommerce_website.model.Product;

import java.io.IOException;

public class ProductActivity extends ECommerceActivity {

    private final ECommerceDao eCommerceDao = ECommerceInMemoryDaoImpl.getInstance();

    @Override
    public String createProduct(final String productName, final long quantity, final int pickUpPinCode,
                                final double price) throws IOException {
        if (StringUtils.isBlank(productName)) {
            throw new BadRequestException("Invalid product name provided");
        }

        return eCommerceDao.createProduct(Product.builder()
                .productName(productName)
                .quantity(quantity)
                .price(price)
                .pickUpAddress(Address.builder().pinCode(pickUpPinCode).build())
                .build());
    }
}
