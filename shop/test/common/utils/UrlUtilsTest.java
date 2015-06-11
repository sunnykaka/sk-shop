package common.utils;

import com.google.common.collect.Lists;
import org.junit.Test;
import play.Play;
import play.test.WithApplication;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static common.utils.UrlUtils.*;


/**
 * Created by liubin on 15-4-2.
 */
public class UrlUtilsTest{


    @Test
    public void test() throws Exception {

//        System.out.println(Base64.getEncoder().encodeToString(AES.initPasswordKey()));

        Map<String, List<String>> params = new HashMap<>();
        params.put("param1", new ArrayList<>());
        params.put("param2", Lists.newArrayList("1"));
        params.put("param3", Lists.newArrayList("1", "2"));
        params.put("param4", Lists.newArrayList("1 2", "3 4"));

        Map<String, List<String>> newParams = disposeQueryString(buildQueryString(params));

        assertThat(newParams.entrySet(), equalTo(params.entrySet()));

    }

}
