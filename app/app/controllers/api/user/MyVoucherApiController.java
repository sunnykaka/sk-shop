package controllers.api.user;

import api.response.order.MyVouchersDto;
import api.response.user.OrderDto;
import api.response.user.OrderInfoDto;
import cmscenter.constants.VoucherActivityKey;
import cmscenter.models.VoucherActivity;
import cmscenter.services.VoucherActivityService;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import common.utils.page.Page;
import common.utils.page.PageFactory;
import controllers.BaseController;
import ordercenter.constants.VoucherStatus;
import ordercenter.dtos.MyVouchers;
import ordercenter.excepiton.VoucherException;
import ordercenter.models.Logistics;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.models.Trade;
import ordercenter.services.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Result;
import productcenter.models.StockKeepingUnit;
import productcenter.services.SkuAndStorageService;
import usercenter.models.Designer;
import usercenter.models.User;
import usercenter.services.DesignerService;
import utils.secure.SecuredAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * 我的代金券管理
 * <p>
 * Created by zhb on 15-5-7.
 */
@org.springframework.stereotype.Controller
public class MyVoucherApiController extends BaseController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherActivityService voucherActivityService;

    /**
     * 个人中心代金券列表
     *
     * @param status
     * @param pageNo
     * @param pageSize
     * @return
     */
    @SecuredAction
    public Result list(String status, int pageNo, int pageSize) {

        User user = currentUser();

        VoucherStatus voucherStatus = null;
        if(StringUtils.isNotBlank(status)) {
            voucherStatus = VoucherStatus.valueOf(status);
        }

        MyVouchers myVouchers = voucherService.findByPage(PageFactory.getPage(request()),
                user.getId(), ofNullable(voucherStatus));

        MyVouchersDto myVouchersDto = MyVouchersDto.build(myVouchers);

        return ok(JsonUtils.object2Node(myVouchersDto));

    }

    /**
     * 获取代金券
     *
     * @param batchUniqueNo
     * @return
     */
    @SecuredAction
    public Result saveVoucher(String batchUniqueNo) {

        User user = currentUser();

        if(StringUtils.isEmpty(batchUniqueNo)){
            throw new AppBusinessException(ErrorCode.OperationError, "没有该代金券");
        }

        VoucherActivity voucherActivity = voucherActivityService.getMyVoucherActivity(batchUniqueNo, VoucherActivityKey.VOUCHER_ACTIVITY_DOUBLE12, user.getId());

        if(voucherActivity != null){
            throw new AppBusinessException(ErrorCode.OperationError, "已领取该代金券");
        }

        try{
            voucherService.requestForActivity(Optional.of(batchUniqueNo),user.getId(),1);
        }catch (VoucherException v){
            throw new AppBusinessException(ErrorCode.OperationError, v.getMessage());
        }

        return noContent();

    }

}
