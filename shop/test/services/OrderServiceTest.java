package services;

import base.BaseTest;
import base.CartTest;
import common.constants.MessageJobSource;
import common.models.MessageJob;
import common.services.MessageJobService;
import common.utils.DateUtils;
import common.utils.test.DbTest;
import controllers.user.LoginTest;
import ordercenter.constants.OrderState;
import ordercenter.models.CartItem;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.services.CartService;
import ordercenter.services.OrderService;
import org.junit.Test;
import productcenter.models.Product;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductTestDataService;
import productcenter.services.SkuAndStorageService;
import usercenter.constants.MarketChannel;
import usercenter.services.UserService;
import utils.Global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by liubin on 15-4-2.
 */
public class OrderServiceTest extends BaseTest implements CartTest {


    @Test
    public void testSubmitOrderSuccess() throws Exception {
        OrderService orderService = Global.ctx.getBean(OrderService.class);
        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);
        UserService userService = Global.ctx.getBean(UserService.class);
        CartService cartService = Global.ctx.getBean(CartService.class);

        Integer userId = mockUser();

        Product product = productTestDataService.initProduct();
        List<StockKeepingUnit> stockKeepingUnits = skuAndStorageService.querySkuListByProductId(product.getId());
        int addNumber = 1;
        List<Integer> cartItemList = stockKeepingUnits.stream().
                map(sku -> addSkuToCart(sku, addNumber, userId)).
                collect(Collectors.toList());

        Map<Integer, Integer> initialSkuStockQuantityMap =
                stockKeepingUnits.
                stream().
                collect(Collectors.toMap(
                        StockKeepingUnit::getId,
                        sku -> skuAndStorageService.getSkuStorage(sku.getId()).getStockQuantity()
                ));

        List<Integer> orderIds = orderService.submitOrder(
                userService.getById(userId),
                String.join("_", cartItemList.stream().map(String::valueOf).collect(Collectors.toList())),
                mockAddress(userId).getId(),
                MarketChannel.WEB
        );

        assertThat(orderIds.isEmpty(), is(false));
        assertThat(orderIds.size(), is(1));

        doInSingleSession(generalDao -> {

            //校验订单和订单项
            Order order = orderService.getOrderById(orderIds.get(0));

            assertThat(order.getCustomerId(), is(product.getCustomerId()));
            assertThat(order.getUserId(), is(userId));
            assertThat(order.getOrderState(), is(OrderState.Create));
            assertThat(order.getOrderItemList().size(), is(stockKeepingUnits.size()));

            for(OrderItem orderItem : order.getOrderItemList()) {
                assertThat(orderItem.getCustomerId(), is(product.getCustomerId()));
                //校验库存
                assertThat(orderItem.getSkuId() > 0, is(true));
                assertThat(orderItem.getSkuId() > 0, is(true));
                assertThat(initialSkuStockQuantityMap.containsKey(orderItem.getSkuId()), is(true));
                int stockQuantityNow = skuAndStorageService.getSkuStorage(orderItem.getSkuId()).getStockQuantity();
                int stockQuantityBefore = initialSkuStockQuantityMap.get(orderItem.getSkuId());
                assertThat(stockQuantityNow, is(stockQuantityBefore - addNumber));
            }

            return null;
        });

        //确保选中的购物车项在订单提交后都已经删除
        for(Integer cartItemId : cartItemList) {
            CartItem cartItem = cartService.getCartItem(cartItemId);
            assertThat(cartItem.getIsDelete(), is(true));
        }
        // 测试购物车添加多个产品后提交订单（多个设计师）

        // 测试立即购买

        //

    }


    private void testSubmitOrder(int userId, int productCount, int addNumber, boolean isPromptlyPay) {

        assertThat("直接购买的产品数量参数productCount必须为1", isPromptlyPay && productCount != 1, is(true));

        OrderService orderService = Global.ctx.getBean(OrderService.class);
        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);
        UserService userService = Global.ctx.getBean(UserService.class);
        CartService cartService = Global.ctx.getBean(CartService.class);

        List<Product> products = new ArrayList<>();
        List<StockKeepingUnit> stockKeepingUnits = new ArrayList<>();

        for (int i = 0; i < productCount; i++) {
            Product product = productTestDataService.initProduct();
            stockKeepingUnits.addAll(skuAndStorageService.querySkuListByProductId(product.getId()));

            products.add(product);
        }

        List<Integer> cartItemList = stockKeepingUnits.stream().
                map(sku -> addSkuToCart(sku, addNumber, userId)).
                collect(Collectors.toList());

        Map<Integer, Integer> initialSkuStockQuantityMap =
                stockKeepingUnits.
                        stream().
                        collect(Collectors.toMap(
                                StockKeepingUnit::getId,
                                sku -> skuAndStorageService.getSkuStorage(sku.getId()).getStockQuantity()
                        ));

        String selItems;
        if(isPromptlyPay) {
            selItems = String.format("%d:%d", stockKeepingUnits.get(0).getId(), addNumber);
        } else {
            selItems = String.join("_", cartItemList.stream().map(String::valueOf).collect(Collectors.toList()));
        }

        List<Integer> orderIds = orderService.submitOrder(
                userService.getById(userId),
                selItems,
                mockAddress(userId).getId(),
                MarketChannel.WEB
        );

        assertThat(orderIds.isEmpty(), is(false));
        assertThat(orderIds.size(), is(productCount));

        doInSingleSession(generalDao -> {

            for(Product product : products) {
                //校验订单和订单项
                Order order = orderService.getOrderById(orderIds.get(0));

                assertThat(order.getCustomerId(), is(product.getCustomerId()));
                assertThat(order.getUserId(), is(userId));
                assertThat(order.getOrderState(), is(OrderState.Create));
                assertThat(order.getOrderItemList().size(), is(stockKeepingUnits.size()));

                for(OrderItem orderItem : order.getOrderItemList()) {
                    assertThat(orderItem.getCustomerId(), is(product.getCustomerId()));
                    //校验库存
                    assertThat(orderItem.getSkuId() > 0, is(true));
                    assertThat(orderItem.getSkuId() > 0, is(true));
                    assertThat(initialSkuStockQuantityMap.containsKey(orderItem.getSkuId()), is(true));
                    int stockQuantityNow = skuAndStorageService.getSkuStorage(orderItem.getSkuId()).getStockQuantity();
                    int stockQuantityBefore = initialSkuStockQuantityMap.get(orderItem.getSkuId());
                    assertThat(stockQuantityNow, is(stockQuantityBefore - addNumber));
                }

            }

            return null;
        });

        //确保选中的购物车项在订单提交后都已经删除
        for(Integer cartItemId : cartItemList) {
            CartItem cartItem = cartService.getCartItem(cartItemId);
            assertThat(cartItem.getIsDelete(), is(true));
        }
    }
//
//    @Test
//    public void testCreateErrorMessageJobToExecute() throws Exception {
//        MessageJobService messageJobService = Global.ctx.getBean(MessageJobService.class);
//
//        MessageJob messageJob1 = messageJobService.sendEmail("sunnykaka@vip.qq.com", "", "你好啊", MessageJobSource.CHANGE_EMAIL, null);
//        MessageJob messageJob2 = messageJobService.sendSmsMessage("186", "您的短信验证码是123456，两小时内有效", MessageJobSource.REGISTER_VERIFICATION_CODE, null);
//
//        messageJobService.executeUnprocessedMessageJobs();
//
//        doInTransactionWithGeneralDao(generalDao -> {
//
//            MessageJob mj1 = generalDao.get(MessageJob.class, messageJob1.getId());
//            assertThat(mj1.isProcessed(), is(false));
//            assertThat(mj1.getProcessInfo(), notNullValue());
//
//            MessageJob mj2 = generalDao.get(MessageJob.class, messageJob2.getId());
//            assertThat(mj2.isProcessed(), is(false));
//            assertThat(mj2.getProcessInfo(), notNullValue());
//
//            return null;
//        });
//
//    }
//
//    @Test
//    public void testSendMessageInTargetTime() throws Exception {
//        MessageJobService messageJobService = Global.ctx.getBean(MessageJobService.class);
//
//        MessageJob messageJob1 = messageJobService.sendEmail("sunnykaka@vip.qq.com", "你好", "你好啊", MessageJobSource.CHANGE_EMAIL, DateUtils.current().plusSeconds(3));
//
//        messageJobService.executeUnprocessedMessageJobs();
//
//        doInTransactionWithGeneralDao(generalDao -> {
//
//            MessageJob mj1 = generalDao.get(MessageJob.class, messageJob1.getId());
//            assertThat(mj1.isProcessed(), is(false));
//            assertThat(mj1.getProcessInfo(), nullValue());
//
//            return null;
//        });
//
//        Thread.sleep(4000L);
//        messageJobService.executeUnprocessedMessageJobs();
//
//        doInTransactionWithGeneralDao(generalDao -> {
//
//            MessageJob mj1 = generalDao.get(MessageJob.class, messageJob1.getId());
//            assertThat(mj1.isProcessed(), is(true));
//            assertThat(mj1.getProcessInfo(), nullValue());
//            assertThat(mj1.getUpdateTime(), notNullValue());
//            assertThat(mj1.getUpdateTime().isAfter(mj1.getCreateTime()), is(true));
//
//            return null;
//        });
//
//    }

}
