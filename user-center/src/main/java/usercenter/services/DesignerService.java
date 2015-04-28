package usercenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.models.Designer;

/**
 * Created by zhb on 15-4-27.
 */
@Service
@Transactional
public class DesignerService {

    @Autowired
    GeneralDao generalDAO;

    /**
     * 根据ID查询
     *
     * @param designerId
     * @return
     */
    @Transactional(readOnly = true)
    public Designer getById(int designerId){
       return generalDAO.get(Designer.class,designerId);
    }
}
