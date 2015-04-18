package controllers.order;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.base.Joiner;
import common.services.GeneralDao;
import common.utils.IdUtils;
import common.utils.JsonUtils;
import common.utils.page.PageFactory;
import common.utils.play.PlayForm;
import ordercenter.constants.OrderStatus;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.services.OrderService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
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
        return ok(update.render(Form.form(Order.class).fill(orderService.get(id))));
    }

    public Result saveOrder() {

        Form<Order> form = PlayForm.form(Order.class).bindFromRequest();
        Order order = form.get();
        if(form.hasErrors()) {
            if(form.globalError() != null) {
                System.out.println("global error: " + form.globalError().message());
            }

            form.errors().forEach((k, v) -> System.out.println(
                    String.format("error key: %s, error value: %s", k,
                            Joiner.on("").join(v.stream().map(x -> x.toString()).collect(Collectors.toList())))));

            System.out.println(order == null);
            System.out.println(order.getBuyerId());

            return ok(add.render(form));
        } else {

            System.out.println(order.getBuyerId());
            System.out.println(order.getBuyTime());
            System.out.println(order.getStatus());
            System.out.println(order.getActualFee());
            if(!order.getOrderItemList().isEmpty()) {
                order.getOrderItemList().forEach(oi -> {
                    System.out.println(oi.getProductId());
                    System.out.println(oi.getProductSku());
                });
            }


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

    @BodyParser.Of(BodyParser.Json.class)
    public Result createOrderByJson() throws JsonProcessingException {
        JsonNode jsonNode = request().body().asJson();
        System.out.println(jsonNode == null ? "null json" : jsonNode.asText());
        try {
            Order order = JsonUtils.OBJECT_MAPPER.treeToValue(jsonNode, Order.class);

            System.out.println(order.getBuyerId());
            System.out.println(order.getBuyTime());
            System.out.println(order.getStatus());
            System.out.println(order.getActualFee());
            if(!order.getOrderItemList().isEmpty()) {
                order.getOrderItemList().forEach(oi -> {
                    System.out.println(oi.getProductId());
                    System.out.println(oi.getProductSku());
                });
            }


        } catch (JsonProcessingException e) {
            Logger.warn("bad json: " + jsonNode.asText(), e);
            badRequest("bad json");
        }

        return ok("{\"result\":\"ok\"}");
    }

    public Result viewOrderByJson(Integer orderId) throws JsonProcessingException {

        Order order = orderService.get(orderId);

        if(order == null) {
            return ok(JsonUtils.OBJECT_MAPPER.createObjectNode());
        } else {
            return ok(JsonUtils.OBJECT_MAPPER.convertValue(order, JsonNode.class));
        }

    }

}