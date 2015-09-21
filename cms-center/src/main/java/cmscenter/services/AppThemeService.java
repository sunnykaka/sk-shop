package cmscenter.services;

import cmscenter.models.AppHome;
import cmscenter.models.AppTheme;
import cmscenter.models.AppThemeContent;
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

        String jpql = "select a from AppTheme a where 1=1 and isDelete=false order by a.startTime desc";
        Map<String, Object> queryParams = new HashMap<>();

        return generalDao.query(jpql, page, queryParams);

    }

    @Transactional(readOnly = true)
    public List<AppThemeContent> getAppThemeContentByThemeId(int themeId){

        String jpql = "select a from AppThemeContent a where 1=1 and a.themeId=:themeId order by a.priority asc";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("themeId", themeId);

        return generalDao.query(jpql, Optional.ofNullable(null), queryParams);

    }

    @Transactional(readOnly = true)
    public String getAppThemeContentFristFont(int themeId){

        String jpql = "select a from AppThemeContent a where 1=1 and a.type='FONT' and a.themeId=:themeId order by a.priority asc";
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


}
