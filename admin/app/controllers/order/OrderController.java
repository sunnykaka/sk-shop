package controllers.order;


import com.google.common.base.Joiner;
import common.utils.page.PageFactory;
import ordercenter.constants.OrderStatus;
import ordercenter.models.Order;
import ordercenter.services.OrderService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.order.add;
import views.html.order.list;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.*;

@org.springframework.stereotype.Controller
public class OrderController extends Controller {

    @Autowired
    private OrderService orderService;


    public Result list(String status, String orderNo) {

        List<Order> orders = orderService.findByKey(of(PageFactory.getPage(request())), ofNullable(orderNo),
                ofNullable(status).map(OrderStatus::valueOf), empty(), empty());

        return ok(list.render(orders));
    }

    public Result addPage() {
        return ok(add.render(Form.form(Order.class)));
    }

    public Result saveOrder() {

        Form<Order> form = Form.form(Order.class).bindFromRequest();
        if(form.hasErrors()) {
            form.errors().forEach((k, v) -> System.out.println(
                    String.format("error key: %s, error value: %s", k,
                            Joiner.on("").join(v.stream().map(x -> x.toString()).collect(Collectors.toList())))));

            return ok(add.render(form));
        } else {

            System.out.println(form.get().getBuyerId());
            System.out.println(form.get().getBuyTime());
            System.out.println(form.get().getStatus());
            System.out.println(form.get().getActualFee());
            return redirect(routes.OrderController.list(null, null));
        }
    }

}