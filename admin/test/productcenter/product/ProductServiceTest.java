package productcenter.product;

import models.Product;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import play.test.WithApplication;
import services.ProductService;
import utils.Global;

import java.util.List;

/**
 * 产品（商品）内容（详情）Service测试
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
    @Rollback(false)
    public void testSave() {
        Product product = new Product();
        product.setName("产品测试_ldj_01");
        product.setBrandId(1111);
        product.setSupplierSpuCode("100001");
        product.setSpuCode("200001");
        product.setCategoryId(2222);
        product.setAddress("产品产地测试");
        product.setDescription("产品描述测试");
        product.setOnline(true);
        product.setIsDelete(true);

        System.out.println("test product: " + product.toString());

        productService.save(product);

        System.out.println("after save product: " + product.toString());
    }

    @Test
    @Rollback(false)
    public void testFalseDelete() {
        Product product = productService.getProductById(1);
        System.out.println("testFalseDelete product: " + product.toString());
        productService.falseDelete(1);
        product = productService.getProductById(1);
        System.out.println("testFalseDelete product: " + product.toString());
    }

    @Test
    @Rollback(false)
    public void testUpdate() {
        Product product = productService.getProductById(1);
        product.setName("产品测试_ldj_01");
        product.setBrandId(1111);
        product.setSupplierSpuCode("100001");
        product.setSpuCode("200001");
        product.setCategoryId(2222);
        product.setAddress("产品产地测试-字段更新");
        product.setDescription("产品描述测试");
        product.setOnline(true);
        product.setIsDelete(true);

        System.out.println("testUpdate product: " + product.toString());

        productService.update(product);

        System.out.println("after testUpdate product: " + product.toString());
    }

    @Test
    @Rollback(false)
    public void testGetProductById() {
        Product product = productService.getProductById(1);
        System.out.println("testGetProductById product: " + product.toString());
    }

    @Test
    @Rollback(false)
    public void testGetProductList() {
        Product param = new Product();
        param.setName("测试");
        List<Product> productList = productService.getProductList(null,param);
        System.out.println("testGetProductList productList: " + productList.size() + "\n" + productList);
    }

}
