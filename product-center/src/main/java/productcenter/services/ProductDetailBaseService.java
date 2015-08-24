package productcenter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import productcenter.dtos.ProductDetailBase;
import productcenter.models.Product;

/**
 * Created by liubin on 15-5-13.
 */
@Service
public class ProductDetailBaseService {

    @Autowired
    private ProductService productService;

    public ProductDetailBase showDetail(int productId, Integer skuId) {

        Product product = productService.getProductById(productId);
        if(product == null) return null;

        ProductDetailBase.Builder builder = ProductDetailBase.Builder.newBuilder(product, skuId);
        builder.buildProductDetail();
        builder.buildCms();
        builder.buildSku();
        builder.buildDefaultSku();
        builder.buildFavorites();
        builder.buildMatchProductList();

        return builder.build();
    }


}
