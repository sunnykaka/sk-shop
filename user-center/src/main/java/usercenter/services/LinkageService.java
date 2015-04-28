package usercenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usercenter.models.address.Area;
import usercenter.models.address.City;
import usercenter.models.address.Province;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 省市区县, 三级联动.
 *
 * @author : Athens(刘杰)
 * @Date: 12-8-13
 * @Time: 下午6:08
 */
@Service
public class LinkageService {

    @Autowired
    private GeneralDao generalDAO;

    /**
     * 获取所有省市信息.
     *
     * @return
     */
    public List<Province> getAllProvince(){

        return generalDAO.findAll(Province.class);
    }

    /**
     * 根据省市名查询对应的信息.
     *
     * @param name 城市名. 如 北京市, 河北省等
     * @return
     */
    public Province getProvinceByName(String name){

        String jpql = "select p from Province p where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and p.name = :name ";
        queryParams.put("name", name);

        List<Province> valueList = generalDAO.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;
    }

    /**
     * 获取所有省市下的所有地市信息.
     *
     * @param provinceCode 省市编码
     * @return
     */
    public List<City> getCityByProvinceCode(String provinceCode){

        String jpql = "select c from City c where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and c.provinceId = :provinceCode ";
        queryParams.put("provinceCode", provinceCode);

        return generalDAO.query(jpql, Optional.empty(), queryParams);
    }

    /**
     * 获取所有地市下的所有区县信息.
     *
     * @param cityCode 地市编码
     * @return
     */
    public List<Area> getAreaByCityCode(String cityCode){
        String jpql = "select a from Area a where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and a.cityId = :cityCode ";
        queryParams.put("cityCode", cityCode);

        return generalDAO.query(jpql, Optional.empty(), queryParams);
    }

}
