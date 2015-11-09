package services;

import base.BaseTest;
import base.CartTest;
import common.utils.DateUtils;
import common.utils.Money;
import ordercenter.constants.VoucherStatus;
import ordercenter.constants.VoucherType;
import ordercenter.excepiton.VoucherException;
import ordercenter.models.Voucher;
import ordercenter.models.VoucherBatch;
import ordercenter.services.VoucherService;
import org.joda.time.DateTime;
import org.junit.Test;
import play.Logger;
import utils.Global;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static java.util.Optional.*;


/**
 * Created by liubin on 15-4-2.
 */
public class VoucherServiceTest extends BaseTest implements CartTest {


    @Test
    public void testRequestForActivitySuccess() throws Exception {

        Integer userId = mockUser();

        Money voucherAmount = Money.valueOf((new java.util.Random().nextInt(10) + 1) * 0.05d);
        DateTime now = DateUtils.current();
        Optional<Integer> period = of(3);
        Optional<DateTime> deadline = of(now.plusDays(2));

        //活动请求，代金券只有有效期没有截止日期
        VoucherBatch voucherBatch = initVoucherBatch(VoucherType.RECEIVE_BY_ACTIVITY, voucherAmount,
                now, empty(), period, empty());
        assertRequestVouchersSuccess(voucherBatch, userId);

        //活动请求，代金券有截止日期
        voucherBatch = initVoucherBatch(VoucherType.RECEIVE_BY_ACTIVITY, voucherAmount,
                now, empty(), period, deadline);
        assertRequestVouchersSuccess(voucherBatch, userId);

    }

    private void assertRequestVouchersSuccess(VoucherBatch voucherBatch, Integer userId) {

        VoucherService voucherService = Global.ctx.getBean(VoucherService.class);

        List<Voucher> voucherList;
        int quantity = 3;
        voucherList = voucherService.requestForActivity(Optional.of(voucherBatch.getUniqueNo()), userId, quantity);

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
    }

    private VoucherBatch initVoucherBatch(VoucherType type, Money voucherAmount, DateTime startTime,
                                          Optional<DateTime> endTime, Optional<Integer> period, Optional<DateTime> deadline) {

        VoucherService voucherService = Global.ctx.getBean(VoucherService.class);

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
        try {
            voucherService.createVoucherBatch(voucherBatch);
        } catch (VoucherException e) {
            Logger.warn(e.getMessage());
            if(voucherBatch.getId() == null) {
                //数据库已经存在用户注册代金券活动
                voucherBatch = voucherService.findValidVoucherBatch(Optional.empty(), VoucherType.FIRE_BY_REGISTER);
            }
        }

        //新建的代金券活动状态为无效，手动改为有效
        voucherService.updateVoucherBatchToValid(voucherBatch.getId());

        return voucherBatch;
    }


}
