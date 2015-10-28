package services;

import base.BaseTest;
import base.CartTest;
import ordercenter.constants.OrderState;
import ordercenter.models.CartItem;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.services.CartService;
import ordercenter.services.OrderService;
import org.junit.Test;
import play.Logger;
import productcenter.models.Product;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductTestDataService;
import productcenter.services.SkuAndStorageService;
import usercenter.constants.MarketChannel;
import usercenter.services.UserService;
import utils.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by liubin on 15-4-2.
 */
public class OrderServiceTest extends BaseTest implements CartTest {


    @Test
    public void testSubmitOrderSuccess() throws Exception {

        Integer userId = mockUser();

        //购物车中选中1个产品，数量为1提交订单
        testSubmitOrder(userId, 1, 1, false);

        //购物车中选中1个产品，数量为2提交订单
        testSubmitOrder(userId, 1, 2, false);

        //购物车中选中3个产品，每个数量为1提交订单
        testSubmitOrder(userId, 3, 1, false);

        //购物车中选中3个产品，每个数量为2提交订单
        testSubmitOrder(userId, 3, 2, false);

        //1个产品，数量为1立即购买
        testSubmitOrder(userId, 1, 1, true);

        //1个产品，数量为5立即购买
        testSubmitOrder(userId, 1, 5, true);

    }

    /**
     * 测试订单提交
     * @param userId 用户ID
     * @param productCount 构造几个产品用于提交订单，每个产品因为属于单独的设计师，会对应一个单独的订单
     * @param addNumber  购物车中的sku添加数量
     * @param isPromptlyPay 是否直接购买，如果为true，productCount需要传1
     */
    private void testSubmitOrder(int userId, int productCount, int addNumber, boolean isPromptlyPay) {

        Logger.debug(String.format("testSubmitOrder, 参数: userId[%d], productCount[%d], addNumber[%d], isPromptlyPay[%b]",
                userId, productCount, addNumber, isPromptlyPay));

        if(isPromptlyPay) {
            assertThat("直接购买的产品数量参数productCount必须为1", productCount == 1, is(true));
        }

        OrderService orderService = Global.ctx.getBean(OrderService.class);
        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);
        UserService userService = Global.ctx.getBean(UserService.class);
        CartService cartService = Global.ctx.getBean(CartService.class);

        List<Product> products = new ArrayList<>();
        List<StockKeepingUnit> stockKeepingUnits = new ArrayList<>();

        for (int i = 0; i < productCount; i++) {
            //初始化产品
            Product product = productTestDataService.initProduct();
            if(isPromptlyPay) {
                //直接购买第一个sku
                stockKeepingUnits.add(skuAndStorageService.querySkuListByProductId(product.getId()).get(0));
            } else {
                stockKeepingUnits.addAll(skuAndStorageService.querySkuListByProductId(product.getId()));
            }

            products.add(product);
        }

        //得到订单提交之前的库存, key为skuId, value为库存数量
        Map<Integer, Integer> initialSkuStockQuantityMap =
                stockKeepingUnits.
                        stream().
                        collect(Collectors.toMap(
                                StockKeepingUnit::getId,
                                sku -> skuAndStorageService.getSkuStorage(sku.getId()).getStockQuantity()
                        ));


        List<Integer> cartItemList = null;
        String selItems;

        if(isPromptlyPay) {
            selItems = String.format("%d:%d", stockKeepingUnits.get(0).getId(), addNumber);

        } else {
            //添加sku到购物车
            cartItemList = stockKeepingUnits.stream().
                    map(sku -> addSkuToCart(sku, addNumber, userId)).
                    collect(Collectors.toList());

            selItems = String.join("_", cartItemList.stream().map(String::valueOf).collect(Collectors.toList()));
        }

        //提交订单
        List<Integer> orderIds = orderService.submitOrder(
                userService.getById(userId),
                selItems,
                mockAddress(userId).getId(),
                MarketChannel.WEB
        );

        assertThat(orderIds.isEmpty(), is(false));
        assertThat(orderIds.size(), is(productCount));

        doInSingleSession(generalDao -> {

            int allOrderItemCount = 0;

            for (int i = 0; i < orderIds.size(); i++) {
                //校验订单
                Order order = orderService.getOrderById(orderIds.get(i));
                Product product = products.get(i);

                assertThat(order.getCustomerId(), is(product.getCustomerId()));
                assertThat(order.getUserId(), is(userId));
                assertThat(order.getOrderState(), is(OrderState.Create));

                for(OrderItem orderItem : order.getOrderItemList()) {
                    assertThat(orderItem.getCustomerId(), is(product.getCustomerId()));
                    //校验库存
                    assertThat(orderItem.getSkuId() > 0, is(true));
                    assertThat(orderItem.getSkuId() > 0, is(true));
                    assertThat(initialSkuStockQuantityMap.containsKey(orderItem.getSkuId()), is(true));
                    int stockQuantityNow = skuAndStorageService.getSkuStorage(orderItem.getSkuId()).getStockQuantity();
                    int stockQuantityBefore = initialSkuStockQuantityMap.get(orderItem.getSkuId());
                    assertThat(stockQuantityNow, is(stockQuantityBefore - addNumber));

                    allOrderItemCount++;
                }
            }

            assertThat(allOrderItemCount, is(stockKeepingUnits.size()));

            return null;
        });

        //确保选中的购物车项在订单提交后都已经删除
        if(!isPromptlyPay) {
            for(Integer cartItemId : cartItemList) {
                CartItem cartItem = cartService.getCartItem(cartItemId);
                assertThat(cartItem.getIsDelete(), is(true));
            }
        }
    }

}
