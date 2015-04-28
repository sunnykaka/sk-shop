package product;


import common.utils.page.Page;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.constants.StoreStrategy;
import productcenter.models.Product;
import productcenter.services.ProductService;
import utils.Global;

import java.util.List;
import java.util.Optional;

/**
 * 产品（商品）图片 Service测试
 * User: lidujun
 * Date: 2015-04-02
 */
public class ProductServiceTest extends WithApplication {

    private ProductService productService;

    @Before
    public void setUp() {
        super.startPlay();
        productService = Global.ctx.getBean(ProductService.class);
    }

    @Test
    public void queryAllProducts() {
        List<Product> list = productService.queryAllProducts();
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void getProductById() {
        Product product = productService.getProductById(5609);
        System.out.println("-----------------------------: " + product);
    }

    @Test
    public void queryProductPageListWithPage() {
        Page<Product> tmpPage = new Page<Product>(2,2);
        Optional<Page<Product>> page = Optional.of(tmpPage);
        Product param = new Product();
        param.setStoreStrategy(StoreStrategy.NormalStrategy);
        List<Product> list = productService.queryProductPageListWithPage(page, param);
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }
}
