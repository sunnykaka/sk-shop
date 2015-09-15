package controllers.api.user;

import api.response.user.AddressDto;
import api.response.user.AreaDto;
import api.response.user.CityDto;
import api.response.user.ProvinceDto;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.FormUtils;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
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

        User user = this.currentUser();

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

        User user = this.currentUser();

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

                addressService.createAddress(address);

                return noContent();

            } catch (AppBusinessException e) {
                addressForm.reject("errors", e.getMessage());
            }
        }

        throw new AppBusinessException(ErrorCode.Conflict, FormUtils.showErrorInfo(addressForm.errors()));

    }

    /**
     * 更新地址
     *
     * @return
     */
    @SecuredAction
    public Result update() {

        User user = this.currentUser();

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

                addressService.updateAddress(oldAddress);

                return noContent();

            } catch (AppBusinessException e) {
                addressForm.reject("errors", e.getMessage());
            }
        }

        throw new AppBusinessException(ErrorCode.Conflict, FormUtils.showErrorInfo(addressForm.errors()));
    }

    /**
     * 删除地址
     *
     * @return
     */
    @SecuredAction
    public Result del(int id) {

        User user = this.currentUser();
        Address address = addressService.getAddress(id, user.getId());

        if (null == address) {
            throw new AppBusinessException(ErrorCode.Conflict, "没有该收货地址信息");
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
    public Result defaultAddress(int id) {

        User user = this.currentUser();
        Address address = addressService.getAddress(id, user.getId());

        if (null == address) {
            throw new AppBusinessException(ErrorCode.Conflict, "没有该收货地址信息");
        }

        addressService.updateDefaultAddress(address, user.getId());

        return noContent();

    }

    /**
     * 获取地址信息
     * @return
     */
    @SecuredAction
    public Result getAddress(int id){
        User user = this.currentUser();
        Address address = addressService.getAddress(ParamUtils.getObjectId(request()), user.getId());

        if (null == address) {
            throw new AppBusinessException(ErrorCode.Conflict, "没有该收货地址信息");
        }

        return ok(JsonUtils.object2Node(AddressDto.build(address)));

    }

    /**
     * 省份、城市、区域联动
     * TODO for查询内容可能多，后面优化
     *
     * @return
     */
    public Result getProvinceCityInfo(){
        try {

            List<ProvinceDto> provinceDtos = new ArrayList<>();
            List<Province> provinceList = linkageService.getAllProvince();
            for(Province province:provinceList){
                ProvinceDto provinceDto = ProvinceDto.build(province);
                List<City> cityList = linkageService.getCityByProvinceCode(province.getId().toString());
                List<CityDto> cityDtos = new ArrayList<>();
                for(City city:cityList){
                    CityDto cityDto = CityDto.build(city);
                    List<Area> areaList = linkageService.getAreaByCityCode(city.getId().toString());
                    List<AreaDto> areaDtos = new ArrayList<>();
                    for(Area area:areaList){
                        areaDtos.add(AreaDto.build(area));
                    }
                    cityDto.setAreas(areaDtos);
                    cityDtos.add(cityDto);
                }
                provinceDto.setCitys(cityDtos);
                provinceDtos.add(provinceDto);
            }

            return ok(JsonUtils.object2Node(provinceDtos));
        } catch (Exception e) {
            return conflict();
        }
    }

}