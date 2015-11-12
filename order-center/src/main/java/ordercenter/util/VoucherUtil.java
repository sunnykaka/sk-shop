package ordercenter.util;

import ordercenter.models.VoucherBatch;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by liubin on 15-11-5.
 */
public class VoucherUtil {

    public static String generateVoucherBatchUniqueNo() {
        return RandomStringUtils.randomNumeric(8);
    }

    public static String generateVoucherUniqueNo(VoucherBatch voucherBatch) {
        return String.format("%s-%s", voucherBatch.getUniqueNo(), RandomStringUtils.randomNumeric(8));
    }


}
