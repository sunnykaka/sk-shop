package common.services;

import common.models.Const;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by liubin on 15-6-5.
 */
@Service
public class ConstService {

    @Autowired
    private GeneralDao generalDao;

    @Transactional(readOnly = true)
    public Const getConstByKey(String constKey) {

        String hql = "select c from Const c where c.constKey = :constKey";
        Map<String, Object> params = new HashMap<>();
        params.put("constKey", constKey);
        List<Const> results = generalDao.query(hql, Optional.empty(), params);
        return results.isEmpty() ? null : results.get(0);

    }

    @Transactional(readOnly = true)
    public String getConstValueByKey(String constKey) {

        Const constByKey = getConstByKey(constKey);
        return constByKey == null ? null : constByKey.getConstValue();

    }

    @Transactional(readOnly = true)
    public String getConstValueWithDefault(String constKey, String defaultValue) {

        String constValueByKey = getConstValueByKey(constKey);
        return StringUtils.isBlank(constValueByKey) ? defaultValue : constValueByKey;

    }




}
