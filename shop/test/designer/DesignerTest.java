package designer;

import base.BaseTest;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import usercenter.services.DesignerService;
import utils.Global;

/**
 * Created by amos on 15-5-15.
 */
public class DesignerTest  extends BaseTest {

    private DesignerService designerService;

    @Before
    public void init() {
        designerService = Global.ctx.getBean(DesignerService.class);
    }

    @Test
    public void testFindAllDesigner(){
        designerService.designerByPriority(null);
    }
}
