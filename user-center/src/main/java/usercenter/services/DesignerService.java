package usercenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usercenter.models.Designer;

/**
 * Created by zhb on 15-4-27.
 */
@Service
public class DesignerService {

    @Autowired
    GeneralDao generalDAO;

    /**
     * 根据ID查询
     *
     * @param designerId
     * @return
     */
    public Designer getById(int designerId){
       return generalDAO.get(Designer.class,designerId);
    }
}
