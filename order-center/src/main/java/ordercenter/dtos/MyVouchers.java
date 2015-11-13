package ordercenter.dtos;

import common.utils.page.Page;
import ordercenter.constants.VoucherStatus;
import ordercenter.models.Voucher;

import java.util.Map;

/**
 * Created by liubin on 15-11-12.
 */
public class MyVouchers {

    private Page<Voucher> vouchers;

    private VoucherStatus currentStatus;

    private Map<VoucherStatus, Integer> countMap;

    public MyVouchers(Page<Voucher> vouchers, VoucherStatus currentStatus, Map<VoucherStatus, Integer> countMap) {
        this.vouchers = vouchers;
        this.currentStatus = currentStatus;
        this.countMap = countMap;
    }

    public Page<Voucher> getVouchers() {
        return vouchers;
    }

    public VoucherStatus getCurrentStatus() {
        return currentStatus;
    }

    public Map<VoucherStatus, Integer> getCountMap() {
        return countMap;
    }
}
