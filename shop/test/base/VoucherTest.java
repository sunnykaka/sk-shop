package base;

import common.utils.DateUtils;
import common.utils.Money;
import controllers.user.LoginTest;
import ordercenter.constants.VoucherBatchStatus;
import ordercenter.constants.VoucherStatus;
import ordercenter.constants.VoucherType;
import ordercenter.models.Voucher;
import ordercenter.models.VoucherBatch;
import ordercenter.services.VoucherService;
import org.joda.time.DateTime;
import utils.Global;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by liubin on 15-10-22.
 */
public interface VoucherTest extends LoginTest {

    /**
     * 子类需要在@Before和@After中调用这个方法
     */
    default void setAllVoucherBatchToInvalid() {
        doInTransactionWithGeneralDao(generalDao -> {
            Map<String, Object> params = new HashMap<>();
            params.put("status", VoucherBatchStatus.INVALID);
            generalDao.update("update VoucherBatch vb set vb.status = :status", params);
            return null;
        });
    }


    /**
     * 初始化代金券活动, 同时将其状态置为VALID
     * @param type
     * @param startTime
     * @param endTime
     * @param period
     * @param deadline
     * @param maxQuantity
     * @return
     */
    default VoucherBatch initVoucherBatchWithValid(VoucherType type, DateTime startTime, Optional<DateTime> endTime,
                                                   Optional<Integer> period, Optional<DateTime> deadline, Optional<Integer> maxQuantity) {

        VoucherService voucherService = Global.ctx.getBean(VoucherService.class);

        VoucherBatch voucherBatch = initVoucherBatch(type, startTime, endTime, period, deadline, maxQuantity);
        //新建的代金券活动状态为无效，手动改为有效
        voucherService.updateVoucherBatchToValid(voucherBatch.getId());

        return voucherBatch;
    }

    /**
     * 初始化代金券活动
     * @param type
     * @param startTime
     * @param endTime
     * @param period
     * @param deadline
     * @param maxQuantity
     * @return
     */
    default VoucherBatch initVoucherBatch(VoucherType type, DateTime startTime, Optional<DateTime> endTime,
                                          Optional<Integer> period, Optional<DateTime> deadline, Optional<Integer> maxQuantity) {

        VoucherService voucherService = Global.ctx.getBean(VoucherService.class);

        //测试生成的代金券面额可能为0.1, 0.2元,  面额不能太大. 因为产品单价可能是0.5元, 如果代金券数量是3, 面额是0.2, 有可能大于订单价格
        Money voucherAmount = Money.valueOf((new java.util.Random().nextInt(2) + 1) * 0.1d);

        VoucherBatch voucherBatch = new VoucherBatch();
        voucherBatch.setType(type);
        voucherBatch.setAmount(voucherAmount);
        voucherBatch.setStartTime(startTime);
        if(period.isPresent()) {
            voucherBatch.setPeriodDay(period.get());
        }
        if(deadline.isPresent()) {
            voucherBatch.setDeadline(deadline.get());
        }
        if(endTime.isPresent()) {
            voucherBatch.setEndTime(endTime.get());
        }
        if(maxQuantity.isPresent()) {
            voucherBatch.setMaxQuantity(maxQuantity.get());
        }

        voucherService.createVoucherBatch(voucherBatch);

        return voucherBatch;
    }

    /**
     * 根据代金券活动请求代金券
     * @param voucherBatch
     * @param userId
     * @param quantity
     * @return
     */
    default List<Voucher> assertRequestVouchersSuccess(VoucherBatch voucherBatch, Integer userId, int quantity) {

        List<Voucher> voucherList = requestVoucherByType(voucherBatch.getType(), userId, voucherBatch.getUniqueNo(), quantity);

        assertThat(voucherList.size(), is(quantity));
        for(Voucher voucher : voucherList) {
            assertThat(voucher.getAmount(), is(voucherBatch.getAmount()));
            if(voucherBatch.getDeadline() != null) {
                assertThat(DateUtils.isBeforeOrEqualWithDateTruncate(voucher.getDeadline(),
                        voucherBatch.getDeadline()), is(true));
            } else {
                assertThat(DateUtils.isBeforeOrEqualWithDateTruncate(voucher.getDeadline(),
                        voucherBatch.getStartTime().plusDays(voucherBatch.getPeriodDay())), is(true));
            }
            assertThat(voucher.getMinOrderAmount(), is(voucherBatch.getMinOrderAmount()));
            assertThat(voucher.getStatus(), is(VoucherStatus.UNUSED));
        }

        return voucherList;
    }

    /**
     * 根据类型请求代金券
     * @param type
     * @param userId
     * @param uniqueNo
     * @param quantity
     * @return
     */
    default List<Voucher> requestVoucherByType(VoucherType type, Integer userId, String uniqueNo, int quantity) {

        VoucherService voucherService = Global.ctx.getBean(VoucherService.class);

        switch (type) {
            case FIRE_BY_REGISTER: {
                return voucherService.requestForRegister(userId, quantity);
            }
            case RECEIVE_BY_ACTIVITY: {
                return voucherService.requestForActivity(of(uniqueNo), userId, quantity);
            }
            default: {
                throw new AssertionError("未知的voucher type: " + type);
            }
        }

    }


}
