package designer;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import usercenter.services.DesignerService;
import utils.Global;

/**
 * Created by amos on 15-5-15.
 */
public class DesignerTest  extends WithApplication {

    private DesignerService designerService;

    @Before
    public void init() {
        designerService = Global.ctx.getBean(DesignerService.class);
    }

    @Test
    public void testFindAllDesigner(){
        designerService.designerById(null);
    }
}
