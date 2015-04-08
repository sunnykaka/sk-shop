package productcenter.product;

import productcenter.models.Product;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.services.ProductService;
import utils.Global;

import java.util.List;
import java.util.Optional;

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
    public void testSave() {
        Product product = new Product();
        product.setName("产品测试_ldj_01");
        product.setDesigerId(2);
        product.setSupplierSpuCode("100001");
        product.setSpuCode("200001");
        product.setCategoryId(2222);
        product.setAddress("产品产地测试");
        product.setDescription("产品描述测试");
        product.setOnline(true);
        product.setIsDelete(true);

        System.out.println("test product: " + product);

        productService.save(product);

        System.out.println("after save product: " + product);
    }

    @Test
    public void testFalseDelete() {
        Optional<Product> productOpt = productService.getProductById(1);
        if(productOpt.isPresent()) {
            Product product =productOpt.get();
            System.out.println("testFalseDelete product: " + product);
            productService.falseDelete(1);
        }
    }

    @Test
    public void testUpdate() {
        Optional<Product> productOpt = productService.getProductById(2);
        if(productOpt.isPresent()) {
            Product product =productOpt.get();
            product.setName("产品测试_ldj_01");
            product.setDesigerId(2);
            product.setSupplierSpuCode("100001");
            product.setSpuCode("200001");
            product.setCategoryId(2222);
            product.setAddress("产品产地测试-字段更新");
            product.setDescription("产品描述测试");
            product.setOnline(true);
            product.setIsDelete(true);

            System.out.println("testUpdate product: " + product);

            productService.update(product);

            System.out.println("after testUpdate product: " + product);
        }
    }

    @Test
    public void testGetProductById() {
        Optional<Product> productOpt = productService.getProductById(2);
        if(productOpt.isPresent()) {
            Product product = productOpt.get();
            System.out.println("testGetProductById product: " + product);
        }
    }

    @Test
    public void testGetProductList() {
        Product param = new Product();
        param.setName("测试");
        List<Product> productList = productService.getProductList(Optional.ofNullable(null),param);
        System.out.println("testGetProductList productList: " + productList.size() + "\n" + productList);
    }

}
