package api.response.user;

import usercenter.models.address.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/2.
 */
public class CityDto {

    private String name;

    private List<AreaDto> areas = new ArrayList<>();

    public static CityDto build(City city){
        if(city == null){return null;}

        CityDto cityDto = new CityDto();
        cityDto.setName(city.getName());

        return cityDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AreaDto> getAreas() {
        return areas;
    }

    public void setAreas(List<AreaDto> areas) {
        this.areas = areas;
    }
}
