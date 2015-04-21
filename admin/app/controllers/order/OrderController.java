package controllers.order;


import common.utils.IdUtils;
import common.utils.page.PageFactory;
import common.utils.play.PlayForm;
import ordercenter.constants.OrderStatus;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.order.add;
import views.html.order.list;
import views.html.order.update;

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

    public Result addPage() {
        return ok(add.render(Form.form(Order.class)));
    }

    public Result updatePage(Integer id) {
        return ok(update.render(Form.form(Order.class).fill(orderService.get(id))));
    }

    public Result saveOrder() {

        Form<Order> form = PlayForm.form(Order.class).bindFromRequest();
        Order order = form.get();
        if(form.hasErrors()) {

            return ok(add.render(form));
        } else {


            if(IdUtils.isEmpty(order.getId())) {

                orderService.saveOrder(order);

            } else {

                //拷贝修改的数据
                Order orderInDb = orderService.get(order.getId());
                orderInDb.setActualFee(order.getActualFee());
                orderInDb.setBuyerId(order.getBuyerId());
                orderInDb.setBuyTime(order.getBuyTime());
                orderInDb.setStatus(order.getStatus());

                for(OrderItem oi : order.getOrderItemList()) {
                    for(OrderItem oiInDb : orderInDb.getOrderItemList()) {
                        if(oi.getId().equals(oiInDb.getId())) {
                            oiInDb.setProductSku(oi.getProductSku());
                            oiInDb.setProductId(oi.getProductId());
                        }
                    }
                }

                orderService.saveOrder(orderInDb);
            }


            return redirect(routes.OrderController.list(null, null));
        }
    }


}