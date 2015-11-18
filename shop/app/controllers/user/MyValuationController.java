package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.FormUtils;
import common.utils.JsonResult;
import ordercenter.models.Valuation;
import ordercenter.services.ValuationService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.dtos.ValuationForm;
import usercenter.models.User;
import usercenter.services.UserService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.user.myValuation;

/**
 * Created by zhb on 15-5-7.
 */
@org.springframework.stereotype.Controller
public class MyValuationController extends Controller {

    @Autowired
    private ValuationService valuationService;

    @Autowired
    private UserService userService;

    /**
     * 添加评论
     *
     * @return
     */
    @SecuredAction
    public Result add() {

        User user = SessionUtils.currentUser();

        Form<ValuationForm> valuationForm = Form.form(ValuationForm.class).bindFromRequest();

        if(!valuationForm.hasErrors()) {
            try {
                ValuationForm valuationF = valuationForm.get();

                Valuation oldValuation = valuationService.findByOrderItemId(user.getId(), valuationF.getOrderItemId());
                if(null != oldValuation){
                    return ok(new JsonResult(false,"已评价该商品").toNode());
                }

                Valuation valuation = new Valuation();
                valuation.setUserId(user.getId());
                valuation.setUserName(user.getUserName());
                valuation.setContent(StringEscapeUtils.escapeHtml4(StringUtils.trim(valuationF.getContent())));
                valuation.setPoint(valuationF.getPoint());
                valuation.setOrderItemId(valuationF.getOrderItemId());
                valuation.setProductId(valuationF.getProductId());

                valuationService.addValuation(valuation);

                return ok(new JsonResult(true,null,valuation).toNode());

            } catch (AppBusinessException e) {
                valuationForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(valuationForm.errors())).toNode());

    }

    @SecuredAction
    public Result saveValuation() {

        User user = SessionUtils.currentUser();

        Form<ValuationForm> valuationForm = Form.form(ValuationForm.class).bindFromRequest();

        if(!valuationForm.hasErrors()) {
            try {
                ValuationForm valuationF = valuationForm.get();

                Valuation valuation = new Valuation();
                valuation.setUserId(user.getId());
                valuation.setUserName(user.getUserName());
                valuation.setContent(StringEscapeUtils.escapeHtml4(StringUtils.trim(valuationF.getContent())));
                valuation.setProductId(valuationF.getProductId());
                if(valuationF.getReplyUserId() != null && valuationF.getReplyUserId() != 0){
                    valuation.setReplyUserId(valuationF.getReplyUserId());
                    User replyUser = userService.getById(valuationF.getReplyUserId());
                    valuation.setReplyUserName(replyUser == null ? "" : replyUser.getUserName());
                }

                valuationService.saveValuation(valuation);

                int count = valuationService.countValuation(valuationF.getProductId());

                return ok(new JsonResult(true, null, count).toNode());

            } catch (AppBusinessException e) {
                valuationForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(valuationForm.errors())).toNode());
    }

    @SecuredAction
    public Result deleteMyValuation(int id) {
        User user = SessionUtils.currentUser();

        Valuation oldValuation = valuationService.findValuationById(user.getId(), id);
        if(oldValuation == null){
            return ok(new JsonResult(false, "没有权限删除").toNode());
        }

        oldValuation.setDeleted(true);
        valuationService.updateValuation(oldValuation);

        int count = valuationService.countValuation(oldValuation.getProductId());

        return ok(new JsonResult(true, "删除成功" , count).toNode());
    }

}
