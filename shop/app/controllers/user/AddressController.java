package controllers.user;

import common.utils.JsonResult;
import common.utils.SQLUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
import usercenter.services.LinkageService;
import views.html.user.myAddress;

import java.util.List;

/**
 * 送货地址管理
 */
@org.springframework.stereotype.Controller
public class AddressController extends Controller {

    /** 最多只有添加5条数据 */
    public static final int DEFAULT_ADDRESS_SIZE = 5;

    public static int test_userId = 1;

    @Autowired
    private AddressService addressService;

    @Autowired
    private LinkageService linkageService;

    public Result index() {

       List<Address> addressList = addressService.queryAllAddress(test_userId);

       return ok(myAddress.render(addressList));

    }

    /**
     * 省份信息
     *
     * @return
     */
    public Result queryProvince(){

        try {
            return ok(new JsonResult(true, null, linkageService.getAllProvince()).toNode());
        } catch (Exception e) {
            return ok(new JsonResult(false).toNode());
        }

    }

    /**
     * 城市信息
     *
     */
    public Result queryCity(String code){

        try {
            return ok(new JsonResult(true, null, linkageService.getCityByProvinceCode(code)).toNode());
        } catch (Exception e) {
            return ok(new JsonResult(false).toNode());
        }
    }

    /**
     * 区县信息
     */
    public Result queryArea(String code){

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
    public Result add(){

        Form<Address> addressForm = Form.form(Address.class).bindFromRequest();
        Address address = addressForm.get();

        int addressCount = addressService.getMyAddressCount(test_userId);
        if(addressCount >= DEFAULT_ADDRESS_SIZE){
            return ok(new JsonResult(false, "最多只能添加5个送货地址").toNode());
        }

        if(addressCount == 0){
            address.setDefaultAddress(Address.DEFAULT_ADDRESS_TRUE);
        }

        address.setUserId(test_userId);
        address.setName(StringEscapeUtils.escapeHtml4(address.getName().trim()));
        address.setLocation(StringEscapeUtils.escapeHtml4(address.getLocation().trim()));

        addressService.createAddress(address);

        return ok(new JsonResult(true, "添加送货地址成功").toNode());
    }

    /**
     * 更新地址
     *
     * @return
     */
    public Result update(){

        Form<Address> addressForm = Form.form(Address.class).bindFromRequest();
        Address address = addressForm.get();

        if(null == address.getId() || address.getId() == 0){
            return ok(new JsonResult(false, "修改送货地址失败").toNode());
        }

        Address oldAddress = addressService.getAddress(address.getId(),test_userId);
        if(null == oldAddress){
            return ok(new JsonResult(false, "修改送货地址失败").toNode());
        }

        address.setUserId(test_userId);
        address.setName(StringEscapeUtils.escapeHtml4(address.getName().trim()));
        address.setLocation(StringEscapeUtils.escapeHtml4(address.getLocation().trim()));
        addressService.updateAddress(address);

        return ok(new JsonResult(true, "修改送货地址成功").toNode());
    }

    /**
     * 删除地址
     *
     * @return
     */
    public Result del(){

        Form<Address> addressForm = Form.form(Address.class).bindFromRequest();
        Address address = addressForm.get();
        if(null == address.getId() || address.getId() == 0){
            return ok(new JsonResult(false, "删除送货地址失败").toNode());
        }

        Address oldAddress = addressService.getAddress(address.getId(), test_userId);
        if(null == oldAddress){
            return ok(new JsonResult(false, "删除送货地址失败").toNode());
        }

        oldAddress.setDeleted(SQLUtils.SQL_DELETE_TRUE);
        addressService.updateAddress(oldAddress);

        return ok(new JsonResult(true, "删除成功").toNode());

    }

    /**
     * 设置默认地址
     *
     * @return
     */
    public Result defaultAddress(){

        Form<Address> addressForm = Form.form(Address.class).bindFromRequest();
        Address address = addressForm.get();
        if(null == address.getId() || address.getId() == 0){
            return ok(new JsonResult(false, "设置送货地址失败").toNode());
        }

        boolean result = addressService.updateDefaultAddress(address.getId(),test_userId);

        return ok(new JsonResult(result).toNode());

    }

}