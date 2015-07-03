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

    /**
     * 暂时没有用上
     *
     * @param orderItemId
     * @return
     */
    @SecuredAction
    public Result findValuation(long orderItemId) {

        //测试，没有用代码，下次删除 zhenghaobin
//        int[] a = new int[]{0,1,2};
//
//        for(int ia:a){
//            for(int i=0;i<=20;i++){
//                Valuation valuation = new Valuation();
//                valuation.setPoint(ia);
//                valuation.setOrderItemId(13236);
//                valuation.setProductId(2206);
//                valuation.setUserId(14341);
//                valuation.setUserName("hello");
//                valuationService.addValuation(valuation);
//            }
//        }

//        User user = SessionUtils.currentUser();
//
//        Valuation valuation = valuationService.findByOrderItemId(user.getId(),orderItemId);
//
//        if(null == valuation){
//            return ok(new JsonResult(false,"未评价",null).toNode());
//        }
//
//        return ok(new JsonResult(true,"已评价",valuation).toNode());
        return ok("true");

    }


}
