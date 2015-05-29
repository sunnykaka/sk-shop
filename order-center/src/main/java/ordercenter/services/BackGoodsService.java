package ordercenter.services;

import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.Money;
import common.utils.page.Page;
import ordercenter.dtos.BackApplyForm;
import ordercenter.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.models.User;

import java.util.*;

/**
 * Created by zhb on 15-5-19.
 */
@Transactional
@Service
public class BackGoodsService {

    @Autowired
    private GeneralDao generalDao;

    @Autowired
    private OrderService orderService;

    /**
     * 处理退货订单信息
     *
     * @param backApply
     * @param user
     */
    public void submitBackGoods(BackApplyForm backApply,User user){

        Map<Integer,OrderItem> orderItemMap = new HashMap<>();
        Order order = orderService.getOrderById(backApply.getOrderId(), user.getId());
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItemMap.put(orderItem.getId(),orderItem);
        }

        List<BackGoodsItem> backGoodsItems = new ArrayList<>();
        List<String> orderItemStr = backApply.getOrderItem();

        if(orderItemStr.size() <= 0){
            throw new AppBusinessException("退货失败，没有选中退货商品！");
        }

        try{
            Money backPrice = Money.valueOf(0);

            for(String str:orderItemStr){

                BackGoodsItem backGoodsItem = new BackGoodsItem();
                String[] item = str.split("-");
                int orderItemId = Integer.parseInt(item[0]);
                int number = Integer.parseInt(item[1]);

                OrderItem orderItem = orderItemMap.get(orderItemId);
                if(null == orderItem){
                    throw new AppBusinessException("退货数据异常，请重新操作!");
                }

                if(number > orderItem.getNumber()){
                    throw new AppBusinessException("退货数据异常，请重新操作!");
                }

                backGoodsItem.setNumber(number);
                backGoodsItem.setUnitPrice(orderItem.getCurUnitPrice());
                backGoodsItem.setOrderState(order.getOrderState());
                backGoodsItem.setOrderItemId(orderItemId);
                backGoodsItems.add(backGoodsItem);

                backPrice = backPrice.add(orderItem.getCurUnitPrice().multiply(number));

            }

            BackGoods backGoods = new BackGoods();
            backGoods.setUserId(user.getId());
            backGoods.setAccountType(user.getAccountType());
            backGoods.setBackReason(backApply.getBackReason());
            backGoods.setBackReasonReal(BackGoods.BackReason.NoQualityProblem);
            backGoods.setOrderId(order.getId());
            backGoods.setOrderNo(order.getOrderNo());
            backGoods.setBackPrice(backPrice);
            backGoods.setBackPhone(user.getPhone());
            backGoods.setUserName(user.getUserName());
            backGoods.setBackGoodsItemList(backGoodsItems);
            backGoods.setBackState(BackGoodsState.Create);
            backGoods.setBackType(BackGoodsState.BackGoodsType.NoSend);

            createBackGoods(backGoods);

        }catch (Exception e){
            throw new AppBusinessException("退货数据异常，请重新操作!");
        }

    }

    /**
     * 创建退货订单
     *
     * @param backGoods
     */
    public void createBackGoods(BackGoods backGoods){

        generalDao.persist(backGoods);
        for(BackGoodsItem backGoodsItem:backGoods.getBackGoodsItemList()){
            backGoodsItem.setBackGoodsId(backGoods.getId());
            generalDao.persist(backGoodsItem);
        }
        BackGoodsLog backGoodsLog = new BackGoodsLog(backGoods,backGoods.getUserName(),"创建退货单,等待审核","");
        generalDao.persist(backGoodsLog);

    }

    /**
     * 取消退货
     *
     * @param backGoodsId
     * @return
     */
    public void cancelBackApply(int backGoodsId,int userId){

        BackGoods backGoods = getBackGoods(backGoodsId,userId);
        if(null == backGoods){
            throw new AppBusinessException("取消退货失败，没有找到退货订单");
        }

        if(backGoods.getUserId() != userId){
            throw new AppBusinessException("取消退货失败，找不到退货订单");
        }

        if(backGoods.getBackState().checkCanNotCancelForUser()){
            throw new AppBusinessException("取消退货失败，退货订单状态不可取消");
        }

        backGoods.setBackState(BackGoodsState.Cancel);
        generalDao.merge(backGoods);

        //创建日志
        BackGoodsLog backGoodsLog = new BackGoodsLog(backGoods,backGoods.getUserName(),"取消退货","");
        generalDao.persist(backGoodsLog);

    }

    /**
     * 查找我的退货订单
     *
     * @param page
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<BackGoods> getMyBackGoods(Optional<Page<BackGoods>> page, int userId){

        String jpql = "select o from BackGoods o join o.backGoodsItemList oi where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.userId = :userId ";
        queryParams.put("userId", userId);


        jpql += " group by o.id order by o.id desc";

        return generalDao.query(jpql,page,queryParams);

    }

    /**
     * 查找我的退货订单
     *
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    public List<BackGoods> getBackGoodsByOrderId(int orderId){

        String jpql = "select o from BackGoods where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.orderId = :orderId ";
        queryParams.put("orderId", orderId);

        return generalDao.query(jpql, Optional.<Page<BackGoods>>empty(), queryParams);

    }

    /**
     * 查找我的退货订单
     *
     * @param backGoodsId
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public BackGoods getBackGoods(int backGoodsId, int userId){

        String jpql = "select o from BackGoods o join o.backGoodsItemList oi where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.userId = :userId ";
        queryParams.put("userId", userId);
        jpql += " and o.id = :backGoodsId ";
        queryParams.put("backGoodsId", backGoodsId);

        List<BackGoods> backGoodsList = generalDao.query(jpql, Optional.empty(), queryParams);

        BackGoods backGoods = null;
        if(backGoodsList != null && backGoodsList.size() > 0) {
            backGoods = backGoodsList.get(0);
        }
        return backGoods;
    }

    /**
     * 查询退货订单历史
     *
     * @param backGoodsId
     * @return
     */
    @Transactional(readOnly = true)
    public List<BackGoodsLog> getBackGoodsLog(int backGoodsId){

        String jpql = "select o from BackGoodsLog o where 1=1 and o.backGoodsId=:backGoodsId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("backGoodsId", backGoodsId);
        return generalDao.query(jpql, Optional.<Page<BackGoodsLog>>empty(), queryParams);

    }

}
