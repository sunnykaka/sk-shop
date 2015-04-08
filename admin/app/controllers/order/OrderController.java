package controllers.order;


import common.utils.page.PageFactory;
import ordercenter.constants.OrderStatus;
import ordercenter.models.Order;
import ordercenter.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.order.list;

import java.util.List;

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
//    public Result addBar() {
//        Form<Bar> form = Form.form(Bar.class).bindFromRequest();
//        Bar bar = form.get();
//        barService.addBar(bar);
//        return redirect(controllers.routes.Application.index());
//    }
//
//    public Result listBars() {
//        return ok(Json.toJson(barService.getAllBars()));
//    }
    
}