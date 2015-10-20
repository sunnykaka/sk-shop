package base;

import common.utils.DateUtils;
import common.utils.Money;
import common.utils.RegExpUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by liubin on 15-4-21.
 */
public class PlainTest {

    @Test
    public void test() {

//        System.out.println("18682000593".matches("^[1][\\d]{10}"));

//        List<String> list = new ArrayList<String>() {{
//            add("1");
//            add("1");
//            add("1");
//        }};
//
//        System.out.println(list.stream().collect(Collectors.toMap(k -> k, k -> k)));

//        System.out.println(DateUtils.printDeadlineFromNow(DateUtils.current().plusHours(3).plusDays(11)));

//        System.out.println(Money.valueOf(2.15d).toString());

//        String s = "<U+1F4B0>刘斌<U+1F4B0>";
//        System.out.println(StringEscapeUtils.escapeHtml4(s));
//        System.out.println(StringEscapeUtils.escapeHtml4(s).length());
//        System.out.println(s.replaceAll("[^(0-9a-zA-Z\\u4e00-\\u9fa5)]", ""));

//        Pattern p = Pattern.compile("<[^>]+>刘斌<[^>]+>");
//        System.out.println(s.matches(p.pattern()));

        String html = "<input type=\"hidden\" id=\"r_code\" name=\"r_code\" value=\"rYNkKDzU\"/>";
        Pattern pattern = Pattern.compile(".*name=\"r_code\" value=\"(\\w+)\".*");
        Matcher matcher = pattern.matcher(html);
        if(!matcher.matches()) {
            throw new AssertionError("注册页面没有返回r_code, html: " + html);
        }
        String group = matcher.group();
        System.out.println(group);
        System.out.println(matcher.group(1));

    }

}
