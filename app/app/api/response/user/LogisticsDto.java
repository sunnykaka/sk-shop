package api.response.user;

import ordercenter.models.Logistics;

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
    private String linkage;

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
        logisticsDto.setLinkage(logistics.getProvince());
        logisticsDto.setLocation(logistics.getLocation());
        logisticsDto.setMobile(logistics.getMobile());
        logisticsDto.setName(logistics.getName());

        return logisticsDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkage() {
        return linkage;
    }

    public void setLinkage(String linkage) {
        this.linkage = linkage;
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
