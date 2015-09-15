package api.response.user;

import usercenter.models.address.Area;

/**
 * Created by Administrator on 2015/9/2.
 */
public class AreaDto {

    private String name;

    public static AreaDto build(Area area){
        if(area == null){return null;}

        AreaDto areaDto = new AreaDto();
        areaDto.setName(area.getName());

        return areaDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
