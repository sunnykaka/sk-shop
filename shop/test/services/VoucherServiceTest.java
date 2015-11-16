package services;

import base.BaseTest;
import base.VoucherTest;
import common.utils.DateUtils;
import ordercenter.constants.VoucherBatchStatus;
import ordercenter.constants.VoucherStatus;
import ordercenter.constants.VoucherType;
import ordercenter.excepiton.VoucherException;
import ordercenter.models.Voucher;
import ordercenter.models.VoucherBatch;
import ordercenter.services.VoucherService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import play.Logger;
import utils.Global;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by liubin on 15-4-2.
 */
public class VoucherServiceTest extends BaseTest implements VoucherTest {

    /**
     * 测试运行之前将数据库所有的代金券活动状态置为无效
     */
    @Before
    public void init() {
        setAllVoucherBatchToInvalid();
    }


    @Test
    public void testRequestForActivitySuccess() throws Exception {

        Integer userId = mockUser();

        DateTime now = DateUtils.current();
        Optional<Integer> period = of(3);
        Optional<DateTime> deadline = of(now.plusDays(2));
        int quantity = 3;

        //为每种类型都运行测试
        for(VoucherType type : VoucherType.values()) {
            //活动请求，代金券只有有效期没有截止日期
            VoucherBatch voucherBatch = initVoucherBatchWithValid(type, now, empty(), period, empty(), empty());
            assertRequestVouchersSuccess(voucherBatch, userId, quantity);

            setAllVoucherBatchToInvalid();

            //活动请求，代金券有截止日期
            voucherBatch = initVoucherBatchWithValid(type, now, empty(), period, deadline, empty());
            assertRequestVouchersSuccess(voucherBatch, userId, quantity);

        }

    }

    @Test
    public void testRequestForActivityFail() throws Exception {

        Integer userId = mockUser();

        DateTime now = DateUtils.current();
        Optional<Integer> period = of(3);
        int maxQuantity = 1;

        for(VoucherType type : VoucherType.values()) {
            //代金券最大数量为1, 但是请求2个
            VoucherBatch voucherBatch = initVoucherBatchWithValid(type, now, empty(), period, empty(), of(maxQuantity));
            boolean exceptionThrown = false;
            try {
                requestVoucherByType(type, userId, voucherBatch.getUniqueNo(), maxQuantity + 1);
            } catch (VoucherException expected) {
                Logger.debug(expected.getMessage());
                exceptionThrown = true;
            }
            if(!exceptionThrown) {
                throw new AssertionError("因为请求的代金券数量超过上限，预期抛出VoucherException，但是没有异常抛出");
            }
        }
    }

    @Test
    public void testChangeVoucherStatusSuccess() throws Exception {

        VoucherService voucherService = Global.ctx.getBean(VoucherService.class);

        Integer userId = mockUser();

        DateTime now = DateUtils.current().minusMinutes(1);
        Optional<DateTime> deadline = of(now.minusDays(1));


        //测试代金券活动根据endTime变为无效
        VoucherBatch voucherBatch1 = initVoucherBatch(VoucherType.RECEIVE_BY_ACTIVITY, now, empty(), empty(), deadline, empty());
        voucherService.changeStatus();
        doInTransactionWithGeneralDao(generalDao -> {
            //设置endTime为现在
            VoucherBatch vb = generalDao.get(VoucherBatch.class, voucherBatch1.getId());
            assertThat(vb.getStatus(), is(VoucherBatchStatus.VALID));
            vb.setEndTime(now);
            generalDao.persist(vb);
            return null;
        });

        //这时候endTime已经过期，调用这个方法应该会设置成失效
        voucherService.changeStatus();
        doInTransactionWithGeneralDao(generalDao -> {
            VoucherBatch vb = voucherService.getVoucherBatch(voucherBatch1.getId());
            assertThat(vb.getStatus(), is(VoucherBatchStatus.INVALID));
            return null;
        });

        //测试代金券根据deadline变为过期
        VoucherBatch voucherBatch2 = initVoucherBatchWithValid(VoucherType.RECEIVE_BY_ACTIVITY, now, empty(), empty(), deadline, empty());
        List<Voucher> voucherList = assertRequestVouchersSuccess(voucherBatch2, userId, 1);
        Voucher voucher = voucherList.get(0);
        assertThat(voucher.getStatus(), is(VoucherStatus.UNUSED));
        voucherService.changeStatus();
        doInTransactionWithGeneralDao(generalDao -> {
            Voucher v = voucherService.getVoucher(voucher.getId());
            assertThat(v.getStatus(), is(VoucherStatus.OVERDUE));
            return null;
        });



    }


    @Test
    @Ignore
    public void test1() throws Exception {

        DateTime now = DateUtils.current();
        Optional<Integer> period = of(100);
        int quantity = 10;
        VoucherBatch voucherBatch = initVoucherBatchWithValid(VoucherType.RECEIVE_BY_ACTIVITY, now, empty(), period, empty(), empty());
        assertRequestVouchersSuccess(voucherBatch, 9, quantity);

    }




}
