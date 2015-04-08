package productcenter.product;

import common.utils.Money;
import models.Sku;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import play.test.WithApplication;
import services.SkuService;
import utils.Global;

import java.util.List;
import java.util.Optional;

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
    public void testSave() {
        Sku sku = new Sku();
        sku.setProductId(1);
        sku.setSkuProperty("11111,22222");
        sku.setOnline(true);
        sku.setBarCode("100001");
        sku.setSkuCode("2000001");
        sku.setPrice(Money.valueOf(10));
        sku.setDomesticPrice(Money.valueOf(20));
        sku.setForeignPrice(Money.valueOf(30));
        sku.setStock(40);
        sku.setSellMax(50);
        sku.setStatus(true);

        System.out.println("testSave sku: " + sku.toString());

        skuService.save(sku);

        System.out.println("after save sku: " + sku.toString());
    }

    @Test
    public void testRealDelete() {
        Optional<Sku> skuOpt = skuService.getSkuById(1);
        if(skuOpt.isPresent()) {
            Sku sku = skuOpt.get();
            System.out.println("testRealDelete sku: " + sku.toString());
            skuService.realDelete(1);
        }
    }

    @Test
    public void testUpdate() {
        Optional<Sku> skuOpt = skuService.getSkuById(2);
        if(skuOpt.isPresent()) {
            Sku sku = skuOpt.get();
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
    }

    @Test
    public void testGetSkuById() {
        Optional<Sku> skuOpt = skuService.getSkuById(2);
        if(skuOpt.isPresent()) {
            Sku sku = skuOpt.get();
            System.out.println("testGetSKUById sku: " + sku.toString());
        }
    }

    @Test
    public void testGetSkuBySkuCode() {
        Optional<Sku> skuOpt = skuService.getSkuBySkuCode("2000001");
        if(skuOpt.isPresent()) {
            Sku sku = skuOpt.get();
            System.out.println("testGetSkuBySkuCode sku: " + sku.toString());
        }
    }

    @Test
    public void testGetSKUList() {
        Sku param = new Sku();
        param.setProductId(1);
        List<Sku> skuList = skuService.getSkuList(Optional.ofNullable(null), param);
        System.out.println("testGetSKUList skuList: " + skuList.size() + "\n" + skuList);
    }

}
