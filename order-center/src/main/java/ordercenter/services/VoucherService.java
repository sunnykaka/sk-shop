package ordercenter.services;

import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.Money;
import ordercenter.constants.VoucherBatchStatus;
import ordercenter.constants.VoucherStatus;
import ordercenter.constants.VoucherType;
import ordercenter.excepiton.VoucherException;
import ordercenter.models.Order;
import ordercenter.models.Voucher;
import ordercenter.models.VoucherBatch;
import ordercenter.models.VoucherUse;
import ordercenter.util.VoucherUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;

import java.util.*;

/**
 * 代金券服务
 */
@Service
public class VoucherService {

    @Autowired
    private GeneralDao generalDao;

    /**
     * 通过活动请求代金券
     * @param batchUniqueNo
     * @param userId
     * @param quantity
     * @return
     * @throws VoucherException 需要对异常做处理，可以将异常中的message显示给用户
     */
    @Transactional
    public List<Voucher> requestForActivity(Optional<String> batchUniqueNo, int userId, int quantity) {

        VoucherBatch voucherBatch = findValidVoucherBatch(batchUniqueNo, VoucherType.RECEIVE_BY_ACTIVITY);

        return createVoucherByBatch(voucherBatch, userId, quantity);

    }

    /**
     * 用户注册的时候自动发送代金券
     * @param userId
     * @param quantity
     * @return
     * @throws VoucherException 需要对异常做处理，可以将异常中的message显示给用户
     */
    @Transactional
    public List<Voucher> requestForRegister(int userId, int quantity) {

        VoucherBatch voucherBatch = findValidVoucherBatch(Optional.empty(), VoucherType.FIRE_BY_REGISTER);

        return createVoucherByBatch(voucherBatch, userId, quantity);

    }


    private List<Voucher> createVoucherByBatch(VoucherBatch voucherBatch, int userId, int quantity) {

        DateTime now = DateUtils.current();
        List<Voucher> voucherList = new ArrayList<>(quantity);

        if(voucherBatch == null || quantity <= 0) {
            return voucherList;
        }

        //代金券数量有限制
        if(voucherBatch.getMaxQuantity() != null) {
            int count = countVoucherInBatch(voucherBatch.getId());
            if(count + quantity > voucherBatch.getMaxQuantity()) {
                Logger.warn(String.format(
                        "代金券数量达到上限，代金券批次编号[%s], 已有数量[%d], 请求数量[%d], 最大允许数量[%d]",
                        voucherBatch.getUniqueNo(), count, quantity, voucherBatch.getMaxQuantity()));
                throw new VoucherException("您来晚了一步，代金券已被抢完");
            }
        }

        for (int i = 0; i < quantity; i++) {
            Voucher voucher = new Voucher();
            voucher.setAmount(voucherBatch.getAmount());
            voucher.setBatchId(voucherBatch.getId());
            voucher.setCreateTime(now);
            voucher.setDeadline(voucherBatch.computeVoucherDeadline(now));
            voucher.setMinOrderAmount(voucherBatch.getMinOrderAmount());
            voucher.setStatus(VoucherStatus.UNUSED);
            voucher.setType(voucherBatch.getType());
            voucher.setUniqueNo(VoucherUtil.generateVoucherUniqueNo(voucherBatch));
            voucher.setUserId(userId);

            generalDao.persist(voucher);
            voucherList.add(voucher);
        }

        return voucherList;
    }

    @Transactional(readOnly = true)
    public int countVoucherInBatch(Integer voucherBatchId) {
        String jpql = "select count(v) from Voucher v where v.batchId = :batchId";
        Map<String, Object> params = new HashMap<>();
        params.put("batchId", voucherBatchId);
        List<Long> results = generalDao.query(jpql, Optional.empty(), params);
        return results.get(0).intValue();
    }

    @Transactional(readOnly = true)
    public VoucherBatch findValidVoucherBatch(Optional<String> batchUniqueNo, VoucherType type) {

        String jpql = "select vb from VoucherBatch vb where vb.status = :status and vb.type = :type ";
        Map<String, Object> params = new HashMap<>();
        params.put("status", VoucherBatchStatus.VALID);
        params.put("type", type);
        if(batchUniqueNo.isPresent() && StringUtils.isNoneBlank(batchUniqueNo.get())) {
            jpql += " and vb.uniqueNo = :uniqueNo";
            params.put("uniqueNo", batchUniqueNo.get());
        }

        List<VoucherBatch> results = generalDao.query(jpql, Optional.empty(), params);
        if(results.isEmpty()) {
            return null;
        } else if(results.size() == 1) {
            return results.get(0);
        } else {
            Logger.error(String.format(
                    "findValidVoucherBatch方法根据batchUniqueNo[%s], type[%s]查出来的数据条数为[%d]，程序有bug?",
                    batchUniqueNo, type.toString(), results.size()));
            throw new VoucherException("获取代金券失败");
        }


    }

    /**
     *
     * @param voucherUniqueNoList
     * @param userId
     * @param totalOrderMoney
     * @return
     * @throws VoucherException
     */
    @Transactional(readOnly = true)
    public List<Voucher> findVouchersByUniqueNo(List<String> voucherUniqueNoList, Integer userId, Money totalOrderMoney) {

        List<Voucher> vouchers = findVouchers(voucherUniqueNoList);
        String errorMsg = null;
        for(Voucher voucher : vouchers) {
            if(!VoucherStatus.UNUSED.equals(voucher.getStatus())) {
                errorMsg = String.format("代金券%s%s, 请重新下单", voucher.getUniqueNo(), voucher.getStatus().value);
                Logger.warn(errorMsg);
                break;
            }
            if(voucher.getUserId() != null && !voucher.getUserId().equals(userId)) {
                errorMsg = String.format("代金券%s使用失败, 请重新下单", voucher.getUniqueNo());
                Logger.warn(String.format("代金券%s使用失败, 绑定用户[%s], 当前用户[%d]",
                        voucher.getUniqueNo(), String.valueOf(voucher.getUserId()), userId));
                break;
            }
            if(totalOrderMoney.getAmount() < voucher.getMinOrderAmount().getAmount()) {
                errorMsg = String.format("代金券%s使用失败, 订单金额需要大于%.2f元", voucher.getUniqueNo(), voucher.getMinOrderAmount().getAmount());
                Logger.warn(errorMsg);
                break;
            }
        }
        if(errorMsg != null) {
            throw new VoucherException(errorMsg);
        }

        return vouchers;

    }

    /**
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Voucher> findVouchersByUser(Integer userId) {

        String jpql = "select v from Voucher v where v.status = :status and " +
                "(v.userId is null or v.userId = :userId) ";
        Map<String, Object> params = new HashMap<>();
        params.put("status", VoucherStatus.UNUSED);
        params.put("userId", userId);

        return generalDao.query(jpql, Optional.empty(), params);
    }

    private List<Voucher> findVouchers(List<String> voucherUniqueNoList) {
        if(voucherUniqueNoList == null || voucherUniqueNoList.isEmpty()) return new ArrayList<>();

        StringBuilder jpql = new StringBuilder("select v from Voucher v where v.uniqueNo in (");
        Map<String, Object> params = new HashMap<>();
        for(int i = 0; i < voucherUniqueNoList.size(); i++) {
            String voucherUniqueNo = voucherUniqueNoList.get(i);
            String key = "uniqueNo" + i;
            jpql.append(":").append(key).append(",");
            params.put(key, voucherUniqueNo);
        }
        jpql.replace(jpql.length() - 1, jpql.length(), ")");

        return generalDao.query(jpql.toString(), Optional.empty(), params);
    }

    @Transactional
    public void useVoucherForOrder(List<Order> orders, List<Voucher> vouchers) {

        for(Voucher voucher : vouchers) {

            voucher.setStatus(VoucherStatus.USED);
            voucher.setUseTime(DateUtils.current());
            generalDao.persist(voucher);

            for(Order order : orders) {
                VoucherUse voucherUse = new VoucherUse();
                voucherUse.setOrderId(order.getId());
                voucherUse.setVoucherId(voucher.getId());
                generalDao.persist(voucherUse);
            }
        }

    }

    @Transactional
    public void createVoucherBatch(VoucherBatch voucherBatch) {
        if(voucherBatch.getType() == null) {
            throw new VoucherException("创建代金券活动失败，类型不能为空");
        }
        if(voucherBatch.getAmount().getAmount() <= 0d) {
            throw new VoucherException("创建代金券活动失败，代金券面额需要大于0");
        }
        if(voucherBatch.getStartTime() == null) {
            throw new VoucherException("创建代金券活动失败，需要选择开始时间");
        }
        if(voucherBatch.getDeadline() == null && voucherBatch.getPeriodDay() == null) {
            throw new VoucherException("创建代金券活动失败，需要在代金券期限或者有效期中间至少填写一项");
        }

        if(voucherBatch.getType().equals(VoucherType.FIRE_BY_REGISTER)) {
            VoucherBatch oldVoucherBatch = findValidVoucherBatch(Optional.empty(), VoucherType.FIRE_BY_REGISTER);
            if(oldVoucherBatch != null) {
                throw new VoucherException("创建代金券活动失败，用户注册自动赠送并且状态为有效的代金券活动在系统已存在");
            }
        }

        voucherBatch.setStatus(VoucherBatchStatus.INVALID);
        voucherBatch.setUniqueNo(VoucherUtil.generateVoucherBatchUniqueNo());

        generalDao.persist(voucherBatch);

    }

    @Transactional
    public void updateVoucherBatchToValid(int batchId) {
        VoucherBatch voucherBatch = getVoucherBatch(batchId);
        voucherBatch.setStatus(VoucherBatchStatus.VALID);
        generalDao.persist(voucherBatch);
    }

    @Transactional(readOnly = true)
    public Voucher getVoucher(Integer id) {
        return generalDao.get(Voucher.class, id);
    }

    @Transactional(readOnly = true)
    public VoucherBatch getVoucherBatch(Integer id) {
        return generalDao.get(VoucherBatch.class, id);
    }


}