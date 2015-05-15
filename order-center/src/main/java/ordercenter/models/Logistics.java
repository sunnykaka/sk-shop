package ordercenter.models;

import common.models.utils.EntityClass;
import usercenter.models.address.Address;

import javax.persistence.*;

/**
 * 物流
 * 这个对象冗余了地址的信息，这样即便地址删除了，订单还保留这个信息
 * User: lidujun
 * Date: 2015-05-08
 */
@Table(name = "Logistics")
@Entity
public class Logistics implements EntityClass<Integer> {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单id
     */
    private int orderId;

    /**
     * 地址id
     */
    private int addressId;

    /**
     * 下单人
     */
    private String addressOwner;

    /**
     * 收获人姓名
     */
    private String name;

    /**
     * 省份，比如浙江
     */
    private String province;

    /**
     * 具体位置，到门牌号
     */
    private String location;

    /**
     * 移动电话
     */
    private String mobile;

    /**
     * 固定电话
     */
    private String telephone;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 邮编
     */
    private String zipCode;

    //配送信息
    //private DeliveryInfo deliveryInfo;

    //冗余地址信息，当用户删除地址的时候，地址信息还在
    //private LogisticsRedundancy logisticsRedundancy;

    public Logistics(){}

    /**
     * 注入冗余的地址信息，物流对象保存一份当时订单的地址数据
     *
     * @param address
     */
    public Logistics(Address address) {
        this.setAddressId(address.getId());
        this.setEmail(address.getEmail());
        this.setLocation(address.getLocation());
        this.setProvince(address.getProvince());
        this.setTelephone(address.getTelephone());
        this.setMobile(address.getMobile());
        this.setZipCode(address.getZipCode());
        this.setName(address.getName());
    }

//    public LogisticsRedundancy getLogisticsRedundancy() {
//        return logisticsRedundancy;
//    }
//
//    public void setLogisticsRedundancy(LogisticsRedundancy logisticsRedundancy) {
//        this.logisticsRedundancy = logisticsRedundancy;
//    }


    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "orderId")
    @Basic
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Column(name = "addressId")
    @Basic
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    @Column(name = "addressOwner")
    @Basic
    public String getAddressOwner() {
        return addressOwner;
    }

    public void setAddressOwner(String addressOwner) {
        this.addressOwner = addressOwner;
    }

    @Column(name = "name")
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "province")
    @Basic
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name = "location")
    @Basic
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Column(name = "mobile")
    @Basic
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(name = "telephone")
    @Basic
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "email")
    @Basic
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "zipCode")
    @Basic
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
