package controllers.cms;

import models.CmsContent;
import models.CmsExhibition;
import models.ExhibitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import services.CmsService;
import usercenter.utils.SessionUtils;
import views.html.index;

import java.util.List;
import java.util.Map;

/**
 * Created by amos on 15-5-12.
 */
@org.springframework.stereotype.Controller
public class CmsController extends Controller {

    /**
     *  首页DataMap的集合
     */
    private static final String INDEX_EXHIBITION = "selling_exhibitions";


}
