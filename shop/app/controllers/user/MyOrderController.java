package controllers.user;

import common.utils.page.Page;
import common.utils.page.PageFactory;
import ordercenter.constants.OrderState;
import ordercenter.models.Logistics;
import ordercenter.models.Order;
import ordercenter.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.models.User;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.user.myOrder;
import views.html.user.myOrderInfo;

import java.util.List;
import java.util.Optional;

/**
 * 我的订单管理
 *
 * Created by zhb on 15-5-7.
 */
@org.springframework.stereotype.Controller
public class MyOrderController extends Controller {

    public static final int user_id = 14303;

    @Autowired
    private OrderService orderService;

    /**
     * 单订管理首页
     *
     * @param queryType
     *          0所有订单、1待付款、2待发货、3待收货、4待评价
     * @param orderState
     * @return
     */
    public Result index(int queryType,String orderState){
        //User user = SessionUtils.currentUser();

        Page<Order> page = PageFactory.getPage(request());
        page.setPageSize(10);

        List<Order> orderList = orderService.getOrderByUserId(Optional.of(page), user_id, queryType, orderState);
        for(Order order:orderList){

            Logistics logistics = orderService.getLogisticsByOrderId(order.getId());
            if(null != logistics){
                order.setAddressName(logistics.getName());
            }else{
                order.setAddressName(order.getUserName());
            }

        }

        page.setResult(orderList);


        return ok(myOrder.render(page));

    }

    /**
     * 订单详情页面
     *
     * @param orderId
     * @return
     */
    //@SecuredAction
    public Result orderContent(int orderId){
        //User user = SessionUtils.currentUser();

        Order order = orderService.getOrderById(orderId,user_id);
        Logistics logistics = orderService.getLogisticsByOrderId(order.getId());

        return ok(myOrderInfo.render(order,logistics));

    }

    /**
     * 申请退货页面
     *
     * @param orderId
     * @return
     */
//    @SecuredAction
    public Result backApply(int orderId){
//        User user = SessionUtils.currentUser();

        return ok();

    }

    /**
     * 售后服务首页
     *
     * @return
     */
//    @SecuredAction
    public Result backIndex(){
//        User user = SessionUtils.currentUser();

        return ok();

    }

    /**
     * 售后详情
     *
     * @return
     */
//    @SecuredAction
    public Result backContent(){
//        User user = SessionUtils.currentUser();

        return ok();

    }

}
