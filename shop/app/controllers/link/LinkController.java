package controllers.link;

import common.utils.JsonResult;
import models.Link;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import services.LinkService;
import utils.secure.SecuredAction;

import java.util.List;

/**
 * 友情链接Controller
 * User: lidujun
 * Date: 2015-07-14
 */
@org.springframework.stereotype.Controller
public class LinkController extends Controller {

    @Autowired
    private LinkService linkService;

    /**
     * 获取友情链接列表
     * @return
     */
    @SecuredAction
    public Result getLinkList() {
        try {
            List<Link> linkList = linkService.getLinkList();
            return ok(new JsonResult(true,"友情链接列表", linkList).toNode());
        } catch (final Exception e) {
            Logger.error("获取友情链接列表出现异常:", e);
            return ok(new JsonResult(false,"获取友情链接列表出现异常").toNode());
        }
    }

}