package cmscenter.dtos;

import cmscenter.constants.HomeFocusType;
import cmscenter.models.HomeFocus;

/**
 * Created by zhb on 2015/10/29.
 */
public class HomeFocusDto {

    private String picUrl;

    private String type;

    private String content;

    private String name;

    public static HomeFocusDto build(HomeFocus homeFocus){
        HomeFocusDto homeFocusDto = new HomeFocusDto();
        homeFocusDto.setContent(homeFocus.getContent());
        homeFocusDto.setName(homeFocus.getName());
        homeFocusDto.setPicUrl(homeFocus.getPicUrl());
        homeFocusDto.setType(homeFocus.getType().getName());
        return homeFocusDto;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
