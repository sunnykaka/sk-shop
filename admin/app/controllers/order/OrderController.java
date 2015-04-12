package controllers.order;


import com.google.common.base.Joiner;
import common.services.GeneralDao;
import common.utils.page.PageFactory;
import common.utils.play.PlayForm;
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
import views.html.order.update;

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

    public Result updatePage(Integer id) {
        System.out.println("==========in update" + Thread.currentThread());
        return ok(update.render(Form.form(Order.class).fill(orderService.get(id))));
    }

    public Result saveOrder() {

        Form<Order> form = PlayForm.form(Order.class).bindFromRequest();
        if(form.hasErrors()) {
            if(form.globalError() != null) {
                System.out.println("global error: " + form.globalError().message());
            }

            form.errors().forEach((k, v) -> System.out.println(
                    String.format("error key: %s, error value: %s", k,
                            Joiner.on("").join(v.stream().map(x -> x.toString()).collect(Collectors.toList())))));

            System.out.println(form.get() == null);
            System.out.println(form.get().getBuyerId());

            return ok(add.render(form));
        } else {

            System.out.println(form.get().getBuyerId());
            System.out.println(form.get().getBuyTime());
            System.out.println(form.get().getStatus());
            System.out.println(form.get().getActualFee());
            if(!form.get().getOrderItemList().isEmpty()) {
                form.get().getOrderItemList().forEach(oi -> {
                    System.out.println(oi.getProductId());
                    System.out.println(oi.getProductSku());
                });
            }



            orderService.saveOrder(form.get());

            return redirect(routes.OrderController.list(null, null));
        }
    }

}