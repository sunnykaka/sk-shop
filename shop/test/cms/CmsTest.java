package cms;

import base.BaseTest;
import models.CmsExhibition;
import models.ExhibitionStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import services.CmsService;
import utils.Global;

import java.util.List;
import java.util.Map;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by amos on 15-5-11.
 */
public class CmsTest extends BaseTest {

    private CmsService cmsService;

    @Before
    public void init() {
        cmsService = Global.ctx.getBean(CmsService.class);
    }

    @Test
    @Ignore
    public void testProdOnPublish() {

        Boolean result = cmsService.useFirstSellPrice(2184);
        Assert.assertTrue(result);
        Assert.assertFalse(cmsService.useFirstSellPrice(2186));

    }

    @Test
    @Ignore
    public void testFindAllExhibition() {
        Map<ExhibitionStatus, List<CmsExhibition>> result = cmsService.queryAllExhibition();
        Assert.assertEquals(2, result.size());
        List<CmsExhibition> prepareList = result.get(ExhibitionStatus.PREPARE);
        List<CmsExhibition> Selling = result.get(ExhibitionStatus.SELLING);

        Assert.assertEquals(2, prepareList.size());
        Assert.assertEquals(2, Selling.size());


    }
}
