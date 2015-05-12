package controllers.cms;

import models.CmsContent;
import models.CmsExhibition;
import models.ExhibitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import services.CmsService;

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

    @Autowired
    private CmsService cmsService;

    public void index(){
        Map<ExhibitionStatus, List<CmsExhibition>> exhibitions = cmsService.queryAllExhibition();
        List<CmsExhibition> sellingList = exhibitions.get(ExhibitionStatus.SELLING);
        List<CmsContent> contents = cmsService.allContents();
    }
}
