package controllers.api.product;

import api.response.product.DesignerSizeDto;
import api.response.product.ProductDetailDto;
import api.response.product.ProductDto;
import api.response.user.DesignerDto;
import base.BaseTest;
import common.utils.JsonUtils;
import controllers.api.user.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import productcenter.dtos.SkuCandidate;
import productcenter.dtos.SkuInfo;
import productcenter.models.Product;
import productcenter.services.ProductTestDataService;
import usercenter.models.Designer;
import utils.Global;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.mvc.Http.Status.NO_CONTENT;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;


/**
 * Created by liubin on 15-4-2.
 */
public class ProductApiControllerTest extends BaseTest{

    @Test
    public void testRequestProductSuccess() throws Exception {

        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        Product product = productTestDataService.initProduct();

        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(routes.ProductApiController.detail(String.valueOf(product.getId())).url());
        Result result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(OK));
        assertThat(contentAsString(result), is(""));

        ProductDetailDto productDetailDto = JsonUtils.json2Object(contentAsString(result), ProductDetailDto.class);

        ProductDto productDto = productDetailDto.getProduct();
        assertThat(productDto, notNullValue());
        assertThat(productDto.getName(), is(product.getName()));
        assertThat(productDto.getTagType(), is(product.getTagType()));
        assertThat(productDto.getDescription(), is(product.getDescription()));
        DesignerDto designerDto = productDto.getDesigner();
        assertThat(designerDto, notNullValue());
        assertThat(designerDto.getName(), is(product.getCustomer().getName()));
        assertThat(designerDto.getId(), is(product.getCustomer().getId()));

        DesignerSizeDto designerSizeDto = productDetailDto.getDesignerSize();
        assertThat(designerSizeDto, notNullValue());
        assertThat(designerSizeDto.getId(), is(product.getDesignerSizeId()));

        SkuInfo defaultSku = productDetailDto.getDefaultSku();
        assertThat(defaultSku, notNullValue());

        List<SkuCandidate> skuCandidateList = productDetailDto.getSkuCandidateList();
        assertThat(skuCandidateList, notNullValue());
        assertThat(skuCandidateList.size() > 0, is(true));

        Map<String, SkuInfo> skuMap = productDetailDto.getSkuMap();
        assertThat(skuMap, notNullValue());
        assertThat(skuMap.size() > 0, is(true));

    }



}
