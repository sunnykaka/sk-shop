package services.product;

import dtos.ProductDetail;
import ordercenter.services.ValuationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import productcenter.dtos.ProductDetailBase;
import productcenter.services.ProductDetailBaseService;
import productcenter.services.SeoService;

import java.util.stream.IntStream;

/**
 * Created by liubin on 15-5-13.
 */
@Service
public class ProductDetailService {

    @Autowired
    private ProductDetailBaseService productDetailBaseService;

    @Autowired
    private ValuationService valuationService;

    @Autowired
    private SeoService seoService;


    public ProductDetail showDetail(int productId, Integer skuId) {

        ProductDetail productDetail = new ProductDetail();
        ProductDetailBase productDetailBase = productDetailBaseService.showDetail(productId, skuId);
        int[] counts = valuationService.countValuationGroupByPoint(productDetailBase.getProduct().getId());

        productDetail.setBase(productDetailBase);
        productDetail.setGoodValuationCount(counts[0]);
        productDetail.setNormalValuationCount(counts[1]);
        productDetail.setBadValuationCount(counts[2]);
        productDetail.setAllValuationCount(IntStream.of(counts).sum());

        productDetail.setSeo(seoService.getProductSeo(productDetailBase.getProduct()));

        return productDetail;
    }


}
