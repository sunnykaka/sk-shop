package api.response.order;

import common.utils.page.Page;
import ordercenter.constants.VoucherStatus;
import ordercenter.dtos.MyVouchers;
import ordercenter.models.Voucher;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by liubin on 15-11-12.
 */
public class MyVouchersDto {

    private Page<VoucherDto> vouchers;

    private VoucherStatus currentStatus;

    private Map<VoucherStatus, Integer> countMap;

    private MyVouchersDto(Page<VoucherDto> vouchers, VoucherStatus currentStatus, Map<VoucherStatus, Integer> countMap) {
        this.vouchers = vouchers;
        this.currentStatus = currentStatus;
        this.countMap = countMap;
    }

    public static MyVouchersDto build(MyVouchers myVouchers) {
        Page<Voucher> voucherPage = myVouchers.getVouchers();
        Page<VoucherDto> voucherDtoPage = new Page<>(voucherPage.getPageNo(), voucherPage.getPageSize());
        voucherDtoPage.setResult(voucherPage.getResult().stream().map(VoucherDto::build).collect(Collectors.toList()));

        return new MyVouchersDto(voucherDtoPage, myVouchers.getCurrentStatus(), myVouchers.getCountMap());
    }

    public Page<VoucherDto> getVouchers() {
        return vouchers;
    }

    public VoucherStatus getCurrentStatus() {
        return currentStatus;
    }

    public Map<VoucherStatus, Integer> getCountMap() {
        return countMap;
    }
}
