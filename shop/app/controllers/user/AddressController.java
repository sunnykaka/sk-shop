package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import common.utils.SQLUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.dtos.AddressForm;
import usercenter.models.User;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
import usercenter.services.LinkageService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.user.myAddress;

import java.util.List;

/**
 * 送货地址管理
 */
@org.springframework.stereotype.Controller
public class AddressController extends Controller {

    /**
     * 最多只有添加数据
     */
    public static final int DEFAULT_ADDRESS_SIZE = 4;

    @Autowired
    private AddressService addressService;

    @Autowired
    private LinkageService linkageService;

    @SecuredAction
    public Result index() {

        User user = SessionUtils.currentUser();

        List<Address> addressList = addressService.queryAllAddress(user.getId(),false);

        return ok(myAddress.render(addressList));

    }

    @SecuredAction
    public Result list() {

        User user = SessionUtils.currentUser();

        List<Address> addressList = addressService.queryAllAddress(user.getId(),true);

        return ok(new JsonResult(true, null, addressList).toNode());

    }

    /**
     * 省份信息
     *
     * @return
     */
    public Result queryProvince() {

        try {
            return ok(new JsonResult(true, null, linkageService.getAllProvince()).toNode());
        } catch (Exception e) {
            return ok(new JsonResult(false).toNode());
        }

    }

    /**
     * 城市信息
     */
    public Result queryCity(String code) {

        try {
            return ok(new JsonResult(true, null, linkageService.getCityByProvinceCode(code)).toNode());
        } catch (Exception e) {
            return ok(new JsonResult(false).toNode());
        }
    }

    /**
     * 区县信息
     */
    public Result queryArea(String code) {

        try {
            return ok(new JsonResult(true, null, linkageService.getAreaByCityCode(code)).toNode());
        } catch (Exception e) {
            return ok(new JsonResult(false).toNode());
        }
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
                    return ok(new JsonResult(false, "最多只能添加" + DEFAULT_ADDRESS_SIZE + "个送货地址").toNode());
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

                return ok(new JsonResult(true, "添加送货地址成功",address).toNode());

            } catch (AppBusinessException e) {
                addressForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, addressForm.errorsAsJson().toString()).toNode());

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
                    return ok(new JsonResult(false, "修改送货地址失败").toNode());
                }

                Address oldAddress = addressService.getAddress(addressF.getId(), user.getId());
                if (null == oldAddress) {
                    return ok(new JsonResult(false, "修改送货地址失败").toNode());
                }

                oldAddress.setUserId(user.getId());
                oldAddress.setName(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getName())));
                oldAddress.setLocation(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getLocation())));
                oldAddress.setProvince(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getProvince())));
                oldAddress.setCity(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getCity())));
                oldAddress.setArea(StringEscapeUtils.escapeHtml4(StringUtils.trim(addressF.getDistricts())));
                oldAddress.setMobile(addressF.getMobile());
                oldAddress.setZipCode(addressF.getZipCode());

                addressService.updateAddress(oldAddress);

                return ok(new JsonResult(true, "修改送货地址成功").toNode());

            } catch (AppBusinessException e) {
                addressForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, addressForm.errorsAsJson().toString()).toNode());
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
            return ok(new JsonResult(false, "删除送货地址失败").toNode());
        }

        address.setDeleted(SQLUtils.SQL_DELETE_TRUE);
        addressService.updateAddress(address);

        return ok(new JsonResult(true, "删除成功").toNode());

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
            return ok(new JsonResult(false, "设置送货地址失败").toNode());
        }

        boolean result = addressService.updateDefaultAddress(address, user.getId());

        return ok(new JsonResult(result).toNode());

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
            return ok(new JsonResult(false, "获取地址失败").toNode());
        }

        return ok(new JsonResult(true,null,address).toNode());

    }

}