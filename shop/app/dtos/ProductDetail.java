package dtos;

import productcenter.dtos.ProductDetailBase;
import productcenter.models.Seo;

/**
 * Created by liubin on 15-5-13.
 */
public class ProductDetail {

    //商品描述部分
    private ProductDetailBase base;

    //评论总数量
    private int goodValuationCount;

    private int normalValuationCount;

    private int badValuationCount;

    private int allValuationCount;

    private Seo seo;


    public ProductDetailBase getBase() {
        return base;
    }

    public void setBase(ProductDetailBase base) {
        this.base = base;
    }

    public int getGoodValuationCount() {
        return goodValuationCount;
    }

    public void setGoodValuationCount(int goodValuationCount) {
        this.goodValuationCount = goodValuationCount;
    }

    public int getNormalValuationCount() {
        return normalValuationCount;
    }

    public void setNormalValuationCount(int normalValuationCount) {
        this.normalValuationCount = normalValuationCount;
    }

    public int getBadValuationCount() {
        return badValuationCount;
    }

    public void setBadValuationCount(int badValuationCount) {
        this.badValuationCount = badValuationCount;
    }

    public int getAllValuationCount() {
        return allValuationCount;
    }

    public void setAllValuationCount(int allValuationCount) {
        this.allValuationCount = allValuationCount;
    }

    public Seo getSeo() {
        return seo;
    }

    public void setSeo(Seo seo) {
        this.seo = seo;
    }


}
