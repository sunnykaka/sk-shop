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

    public ProductDetail showDetail(int productId) {

        Product product = productService.getProductById(productId);
        if(product == null) return null;




        return null;
    }


}
