package cms;

import models.CmsExhibition;
import models.ExhibitionStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import services.CmsService;
import utils.Global;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by amos on 15-5-11.
 */
public class CmsTest extends WithApplication {

    private CmsService cmsService;

    @Before
    public void init() {
        cmsService = Global.ctx.getBean(CmsService.class);
    }

    @Test
    public void testProdOnPublish() {

        Boolean result = cmsService.onFirstPublish(2184);
        Assert.assertTrue(result);
        Assert.assertFalse(cmsService.onFirstPublish(2186));

    }

    @Test
    public void testFindAllExhibition() {
        Map<ExhibitionStatus, List<CmsExhibition>> result = cmsService.queryAllExhibition();
        Assert.assertEquals(2, result.size());
        List<CmsExhibition> prepareList = result.get(ExhibitionStatus.PREPARE);
        List<CmsExhibition> Selling = result.get(ExhibitionStatus.SELLING);

        Assert.assertEquals(2, prepareList.size());
        Assert.assertEquals(2, Selling.size());


    }
}
