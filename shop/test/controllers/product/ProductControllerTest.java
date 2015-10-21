package controllers.product;

import base.BaseTest;
import common.utils.test.DbTest;
import common.utils.DateUtils;
import common.utils.JsonResult;
import common.utils.page.Page;
import ordercenter.models.Valuation;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class ProductControllerTest extends BaseTest implements DbTest {

    @Test
    public void testListProductValuations() throws Exception {

        int size = 100;
        int fakeProductId = Integer.parseInt(RandomStringUtils.randomNumeric(8));
        prepareValuations(size, Optional.of(fakeProductId));


        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(routes.ProductController.valuations(fakeProductId, null).url());
        Result result = route(request);
        Logger.debug(" ProductController.valuations result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(nullValue()));

        Map page = (Map) jsonResult.getData();
        assertThat(page.get("totalCount"), is(size));
        List results = (List)(page.get("result"));
        assertThat(results.size(), is(Page.DEFAULT_PAGE_SIZE));

        int allTotal = 0;

        for(int i=0; i<3; i++) {
            request = new Http.RequestBuilder().method(GET).uri(routes.ProductController.valuations(fakeProductId, i).url());
            result = route(request);
            assertThat(result.status(), is(OK));
            jsonResult = JsonResult.fromJson(contentAsString(result));
            page = (Map) jsonResult.getData();
            int totalCount = (Integer)page.get("totalCount");
            allTotal += totalCount;
        }

        assertThat(allTotal, is(size));

    }

    private void prepareValuations(int size, Optional<Integer> fakeProductId) {

        doInTransactionWithGeneralDao(generalDao -> {

            for (int i = 0; i < size; i++) {
                Valuation valuation = new Valuation();
                valuation.setAppendContent("追加内容");
                valuation.setAppendDate(DateUtils.current());
                valuation.setAppendOperator("追加Operator");
                valuation.setAppendReplyContent("追加回复内容");
                valuation.setAppendReplyDate(DateUtils.current());
                valuation.setContent("我是评论内容" + RandomStringUtils.randomAlphanumeric(8));
                valuation.setCreateDate(DateUtils.current());
                valuation.setOrderCreateDate(DateUtils.current());
                List<Integer> list = new ArrayList<>();
                list.addAll(Valuation.pointName.keySet());
                valuation.setPoint(list.get(new Random().nextInt(list.size())));
                if(fakeProductId.isPresent()) {
                    valuation.setProductId(fakeProductId.get());
                }
                valuation.setUserName("张三userName");
                valuation.setOperator("张三Operator");
                valuation.setReplyContent("回复内容");
                valuation.setReplyTime(DateUtils.current());
                valuation.setUpdateDate(DateUtils.current());

                generalDao.persist(valuation);

            }

            return null;
        });


    }


}
