package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.FormUtils;
import common.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.models.Feedback;
import usercenter.services.FeedbackService;

@org.springframework.stereotype.Controller
public class FeedbackController extends Controller {

    @Autowired
    FeedbackService feedbackService;

    public Result submitFeedback() {
        Form<Feedback> feedbackForm = Form.form(Feedback.class).bindFromRequest();
        if(!feedbackForm.hasErrors()) {
            try {
                Feedback feedback = feedbackForm.get();
                feedbackService.createFeedback(feedback);
                return ok(new JsonResult(true, "意见反馈提交成功！").toNode());
            } catch (AppBusinessException e) {
                feedbackForm.reject("errors", e.getMessage());
            }
        }
        return ok(new JsonResult(false, FormUtils.showErrorInfo(feedbackForm.errors())).toNode());
    }

}