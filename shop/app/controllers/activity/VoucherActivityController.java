package controllers.activity;

import cmscenter.constants.VoucherActivityKey;
import cmscenter.models.VoucherActivity;
import cmscenter.services.VoucherActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.activity.double12Activity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhb on 2015/11/20.
 *
 * 代金券活动
 *
 */
@org.springframework.stereotype.Controller
public class VoucherActivityController extends Controller {

    /** 双12代金券活动 */
    protected static final String[] DOUBLE12_ACTIVITY_UNIQUENO = {"351166"};

    @Autowired
    private VoucherActivityService voucherActivityService;

    public Result double12Activity(Integer userId) {

        Map<String,Boolean> map = new HashMap<>();

        int activityId = VoucherActivityKey.VOUCHER_ACTIVITY_DOUBLE12;

        for(String uniqueNo:DOUBLE12_ACTIVITY_UNIQUENO){
            if(userId != null){
                VoucherActivity voucherActivity = voucherActivityService.getMyVoucherActivity(uniqueNo, activityId, userId);
                if(voucherActivity != null){
                    map.put(uniqueNo,true);
                }else{
                    map.put(uniqueNo,false);
                }
            }else{
                map.put(uniqueNo,false);
            }

        }

        return ok(double12Activity.render(map));

    }

}
