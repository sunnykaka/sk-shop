package controllers.api.shop;

import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import usercenter.models.Feedback;
import usercenter.models.User;
import usercenter.services.FeedbackService;

/**
 * 意见反馈 Controller
 * User: lidujun
 * Date: 2015-09-24
 */
@org.springframework.stereotype.Controller
public class AppFeedbackController extends BaseController {

    @Autowired
    FeedbackService feedbackService;

    public Result submitFeedback() {
        try {
            Form<Feedback> feedbackForm = Form.form(Feedback.class).bindFromRequest();
            Feedback feedback = feedbackForm.get();
            if(feedback != null && feedback.getContact() != null && feedback.getContact().trim().length() > 0) {
                User user = this.currentUser();
                if(user != null) {
                    feedback.setCreateBy(user.getId());
                }
                feedbackService.createFeedback(feedback);
                return noContent();
            } else {
                throw new AppBusinessException(ErrorCode.Conflict, "意见反馈内容不能为空！");
            }
        } catch (AppBusinessException e) {
            Logger.info("提交意见反馈失败,错误信息： " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            Logger.info("提交意见反馈失败,错误信息： " + e.getMessage(), e);
            throw new AppBusinessException(ErrorCode.Conflict, "提交意见反馈失败，请直接联系商城客服人员！");
        }
    }
}