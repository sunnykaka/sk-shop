package services.product;

import dtos.ProductDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import productcenter.models.Product;
import productcenter.services.ProductService;

/**
 * Created by liubin on 15-5-13.
 */
@Service
public class ProductDetailService {

    @Autowired
    private ProductService productService;

    public ProductDetail showDetail(int productId, Integer skuId) {

        Product product = productService.getProductById(productId);
        if(product == null) return null;

        ProductDetail.Builder builder = ProductDetail.Builder.newBuilder(product, skuId);
        builder.buildProductDetail();
        builder.buildValuation();
        builder.buildCms();
        builder.buildSku();
        builder.buildDefaultSku();
        builder.buildFavorites();

        return builder.build();
    }


}
