package controllers;

import common.utils.JsonResult;
import common.utils.test.BaseTest;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class ApplicationTest extends WithApplication implements BaseTest {


    @Test
    @Ignore
    public void testUserLikeExhibition() throws Exception {

        int exhibitionId = 25;

        FakeRequest request = new FakeRequest(POST, routes.Application.userLikeExhibition(exhibitionId, "186820005933").url());
        Result result = route(request);
        assertThat(status(result), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(false));
        assertThat(jsonResult.getData(), is(nullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));

        request = new FakeRequest(POST, routes.Application.userLikeExhibition(exhibitionId, "18682000593").url());
        result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentType(result), is("application/json"));
        assertThat(contentAsString(result), containsString("true"));

        request = new FakeRequest(POST, routes.Application.userLikeExhibition(exhibitionId, "18682000593").url());
        result = route(request);
        assertThat(status(result), is(OK));
        jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(false));
        assertThat(jsonResult.getData(), is(nullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));

    }


}
