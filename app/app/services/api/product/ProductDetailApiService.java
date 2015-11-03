package services.api.product;

import api.response.product.ProductDetailDto;
import cmscenter.services.SkCmsService;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.dtos.ProductDetailBase;
import productcenter.services.ProductDetailBaseService;
import productcenter.services.ProductService;
import usercenter.models.User;

/**
 * Created by liubin on 15-8-3.
 */
@Service
public class ProductDetailApiService {

    @Autowired
    ProductService productService;

    @Autowired
    ProductDetailBaseService productDetailBaseService;

    @Autowired
    SkCmsService skCmsService;

    @Transactional(readOnly = true)
    public ProductDetailDto showDetail(int productId, Integer skuId, User user) {

        ProductDetailBase base = productDetailBaseService.showDetail(productId, skuId, user);
        if(base == null) {
            throw new AppBusinessException(ErrorCode.NotFound, "没有找到您请求的商品信息");
        }
        base.setIsBooked(skCmsService.isBooked(user, productId));

        return ProductDetailDto.build(base);
    }

}
