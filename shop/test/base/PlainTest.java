package base;

import common.utils.DateUtils;
import common.utils.Money;
import common.utils.RegExpUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
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

        System.out.println(RegExpUtils.isUsername("1"));

    }

}
