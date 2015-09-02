package api.response.user;

import usercenter.models.address.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/2.
 */
public class ProvinceDto {

    private String name;

    private List<CityDto> citys = new ArrayList<>();

    public static ProvinceDto build(Province province){
        if(province == null){return null;}

        ProvinceDto provinceDto = new ProvinceDto();
        provinceDto.setName(province.getName());

        return provinceDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityDto> getCitys() {
        return citys;
    }

    public void setCitys(List<CityDto> citys) {
        this.citys = citys;
    }
}
