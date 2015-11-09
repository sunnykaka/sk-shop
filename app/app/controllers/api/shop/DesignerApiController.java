package controllers.api.shop;

import api.response.shop.DesignerListDto;
import api.response.shop.DesignerShopDto;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import common.utils.page.Page;
import controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import usercenter.dtos.DesignerView;
import usercenter.models.Designer;
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

    @Autowired
    ProductPictureService productPictureService;

    public Result designerShop(int designerId) {

        List<DesignerView> designers = designerService.designerById(designerId);
        if(null == designers){
            throw new AppBusinessException(ErrorCode.Conflict, "设计师不存在");
        }else{

            DesignerShopDto designerShopDto = DesignerShopDto.build(designers.get(0), productService);

            return ok(JsonUtils.object2Node(designerShopDto));
        }

    }

    /**
     * 设计师列表页
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    public Result designerList(int pageNo, int pageSize){

        Page<Designer> page = new Page(pageNo,pageSize);
        List<Designer> designerList = designerService.queryDesignerList(page);

        Page<DesignerListDto> dtoPage = new Page(pageNo,pageSize);
        List<DesignerListDto> list = DesignerListDto.buildList(designerList,designerService,productService,productPictureService);
        dtoPage.setResult(list);

        return ok(JsonUtils.object2Node(dtoPage));
    }

}
