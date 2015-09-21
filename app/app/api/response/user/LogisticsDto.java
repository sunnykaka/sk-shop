package api.response.user;

import ordercenter.models.Logistics;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2015/9/7.
 */
public class LogisticsDto {

    /**
     * 收获人姓名
     */
    private String name;

    /**
     * 省份
     */
    private String province;

    private String city;

    private String area;

    /**
     * 具体位置，到门牌号
     */
    private String location;

    /**
     * 移动电话
     */
    private String mobile;

    public static LogisticsDto build(Logistics logistics){
        if(null == logistics){ return null;}

        LogisticsDto logisticsDto = new LogisticsDto();
        logisticsDto.setLocation(logistics.getLocation());
        logisticsDto.setMobile(logistics.getMobile());
        logisticsDto.setName(logistics.getName());

        if(StringUtils.isNotEmpty(logistics.getProvince())){
            String[] str = logistics.getProvince().split(",");
            logisticsDto.setProvince(StringUtils.isEmpty(str[0])?"":str[0]);
            logisticsDto.setCity(StringUtils.isEmpty(str[1])?"":str[1]);
            logisticsDto.setArea(StringUtils.isEmpty(str[2])?"":str[2]);
        }

        return logisticsDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
