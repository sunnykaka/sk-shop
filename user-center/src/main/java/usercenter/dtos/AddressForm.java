package usercenter.dtos;

import play.data.validation.Constraints;

import javax.validation.constraints.Pattern;

/**
 * Created by zhb on 15-5-6.
 */
public class AddressForm {

    private Integer id;

    /**
     * 收获人姓名
     */
    @Constraints.MaxLength(value = 20, message = "输入超过最大20字符限制")
    private String name;

    /**
     * 省份，比如浙江
     */
    @Constraints.MaxLength(value = 200, message = "输入超过最大200字符限制")
    private String province;

    /**
     * 城市
     */
    @Constraints.MaxLength(value = 200, message = "输入超过最大200字符限制")
    private String city;

    /**
     * 区、县  //配合前端 area修改为：districts
     */
    @Constraints.MaxLength(value = 200, message = "输入超过最大200字符限制")
    private String districts;

    /**
     * 具体位置，到门牌号
     */
    @Constraints.MaxLength(value = 200, message = "输入超过最大200字符限制")
    private String location;

    /**
     * 移动电话
     */
    @Pattern(regexp = "^[1][\\d]{10}", message = "请输入正确的手机号码")
    private String mobile;

    /**
     * 邮编
     */
    private String zipCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
