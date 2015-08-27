package productcenter.services;

import common.services.GeneralDao;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.Product;
import usercenter.models.Designer;
import usercenter.services.NationService;

/**
 * Created by liubin on 15-8-27.
 */
@Service
public class ProductTestDataService {

    @Autowired
    NationService nationService;

    @Autowired
    GeneralDao generalDao;

    @Transactional
    public Product initProduct() {

        Product product = new Product();
        product.setProductCode(RandomStringUtils.randomAlphanumeric(8));

        Designer designer = initDesigner();
//        product.setBrandId();

        return null;

    }

    @Transactional
    private Designer initDesigner() {

        Designer designer = new Designer();
        designer.setDescription("推荐理由");
        designer.setIsDelete(false);
        designer.setIsPublished(true);
        designer.setName(RandomStringUtils.randomAlphabetic(6) + "设计师");
        designer.setNationId(nationService.findByNameZh("中国").get(0).getId());
        designer.setPriority(0);

        generalDao.persist(designer);

        return designer;
    }



}
