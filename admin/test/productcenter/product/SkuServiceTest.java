package productcenter.product;

import common.utils.Money;
import models.Sku;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import play.test.WithApplication;
import services.SkuService;
import utils.Global;

import java.util.List;

/**
 * 单品SKU
 * User: lidujun
 * Date: 2015-04-03
 */
public class SkuServiceTest extends WithApplication {

    @Autowired
    private SkuService skuService;

    @Before
    public void setUp() {
        super.startPlay();
        skuService = Global.ctx.getBean(SkuService.class);
    }

    @Test
    @Rollback(false)
    public void testSave() {
        Sku sku = new Sku();
        sku.setProductId(1);
        sku.setOnline(true);
        sku.setBarCode("100001");
        sku.setSkuCode("2000001");
        sku.setPrice(Money.valueOf(10));
        sku.setDomesticPrice(Money.valueOf(20));
        sku.setForeignPrice(Money.valueOf(30));
        sku.setStock(40);
        sku.setSellMax(50);

        System.out.println("testSave sku: " + sku.toString());

        skuService.save(sku);

        System.out.println("after save sku: " + sku.toString());
    }

    @Test
    @Rollback(false)
    public void testRealDelete() {
        Sku sku = skuService.getSkuById(1);
        System.out.println("testRealDelete sku: " + sku.toString());
        skuService.realDelete(1);
    }

    @Test
    @Rollback(false)
    public void testUpdate() {
        Sku sku = skuService.getSkuById(1);
        sku.setProductId(1);
        sku.setOnline(false);
        sku.setBarCode("100001");
        sku.setSkuCode("2000001");
        sku.setPrice(Money.valueOf(10));
        sku.setDomesticPrice(Money.valueOf(20));
        sku.setForeignPrice(Money.valueOf(30));
        sku.setStock(40);
        sku.setSellMax(50);

        System.out.println("testUpdate sku: " + sku.toString());

        skuService.update(sku);

        System.out.println("after testUpdate sku: " + sku.toString());
    }

    @Test
    @Rollback(false)
    public void testGetSKUById() {
        Sku sku = skuService.getSkuById(1);
        System.out.println("testGetSKUById sku: " + sku.toString());
    }

    @Test
    @Rollback(false)
    public void testGetSkuBySkuCode() {
        Sku sku = skuService.getSkuBySkuCode("2000001");
        System.out.println("testGetSkuBySkuCode sku: " + sku.toString());
    }

    @Test
    @Rollback(false)
    public void testGetSKUList() {
        Sku param = new Sku();
        param.setProductId(1);
        List<Sku> skuList = skuService.getSkuList(null, param);
        System.out.println("testGetSKUList skuList: " + skuList.size() + "\n" + skuList);
    }

}
