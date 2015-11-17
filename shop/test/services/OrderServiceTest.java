package services;

import base.BaseTest;
import base.CartTest;
import base.VoucherTest;
import common.utils.DateUtils;
import common.utils.Money;
import ordercenter.constants.OrderState;
import ordercenter.constants.VoucherStatus;
import ordercenter.constants.VoucherType;
import ordercenter.excepiton.VoucherException;
import ordercenter.models.*;
import ordercenter.services.CartService;
import ordercenter.services.OrderService;
import ordercenter.services.VoucherService;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by liubin on 15-4-2.
 */
public class OrderServiceTest extends BaseTest implements CartTest, VoucherTest {

    /**
     * 测试运行之前将数据库所有的代金券活动状态置为无效
     */
    @Before
    @After
    public void init() {
        setAllVoucherBatchToInvalid();
    }

    @Test
    public void testSubmitOrderSuccess() throws Exception {

        Integer userId = mockUser();

        //购物车中选中1个产品，数量为1提交订单
        testSubmitOrder(userId, 1, 1, false, null);

        //购物车中选中1个产品，数量为2提交订单
        testSubmitOrder(userId, 1, 2, false, null);

        //购物车中选中3个产品，每个数量为1提交订单
        testSubmitOrder(userId, 3, 1, false, null);

        //购物车中选中3个产品，每个数量为2提交订单
        testSubmitOrder(userId, 3, 2, false, null);

        //1个产品，数量为1立即购买
        testSubmitOrder(userId, 1, 1, true, null);

        //1个产品，数量为5立即购买
        testSubmitOrder(userId, 1, 5, true, null);

    }

    @Test
    public void testSubmitOrderWithVouchers() throws Exception {

        Integer userId = mockUser();

        DateTime now = DateUtils.current();
        Optional<Integer> period = of(3);
        int quantity = 1;

        //这个列表包含各类型的代金券各一张
        List<Voucher> allTypeVouchers = new ArrayList<>();

        //为每种类型都运行测试
        for(VoucherType type : VoucherType.values()) {
            VoucherBatch voucherBatch = initVoucherBatchWithValid(type, now, empty(), period, empty(), empty());
            List<Voucher> vouchers = assertRequestVouchersSuccess(voucherBatch, userId, quantity);
            //购物车中选中1个产品，数量为1提交订单
            testSubmitOrder(userId, 1, 1, false, vouchers);
            setAllVoucherBatchToInvalid();

            voucherBatch = initVoucherBatchWithValid(type, now, empty(), period, empty(), empty());
            vouchers = assertRequestVouchersSuccess(voucherBatch, userId, quantity);
            //购物车中选中3个产品，每个数量为2提交订单
            testSubmitOrder(userId, 3, 2, false, vouchers);
            setAllVoucherBatchToInvalid();

            voucherBatch = initVoucherBatchWithValid(type, now, empty(), period, empty(), empty());
            vouchers = assertRequestVouchersSuccess(voucherBatch, userId, quantity);
            //1个产品，数量为5立即购买
            testSubmitOrder(userId, 1, 5, true, vouchers);
            setAllVoucherBatchToInvalid();

            voucherBatch = initVoucherBatchWithValid(type, now, empty(), period, empty(), empty());
            vouchers = assertRequestVouchersSuccess(voucherBatch, userId, quantity);
            allTypeVouchers.addAll(vouchers);
        }

        //购物车中选中3个产品，每个数量为2提交订单
        testSubmitOrder(userId, 3, 2, false, allTypeVouchers);
    }

    @Test
    public void testSubmitOrderWithInvalidVouchers() throws Exception {

        Integer userId = mockUser();
        Integer userId2 = mockUser();

        DateTime now = DateUtils.current();
        Optional<Integer> period = of(3);
        int quantity = 3;

        //为每种类型都运行测试
        for(VoucherType type : VoucherType.values()) {
            VoucherBatch voucherBatch = initVoucherBatchWithValid(type, now, empty(), period, empty(), empty());
            List<Voucher> vouchers = assertRequestVouchersSuccess(voucherBatch, userId2, quantity);
            setAllVoucherBatchToInvalid();
            boolean exceptionThrown = false;
            try {
                //使用其他人的优惠券
                testSubmitOrder(userId, 1, 1, false, vouchers);
            } catch (VoucherException expected) {
                Logger.debug(expected.getMessage());
                exceptionThrown = true;
            }
            if(!exceptionThrown) {
                throw new AssertionError("预期抛出VoucherException，但是没有异常抛出");
            }

            voucherBatch = initVoucherBatchWithValid(type, now, empty(), period, empty(), empty());
            vouchers = assertRequestVouchersSuccess(voucherBatch, userId, quantity);
            setAllVoucherBatchToInvalid();
            exceptionThrown = false;
            try {
                //同种类型的优惠券使用多张
                testSubmitOrder(userId, 3, 2, false, vouchers);
            } catch (VoucherException expected) {
                Logger.debug(expected.getMessage());
                exceptionThrown = true;
            }
            if(!exceptionThrown) {
                throw new AssertionError("预期抛出VoucherException，但是没有异常抛出");
            }

        }
    }


    /**
     * 测试订单提交
     * @param userId 用户ID
     * @param productCount 构造几个产品用于提交订单，每个产品因为属于单独的设计师，会对应一个单独的订单
     * @param addNumber  购物车中的sku添加数量
     * @param isPromptlyPay 是否直接购买，如果为true，productCount需要传1
     * @param vouchers
     */
    private void testSubmitOrder(int userId, int productCount, int addNumber, boolean isPromptlyPay, List<Voucher> vouchers) {

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
        VoucherService voucherService = Global.ctx.getBean(VoucherService.class);

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

        //代金券
        List<String> voucherUniqueNoList = new ArrayList<>();
        Money voucherAmount = null;
        if(vouchers != null && !vouchers.isEmpty()) {
            voucherUniqueNoList = vouchers.stream().map(Voucher::getUniqueNo).collect(Collectors.toList());
            voucherAmount = vouchers.stream().map(Voucher::getAmount).reduce(Money.valueOf(0d), Money::add);
        }
        final Money fVoucherAmount = voucherAmount;

        //提交订单
        List<Integer> orderIds = orderService.submitOrder(
                userService.getById(userId),
                selItems,
                mockAddress(userId).getId(),
                MarketChannel.WEB,
                voucherUniqueNoList);

        assertThat(orderIds.isEmpty(), is(false));
        assertThat(orderIds.size(), is(productCount));

        doInSingleSession(generalDao -> {

            int allOrderItemCount = 0;
            Money allOrderItemTotalMoney = Money.valueOf(0d);
            Money allOrderMoney = Money.valueOf(0d);

            for (int i = 0; i < orderIds.size(); i++) {
                //校验订单
                Order order = orderService.getOrderById(orderIds.get(i));
                allOrderMoney = allOrderMoney.add(order.calcTotalMoney());
                allOrderItemTotalMoney = allOrderItemTotalMoney.add(order.calcItemMoney());
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

                if(vouchers != null && !vouchers.isEmpty()) {
                    List<VoucherUse> voucherUseList = order.getVoucherUseList();
                    assertThat(voucherUseList.size(), is(vouchers.size()));
                }
            }

            assertThat(allOrderItemCount, is(stockKeepingUnits.size()));

            if(vouchers != null && !vouchers.isEmpty()) {

                //订单金额 = 订单项金额之和 - 代金券金额
                assertThat(allOrderMoney, is(allOrderItemTotalMoney.subtract(fVoucherAmount)));

                List<Voucher> newVouchers = vouchers.stream().map(v -> voucherService.getVoucher(v.getId())).collect(Collectors.toList());
                boolean allUsed = newVouchers.stream().allMatch(v -> v.getStatus().equals(VoucherStatus.USED));
                assertThat(allUsed, is(true));
                boolean voucherMappedToAllOrder = newVouchers.stream().allMatch(v -> v.getVoucherUseList().size() == orderIds.size());
                assertThat(voucherMappedToAllOrder, is(true));
            }

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