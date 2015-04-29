package product;


import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.models.ProductDesc;
import productcenter.services.ProductDescService;
import utils.Global;

import java.util.List;

/**
 * 产品（商品）图片 Service测试
 * User: lidujun
 * Date: 2015-04-02
 */
public class ProductDescServiceTest extends WithApplication {

    private ProductDescService productDesService;

    @Before
    public void setUp() {
        super.startPlay();
        productDesService = Global.ctx.getBean(ProductDescService.class);
    }

    @Test
    public void queryAllHtmlDesc() {
        List<ProductDesc> list = productDesService.queryAllHtmlDesc();
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void queryHtmlDescByProductId() {
        List<ProductDesc>  list = productDesService.queryHtmlDescByProductId(2159);
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }
}
