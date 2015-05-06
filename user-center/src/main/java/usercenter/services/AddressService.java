package usercenter.services;

import common.services.GeneralDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.models.address.Address;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * User: Asion
 * Date: 12-5-30
 * Time: 下午5:50
 */
@Service
@Transactional
public class AddressService {

    @Autowired
    private GeneralDao generalDAO;

    /**
     * 根据用户ID查询出这个用户的所有地址对象
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Address> queryAllAddress(int userId){

        String jpql = "select a from Address a where 1=1 and a.deleted=false ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and a.userId = :userId ";
        queryParams.put("userId", userId);

        jpql += " order by a.defaultAddress desc ";

        return generalDAO.query(jpql, Optional.empty(), queryParams);
    }

    /**
     * 查询我总共有多少条地址
     *
     * @param userId
     * @return
     */
    public int getMyAddressCount(int userId){
        List<Address> addressList = queryAllAddress(userId);
        if(null == addressList || addressList.size() == 0){
            return 0;
        }

        return addressList.size();
    }

    /**
     * 创建地址
     *
     * @param address
     */
    public void createAddress(Address address){

        generalDAO.persist(address);

    }


    /**
     * 更新地址
     *
     * @param address
     */
    public void updateAddress(Address address){

        generalDAO.merge(address);

    }


    /**
     * 修改默认设置
     *
     * @param address
     * @param userId
     */
    public boolean updateDefaultAddress(Address address,int userId){

        Address oldAddress = queryDefaultAddress(userId);

        if(null != address){
            address.setDefaultAddress(Address.DEFAULT_ADDRESS_TRUE);
            oldAddress.setDefaultAddress(Address.DEFAULT_ADDRESS_FALSE);
            updateAddress(address);
            updateAddress(oldAddress);
            return true;
        }

        return false;

    }

    /**
     * 查询某个用户的缺省地址
     *
     * @param userId
     * @return
     */
    public Address queryDefaultAddress(int userId){

        String jpql = "select a from Address a where 1=1 and a.deleted=false and a.defaultAddress=true ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and a.userId = :userId ";
        queryParams.put("userId", userId);

        List<Address> valueList = generalDAO.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;
    }

    /**
     * 根据地址ID删除地址
     *
     * @param addressId
     */
    public void deleteAddress(int addressId){

    }

    /**
     * 根据地址ID 用户ID删除地址
     *
     * @param addressId
     */
    public void deleteAddress(int addressId, int userId){

    }

    /**
     * 根据用户Id增加地址使用频率
     */
    public void updateFrequencyByAddressId(int id){

    }

    /**
     * 根据地址Id获取详细信息
     */
    public Address getAddress(int addressId){
        return null;
    }

    /**
     * 根据地址Id 用户ID获取详细信息
     */
    public Address getAddress(int addressId, int userId){

        String jpql = "select a from Address a where 1=1 and a.deleted=false";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and a.userId = :userId ";
        queryParams.put("userId", userId);

        jpql += " and a.id = :addressId ";
        queryParams.put("addressId", addressId);

        List<Address> valueList = generalDAO.query(jpql, Optional.empty(), queryParams);
        if (valueList != null && valueList.size() > 0) {
            return valueList.get(0);
        }

        return null;
    }
}
