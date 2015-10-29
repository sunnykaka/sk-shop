package base;

import common.utils.DateUtils;
import common.utils.Money;
import common.utils.RegExpUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.junit.Ignore;
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

//        String html = "<div class=\"top\">\n" +
//                "    \n" +
//                "        <span id=\"r_code\" style=\"display: none;\">OR5oSM3fRpUPgKamD2j/rg==</span>\n" +
//                "    \n" +
//                "    <div class=\"top-inner width1200\">";
//        Pattern pattern = Pattern.compile(".*<span id=\"r_code\".*>(.*)</span>.*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//        Matcher matcher = pattern.matcher(html);
//        if(!matcher.matches()) {
//            throw new AssertionError("注册页面没有返回r_code, html: " + html);
//        }
//        String group = matcher.group();
//        System.out.println(group);
//        System.out.println(matcher.group(1));

        String s = "<img src=\"http://imgs.fashiongeeker.net/9/4B697AAEC8654807B0D9FC8437C2A554.jpg\" border=\"0\" usemap=\"#Map\" alt=\"123\">";
        String result = s.replaceAll(
                "^<img.*?src=\"(.*?)\".*?(usemap=\"\\S*?\")?(\\s+alt=\".*?\")?>$",
                "<img class='lazy' data-origina l='$1' $2 src='/assets/images/grey.gif' style='display:block;' />");
        System.out.println(result);
    }


    @Test
    public void testSort(){
        List<Integer> list = new ArrayList<>();
        list.add(8);
        list.add(1);
        list.add(10);
        list.add(9);
        list.add(15);

        list.sort((n1,n2)->{
            if(n1 > n2 ){
                return 1;
            }
            if(n1 < n2){
                return -1;
            }
            return 0;
        });
        System.out.println(list);
    }
}
