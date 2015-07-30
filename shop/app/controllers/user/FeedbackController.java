package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.FormUtils;
import common.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.models.Feedback;
import usercenter.models.User;
import usercenter.services.FeedbackService;
import usercenter.utils.SessionUtils;

/**
 * 友情链接Controller
 * User: lidujun
 * Date: 2015-07-28
 */
@org.springframework.stereotype.Controller
public class FeedbackController extends Controller {

    @Autowired
    FeedbackService feedbackService;

    public Result submitFeedback() {
        Form<Feedback> feedbackForm = Form.form(Feedback.class).bindFromRequest();
        if(!feedbackForm.hasErrors()) {
            try {
                Feedback feedback = feedbackForm.get();
                if(feedback != null && feedback.getContact() != null && feedback.getContact().trim().length() > 0) {
                    User user = SessionUtils.currentUser();
                    if(user != null) {
                        feedback.setCreateBy(user.getId());
                    }
                    feedbackService.createFeedback(feedback);
                    return ok(new JsonResult(true, "意见反馈提交成功！").toNode());
                } else {
                    return ok(new JsonResult(false, "意见反馈内容不能为空！").toNode());
                }
            } catch (AppBusinessException e) {
                Logger.info("提交意见反馈失败,错误信息： " + e.getMessage(), e);
                feedbackForm.reject("errors", "提交意见反馈失败，请直接联系商城客服人员！");
            }
        }
        return ok(new JsonResult(false, FormUtils.showErrorInfo(feedbackForm.errors())).toNode());
    }

}