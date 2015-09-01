package api.response.user;

import usercenter.models.address.Address;

/**
 * Created by Administrator on 2015/9/1.
 */
public class AddressDto {

    /**
     * 地址Id
     */
    private Integer id;


    /**
     * 是谁的地址
     */
    private int userId;

    /**
     * 收获人姓名
     */
    private String name;

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

    /**
     * 是否默认
     */
    private boolean defaultAddress;

    public static AddressDto build(Address address){

        if(address == null){return null;}

        AddressDto addressDto = new AddressDto();
        addressDto.setId(address.getId());
        addressDto.setArea(address.getArea());
        addressDto.setCity(address.getCity());
        addressDto.setDefaultAddress(address.isDefaultAddress());
        addressDto.setLocation(address.getLocation());
        addressDto.setMobile(address.getMobile());
        addressDto.setName(address.getName());
        addressDto.setProvince(address.getProvince());
        addressDto.setUserId(address.getUserId());

        return addressDto;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
