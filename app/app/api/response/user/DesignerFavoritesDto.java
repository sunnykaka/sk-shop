package api.response.user;

import usercenter.models.DesignerCollect;

/**
 * Created by Administrator on 2015/9/1.
 */
public class DesignerFavoritesDto {

    private Integer id;

    private int designerId;

    private String designerPic;

    private String designerName;

    public static DesignerFavoritesDto build(DesignerCollect designerCollect){

        if(designerCollect == null){ return null; }

        DesignerFavoritesDto designerFavoritesDto = new DesignerFavoritesDto();
        designerFavoritesDto.setDesignerId(designerCollect.getDesignerId());
        designerFavoritesDto.setDesignerName(designerCollect.getDesignerName());
        designerFavoritesDto.setDesignerPic(designerCollect.getDesignerPic());
        designerFavoritesDto.setId(designerCollect.getId());

        return designerFavoritesDto;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getDesignerId() {
        return designerId;
    }

    public void setDesignerId(int designerId) {
        this.designerId = designerId;
    }

    public String getDesignerPic() {
        return designerPic;
    }

    public void setDesignerPic(String designerPic) {
        this.designerPic = designerPic;
    }

    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }
}
