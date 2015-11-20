package controllers.activity;

import cmscenter.constants.VoucherActivityKey;
import cmscenter.models.VoucherActivity;
import cmscenter.services.VoucherActivityService;
import common.utils.ParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.services.UserService;
import usercenter.utils.UserTokenUtils;
import views.html.activity.double12Activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zhb on 2015/11/20.
 *
 * 代金券活动
 *
 */
@org.springframework.stereotype.Controller
public class VoucherActivityController extends Controller {

    /** 双12代金券活动 */
    protected static final String[] DOUBLE12_ACTIVITY_UNIQUENO = {"351166","160557"};

    @Autowired
    private VoucherActivityService voucherActivityService;
    
    @Autowired
    private UserService userService;

    public Result double12Activity() {
        Map<String,Boolean> map = new HashMap<>();

        String token = ParamUtils.getByKey(request(),"accessToken");
        Optional<Integer> userId = userService.retrieveUserIdByAccessToken(token);

        int activityId = VoucherActivityKey.VOUCHER_ACTIVITY_DOUBLE12;

        for(String uniqueNo:DOUBLE12_ACTIVITY_UNIQUENO){
            if(userId.isPresent()){
                VoucherActivity voucherActivity = voucherActivityService.getMyVoucherActivity(uniqueNo, activityId, userId.get());
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
