package product;


import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.models.Html;
import productcenter.services.ProductDesService;
import utils.Global;

import java.util.List;

/**
 * 产品（商品）图片 Service测试
 * User: lidujun
 * Date: 2015-04-02
 */
public class ProductDesServiceTest extends WithApplication {

    private ProductDesService productDesService;

    @Before
    public void setUp() {
        super.startPlay();
        productDesService = Global.ctx.getBean(ProductDesService.class);
    }

    @Test
    public void queryAllHtmlDes() {
        List<Html> list = productDesService.queryAllHtmlDes();
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void queryHtmlDesByProductId() {
        List<Html>  list = productDesService.queryHtmlDesByProductId(2159);
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }
}
