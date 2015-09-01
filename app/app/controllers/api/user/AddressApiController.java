package controllers.api.user;

import api.response.user.AddressDto;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import common.utils.SQLUtils;
import controllers.BaseController;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Result;
import usercenter.dtos.AddressForm;
import usercenter.models.User;
import usercenter.models.address.Address;
import usercenter.models.address.Area;
import usercenter.models.address.City;
import usercenter.models.address.Province;
import usercenter.services.AddressService;
import usercenter.services.LinkageService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;

import java.util.ArrayList;
import java.util.List;

/**
 * 送货地址管理
 */
@org.springframework.stereotype.Controller
public class AddressApiController extends BaseController {

    /**
     * 最多只有添加数据
     */
    public static final int DEFAULT_ADDRESS_SIZE = 4;

    @Autowired
    private AddressService addressService;

    @Autowired
    private LinkageService linkageService;

    @SecuredAction
    public Result list() {

        User user = SessionUtils.currentUser();

        List<Address> addressList = addressService.queryAllAddress(user.getId(), true);

        List<AddressDto> addressDtos = new ArrayList<>();
        for(Address address:addressList){
            addressDtos.add(AddressDto.build(address));
        }

        return ok(JsonUtils.object2Node(addressDtos));

    }

    /**
     * 添加送货地址
     *
     * @return
     */
    @SecuredAction
    public Result add() {

        User user = SessionUtils.currentUser();

        Form<AddressForm> addressForm = Form.form(AddressForm.class).bindFromRequest();

        if (!addressForm.hasErrors()) {
            try {
                AddressForm addressF = addressForm.get();

                int addressCount = addressService.getMyAddressCount(user.getId());
                if (addressCount >= DEFAULT_ADDRESS_SIZE) {
                    throw new AppBusinessException(ErrorCode.Conflict, "最多只能添加" + DEFAULT_ADDRESS_SIZE + "个送货地址");
                }

                Address address = new Address();

                if (addressCount == 0) {
                    address.setDefaultAddress(Address.DEFAULT_ADDRESS_TRUE);
                }

                address.setUserId(user.getId());
                address.setName(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getName())));
                address.setLocation(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getLocation())));
                address.setProvince(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getProvince())));
                address.setCity(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getCity())));
                address.setArea(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getDistricts())));
                address.setMobile(addressF.getMobile());
                address.setZipCode(addressF.getZipCode());

                addressService.createAddress(address);

                return ok(JsonUtils.object2Node(AddressDto.build(address)));

            } catch (AppBusinessException e) {
                addressForm.reject("errors", e.getMessage());
            }
        }

        throw new AppBusinessException(ErrorCode.Conflict, addressForm.errorsAsJson().toString());

    }

    /**
     * 更新地址
     *
     * @return
     */
    @SecuredAction
    public Result update() {

        User user = SessionUtils.currentUser();

        Form<AddressForm> addressForm = Form.form(AddressForm.class).bindFromRequest();

        if (!addressForm.hasErrors()) {
            try {
                AddressForm addressF = addressForm.get();

                if (null == addressF.getId() || addressF.getId() == 0) {
                    throw new AppBusinessException(ErrorCode.Conflict, "修改送货地址失败");
                }

                Address oldAddress = addressService.getAddress(addressF.getId(), user.getId());
                if (null == oldAddress) {
                    throw new AppBusinessException(ErrorCode.Conflict, "修改送货地址失败");
                }

                oldAddress.setName(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getName())));
                oldAddress.setLocation(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getLocation())));
                oldAddress.setProvince(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getProvince())));
                oldAddress.setCity(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getCity())));
                oldAddress.setArea(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getDistricts())));
                oldAddress.setMobile(addressF.getMobile());
                oldAddress.setZipCode(addressF.getZipCode());

                addressService.updateAddress(oldAddress);

                return ok(JsonUtils.object2Node(AddressDto.build(oldAddress)));

            } catch (AppBusinessException e) {
                addressForm.reject("errors", e.getMessage());
            }
        }

        throw new AppBusinessException(ErrorCode.Conflict, addressForm.errorsAsJson().toString());
    }

    /**
     * 删除地址
     *
     * @return
     */
    @SecuredAction
    public Result del(int addressId) {

        User user = SessionUtils.currentUser();
        Address address = addressService.getAddress(addressId, user.getId());

        if (null == address) {
            throw new AppBusinessException(ErrorCode.Conflict, "删除送货地址失败");
        }

        address.setDeleted(SQLUtils.SQL_DELETE_TRUE);
        addressService.updateAddress(address);

        return noContent();

    }

    /**
     * 设置默认地址
     *
     * @return
     */
    @SecuredAction
    public Result defaultAddress(int addressId) {

        User user = SessionUtils.currentUser();
        Address address = addressService.getAddress(addressId, user.getId());

        if (null == address) {
            throw new AppBusinessException(ErrorCode.Conflict, "设置送货地址失败");
        }

        addressService.updateDefaultAddress(address, user.getId());

        return noContent();

    }

    /**
     * 获取地址信息
     *
     * @param addressId
     * @return
     */
    @SecuredAction
    public Result getAddress(int addressId) {

        User user = SessionUtils.currentUser();
        Address address = addressService.getAddress(addressId, user.getId());

        if (null == address) {
            throw new AppBusinessException(ErrorCode.Conflict, "获取地址失败");
        }

        return ok(JsonUtils.object2Node(AddressDto.build(address)));

    }

    /**
     * 省份信息
     *
     * @return
     */
    public Result queryProvince() {

        try {
            List<Province> provinceList = linkageService.getAllProvince();
            return ok(JsonUtils.object2Node(provinceList));
        } catch (Exception e) {
            return conflict();
        }

    }

    /**
     * 城市信息
     *
     * @return
     */
    public Result queryCity() {

        try {
            List<City> cityList = linkageService.getAllCity();
            return ok(JsonUtils.object2Node(cityList));
        } catch (Exception e) {
            return conflict();
        }

    }

    /**
     * 区域信息
     *
     * @return
     */
    public Result queryArea() {

        try {
            List<Area> areaList = linkageService.getAllArea();
            return ok(JsonUtils.object2Node(areaList));
        } catch (Exception e) {
            return conflict();
        }

    }

}