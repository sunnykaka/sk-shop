package controllers.api.shop;

import api.response.shop.DesignerShopDto;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import productcenter.services.ProductService;
import usercenter.dtos.DesignerView;
import usercenter.services.DesignerService;

import java.util.List;

/**
 * Created by zhb on 2015/10/27.
 */
@org.springframework.stereotype.Controller
public class DesignerApiController extends BaseController {

    @Autowired
    private DesignerService designerService;

    @Autowired
    private ProductService productService;

    public Result designerShop(int designerId) {

        List<DesignerView> designers = designerService.designerById(designerId);
        if(null == designers){
            throw new AppBusinessException(ErrorCode.Conflict, "设计师不存在");
        }else{

            DesignerShopDto designerShopDto = DesignerShopDto.build(designers.get(0), productService);

            return ok(JsonUtils.object2Node(designerShopDto));
        }

    }

}
