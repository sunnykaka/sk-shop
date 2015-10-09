package cmscenter.services;

import cmscenter.models.AppHome;
import cmscenter.models.AppTheme;
import cmscenter.models.AppThemeContent;
import cmscenter.models.DeviceToken;
import common.services.GeneralDao;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Zhb on 2015/9/15.
 */
@Service
public class AppThemeService {

    @Autowired
    GeneralDao generalDao;

    @Transactional(readOnly = true)
    public List<AppHome> getAppHomes(){
       return generalDao.findAll(AppHome.class);
    }

    public AppTheme getAppThemeById(int themeId){
        return generalDao.get(AppTheme.class, themeId);
    }

    @Transactional(readOnly = true)
    public List<AppTheme> findAppThemePageList(Optional<Page<AppTheme>> page){

        String jpql = "select a from AppTheme a where 1=1 and isDelete=false and online=true order by a.startTime desc";
        Map<String, Object> queryParams = new HashMap<>();

        return generalDao.query(jpql, page, queryParams);

    }

    @Transactional(readOnly = true)
    public String getAppThemeContentByThemeId(int themeId){

        String jpql = "select a from AppThemeContent a where 1=1 and a.themeId=:themeId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("themeId", themeId);

        List<AppThemeContent> appThemeContentList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        AppThemeContent appThemeContent = null;
        if(appThemeContentList != null && appThemeContentList.size() > 0){
            appThemeContent = appThemeContentList.get(0);
        }
        if(appThemeContent == null){
            return "";
        }else{
            return appThemeContent.getContent();
        }

    }

    @Transactional()
    public void saveDeviceToken(DeviceToken deviceToken){

        generalDao.persist(deviceToken);

    }

    @Transactional(readOnly = true)
    public DeviceToken findBytoken(String token){

        String jpql = "select pc from DeviceToken pc where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and pc.token = :token ";
        queryParams.put("token", token);

        List<DeviceToken> valueList = generalDao.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;

    }


}
