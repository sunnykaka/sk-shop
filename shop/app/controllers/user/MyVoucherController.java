package controllers.user;

import common.utils.page.PageFactory;
import ordercenter.constants.VoucherStatus;
import ordercenter.dtos.MyVouchers;
import ordercenter.services.VoucherService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.models.User;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.user.myVoucher;

import static java.util.Optional.ofNullable;

/**
 * 代金券管理
 */
@org.springframework.stereotype.Controller
public class MyVoucherController extends Controller {

    @Autowired
    private VoucherService voucherService;

    @SecuredAction
    public Result list(String status, int pageNo, int pageSize) {

        User user = SessionUtils.currentUser();

        VoucherStatus voucherStatus = null;
        if(StringUtils.isNotBlank(status)) {
            voucherStatus = VoucherStatus.valueOf(status);
        }

        MyVouchers myVouchers = voucherService.findByPage(PageFactory.getPage(request()),
                user.getId(), ofNullable(voucherStatus));

        return ok(myVoucher.render(myVouchers));

    }


}
