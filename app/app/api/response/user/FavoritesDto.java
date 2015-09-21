package api.response.user;

import cmscenter.models.ThemeCollect;
import productcenter.models.ProductCollect;
import usercenter.models.DesignerCollect;

/**
 * Created by Administrator on 2015/9/1.
 */
public class FavoritesDto {

    private Integer id;

    private int objectId;

    private String objectPic;

    private String objectName;

    public static FavoritesDto build(DesignerCollect designerCollect){

        if(designerCollect == null){ return null; }

        FavoritesDto favoritesDto = new FavoritesDto();
        favoritesDto.setObjectId(designerCollect.getDesignerId());
        favoritesDto.setObjectName(designerCollect.getDesignerName());
        favoritesDto.setObjectPic(designerCollect.getDesignerPic());
        favoritesDto.setId(designerCollect.getId());

        return favoritesDto;

    }

    public static FavoritesDto build(ProductCollect productCollect){

        if(productCollect == null){ return null; }

        FavoritesDto favoritesDto = new FavoritesDto();
        favoritesDto.setObjectId(productCollect.getProductId());
        favoritesDto.setObjectName(productCollect.getProductName());
        favoritesDto.setObjectPic(productCollect.getProductPic());
        favoritesDto.setId(productCollect.getId());

        return favoritesDto;

    }

    public static FavoritesDto build(ThemeCollect themeCollect){

        if(themeCollect == null){ return null; }

        FavoritesDto favoritesDto = new FavoritesDto();
        favoritesDto.setObjectId(themeCollect.getThemeId());
        favoritesDto.setObjectName(themeCollect.getThemeName());
        favoritesDto.setObjectPic(themeCollect.getThemePic());
        favoritesDto.setId(themeCollect.getId());

        return favoritesDto;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getObjectPic() {
        return objectPic;
    }

    public void setObjectPic(String objectPic) {
        this.objectPic = objectPic;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
