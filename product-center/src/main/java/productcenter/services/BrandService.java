package productcenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productcenter.models.Brand;
import productcenter.models.Value;
import usercenter.models.Designer;

/**
 * Created by liubin on 15-8-28.
 */
@Service
public class BrandService {

    @Autowired
    GeneralDao generalDao;

    @Autowired
    PropertyAndValueService propertyAndValueService;


    @Transactional
    public void createDesignerAndBrand(Designer designer){
        generalDao.persist(designer);

        //暂时处理掉，商品中的品牌非必选项目
        Brand brand = new Brand();
        brand.setCustomerId(designer.getId());
        brand.setDescription("自动生成默认品牌");
        this.createBrand(brand);
    }

    @Transactional
    public void createBrand(Brand brand) {
        Value value = new Value();
        value.setValueName(brand.getName());
        int brandId = propertyAndValueService.createValueIfNotExist(value);
        brand.setId(brandId);
        generalDao.persist(brand);
    }




}
