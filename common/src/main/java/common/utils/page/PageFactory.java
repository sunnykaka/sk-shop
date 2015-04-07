package common.utils.page;


import org.apache.commons.lang3.StringUtils;
import play.mvc.Http;

import java.util.Map;

/**
 * User: liubin
 * Date: 14-3-11
 */
public class PageFactory {

    public static Page getPage(Http.Request request) {
        Map<String, String[]> params = request.body().asFormUrlEncoded();
        String[] pages = params.getOrDefault("page", new String[0]);
        String[] limit = params.getOrDefault("limit", new String[0]);
        int pageNo = (pages.length != 0 && StringUtils.isNumeric(pages[0])) ? Integer.parseInt(pages[0]) : 1;
        int pageSize = (limit.length != 0 && StringUtils.isNumeric(limit[0])) ? Integer.parseInt(limit[0]) : Page.DEFAULT_PAGE_SIZE;
        return new Page(pageNo, pageSize);
    }

}
