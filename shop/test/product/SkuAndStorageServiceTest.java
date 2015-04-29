package product;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import productcenter.models.SkuStorage;
import productcenter.models.StockKeepingUnit;
import productcenter.services.SkuAndStorageService;
import utils.Global;

import java.util.List;

/**
 * Sku和库存Service测试
 * User: lidujun
 * Date: 2015-04-02
 */
public class SkuAndStorageServiceTest extends WithApplication {

    private SkuAndStorageService skuAndStorageService;

    @Before
    public void setUp() {
        super.startPlay();
        skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);
    }

    @Test
    public void queryAllStockKeepingUnits() {
        List<StockKeepingUnit> list = skuAndStorageService.queryAllStockKeepingUnits();
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void getStockKeepingUnitById() {
        StockKeepingUnit sku = skuAndStorageService.getStockKeepingUnitById(5569);
        System.out.println("-----------------------------: " + sku);
    }

    @Test
    public void querySKUByBarCode() {
        StockKeepingUnit sku = skuAndStorageService.querySKUByBarCode("9335955000692");
        System.out.println("-----------------------------: " + sku);
    }

    @Test
    public void querySkuListByProductId() {
        List<StockKeepingUnit> list = skuAndStorageService.querySkuListByProductId(2159);
        System.out.println("-----------------------------: " + list.size() + "\n" + list);
    }

    @Test
    public void getSkuStorage() {
        SkuStorage skuStorage = skuAndStorageService.getSkuStorage(5569);
        System.out.println("-----------------------------: " + skuStorage);
    }
}
