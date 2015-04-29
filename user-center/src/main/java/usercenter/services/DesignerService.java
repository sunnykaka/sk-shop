package usercenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.models.Designer;
import usercenter.models.DesignerPicture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Designer getDesignerById(int designerId){
       return generalDAO.get(Designer.class,designerId);
    }

    /**
     * 获取设计师主图
     *
     * @param designerId
     * @return
     */
    @Transactional(readOnly = true)
    public DesignerPicture getDesignerPicByDesignerById(int designerId){

        String jpql = "select dp from DesignerPicture dp where 1=1 and dp.mainPic=true and dp.designerId=:designerId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("designerId", designerId);

        List<DesignerPicture> list = generalDAO.query(jpql, Optional.ofNullable(null), queryParams);
        if(list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return new DesignerPicture(DesignerPicture.DESIGNER_DEFAULT_PIC);
        }
    }
}
