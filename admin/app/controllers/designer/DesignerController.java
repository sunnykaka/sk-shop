package controllers.designer;

import common.utils.page.Page;
import common.utils.page.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.Designer;
import productcenter.models.Nation;
import productcenter.services.DesignerService;
import views.html.designer.designerlist;
import views.html.designer.designerForm;
import views.html.designer.showDesigner;
import views.html.designer.editDesignerForm;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;

@org.springframework.stereotype.Controller
public class DesignerController extends Controller {

    @Autowired
    private DesignerService designerService;

    private Result getDesignerList( Page page, Designer param) {
        param.setIsDelete(false);
        List<Designer> designers = designerService.getDesignerList(of(page), param);
        return ok(designerlist.render(param, designers));
    }

    public Result designerlist() {
        Form<Designer> form = Form.form(Designer.class).bindFromRequest();
        Designer param = form.get();
        Page page = PageFactory.getPage(request());
        return getDesignerList(page, param);
    }

    public Result designerForm() {
        return ok(designerForm.render(new Nation(), new Designer())); //
    }

    public Result addDesigner() {
        Form<Designer> form = Form.form(Designer.class).bindFromRequest();
        Designer param = form.get();
        if(param != null) {
            param.setIsDelete(false);
        }
        designerService.save(param);

        Page page = PageFactory.getPage(request());
        return getDesignerList(page, new Designer());
    }

    public Result showDesigner(Integer designerId) {
        Designer designer = new Designer();
        Optional<Designer> designerOpt = designerService.getDesignerById(designerId);
        if(designerOpt.isPresent()) {
            designer = designerOpt.get();
        }
        return ok(showDesigner.render(designer));
    }

    public Result editDesignerForm(Integer designerId) {
        Designer designer = new Designer();
        Optional<Designer> designerOpt = designerService.getDesignerById(designerId);
        if(designerOpt.isPresent()) {
            designer = designerOpt.get();
        }
        return ok(editDesignerForm.render(new Nation(), designer)); //
    }

    public Result editDesigner() {
        Form<Designer> form = Form.form(Designer.class).bindFromRequest();
        Designer param = form.get();
        if(param != null) {
            param.setIsDelete(false);
        }
        designerService.update(param);

        Page page = PageFactory.getPage(request());
        return getDesignerList(page, new Designer());
    }

    public Result falseDeleteDesigner(Integer designerId) {
        designerService.falseDelete(designerId);
        Page page = PageFactory.getPage(request());
        return getDesignerList(page, new Designer());
    }

}
