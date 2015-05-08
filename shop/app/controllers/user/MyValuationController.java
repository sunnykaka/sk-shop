package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.dtos.ValuationForm;
import productcenter.models.Valuation;
import productcenter.services.ValuationService;
import usercenter.models.User;
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

    /**
     * 我的评论首页
     *
     * @return
     */
    @SecuredAction
    public Result index() {

        User user = SessionUtils.currentUser();

        return ok(myValuation.render());

    }

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
                valuation.setContent(StringEscapeUtils.escapeHtml4(valuationF.getContent()));
                valuation.setPoint(valuationF.getPoint());
                valuation.setOrderItemId(valuationF.getOrderItemId());
                valuation.setProductId(valuationF.getProductId());

                valuationService.addValuation(valuation);

                return ok(new JsonResult(true,null,valuation).toNode());

            } catch (AppBusinessException e) {
                valuationForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, valuationForm.errorsAsJson().toString()).toNode());

    }

    @SecuredAction
    public Result findValuation(long orderItemId) {

        User user = SessionUtils.currentUser();

        Valuation valuation = valuationService.findByOrderItemId(user.getId(),orderItemId);

        if(null == valuation){
            return ok(new JsonResult(false,"未评价",null).toNode());
        }

        return ok(new JsonResult(true,"已评价",valuation).toNode());

    }


}
