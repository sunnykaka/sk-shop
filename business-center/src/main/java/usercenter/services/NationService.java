package usercenter.services;

import common.services.GeneralDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.models.address.Address;
import usercenter.models.address.Nation;

import java.util.*;

/**
 *
 */
@Service
@Transactional
public class NationService {

    @Autowired
    private GeneralDao generalDAO;

    /**
     * 根据中文名称查询国籍
     * @param nameZh
     * @return
     */
    @Transactional(readOnly = true)
    public List<Nation> findByNameZh(String nameZh){
        if(StringUtils.isBlank(nameZh)) {
            return new ArrayList<>();
        }

        String jpql = "select n from Nation n where n.nameZh like :nameZh ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("nameZh", "%" + nameZh + "%");

        return generalDAO.query(jpql, Optional.empty(), queryParams);
    }

}
