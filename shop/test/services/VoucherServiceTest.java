package services;

import base.BaseTest;
import base.VoucherTest;
import common.utils.DateUtils;
import ordercenter.constants.VoucherType;
import ordercenter.excepiton.VoucherException;
import ordercenter.models.VoucherBatch;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import play.Logger;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;


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
            VoucherBatch voucherBatch = initVoucherBatch(type, now, empty(), period, empty(), empty());
            assertRequestVouchersSuccess(voucherBatch, userId, quantity);

            setAllVoucherBatchToInvalid();

            //活动请求，代金券有截止日期
            voucherBatch = initVoucherBatch(type, now, empty(), period, deadline, empty());
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
            VoucherBatch voucherBatch = initVoucherBatch(type, now, empty(), period, empty(), of(maxQuantity));
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





}
