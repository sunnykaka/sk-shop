package ordercenter.services;

import com.google.common.collect.Lists;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.Money;
import common.utils.page.Page;
import ordercenter.constants.VoucherBatchStatus;
import ordercenter.constants.VoucherStatus;
import ordercenter.constants.VoucherType;
import ordercenter.dtos.MyVouchers;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.of;

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

    /**
     * 计算活动下的代金券数量
     * @param voucherBatchId
     * @return
     */
    @Transactional(readOnly = true)
    public int countVoucherInBatch(Integer voucherBatchId) {
        String jpql = "select count(v) from Voucher v where v.batchId = :batchId";
        Map<String, Object> params = new HashMap<>();
        params.put("batchId", voucherBatchId);
        List<Long> results = generalDao.query(jpql, Optional.empty(), params);
        return results.get(0).intValue();
    }

    /**
     * 根据编号和类型，查找可用的代金券
     * @param batchUniqueNo
     * @param type
     * @return
     */
    @Transactional(readOnly = true)
    public VoucherBatch findValidVoucherBatch(Optional<String> batchUniqueNo, VoucherType type) {

        String jpql = "select vb from VoucherBatch vb where vb.status = :status and vb.type = :type ";
        Map<String, Object> params = new HashMap<>();
        params.put("status", VoucherBatchStatus.VALID);
        params.put("type", type);
        if(batchUniqueNo.isPresent() && StringUtils.isNotBlank(batchUniqueNo.get())) {
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
     * 根据编号,用户查找代金券
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

        //根据类型统计代金券出现次数
        Map<VoucherType, Integer> typeCountMap =
                vouchers.stream().
                        map(Voucher::getType).
                        collect(Collectors.toMap(Function.identity(), type -> 1, Math::addExact));

        //确保同类型的券只能使用一张
        Optional<Map.Entry<VoucherType, Integer>> entry = typeCountMap.entrySet().stream().filter(e -> e.getValue() > 1).findFirst();
        if(entry.isPresent()) {
            errorMsg = String.format("代金券使用失败, %s使用了%d张", entry.get().getKey().value, entry.get().getValue());
        }

        if(errorMsg != null) {
            throw new VoucherException(errorMsg);
        }

        return vouchers;

    }

    /**
     * 结算页面，查询可用的代金券
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Voucher> findUserAvailableVouchers(Integer userId) {

        String jpql = "select v from Voucher v where v.status = :status and " +
                "(v.userId is null or v.userId = :userId) order by v.amount desc";
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

    /**
     * 创建代金券活动
     * @param voucherBatch
     */
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

        //校验注册送的活动无重复
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

    @Transactional
    public void updateVoucherBatchToInvalid(int batchId) {
        VoucherBatch voucherBatch = getVoucherBatch(batchId);
        voucherBatch.setStatus(VoucherBatchStatus.INVALID);
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

    @Transactional(readOnly = true)
    public MyVouchers findByPage(Page<Voucher> page, Integer userId, Optional<VoucherStatus> status) {

        VoucherStatus currentStatus = status.orElse(VoucherStatus.UNUSED);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("status", currentStatus);

        String jpql = "select v from Voucher v where v.userId = :userId and v.status = :status";

        generalDao.query(jpql, of(page), params);

        //查询其他状态的总条数
        Map<VoucherStatus, Integer> countMap =
                Lists.newArrayList(VoucherStatus.values()).stream().collect(Collectors.toMap(
                        Function.identity(),
                        s -> {
                            if (s.equals(currentStatus)) {
                                return page.getTotalCount();
                            } else {
                                return this.count(userId, of(s));
                            }
                        }
                ));

        Map<VoucherStatus, Integer> sortedMap = new TreeMap<>(Enum::compareTo);
        sortedMap.putAll(countMap);

        return new MyVouchers(page, currentStatus, sortedMap);
    }

    /**
     * 根据用户和状态查询代金券数量
     * @param userId
     * @param status
     * @return
     */
    @Transactional(readOnly = true)
    public int count(Integer userId, Optional<VoucherStatus> status) {

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("status", status.orElse(VoucherStatus.UNUSED));

        String jpql = "select count(1) from Voucher v where v.userId = :userId and v.status = :status";

        List<Long> query = generalDao.query(jpql, Optional.empty(), params);
        return query.get(0).intValue();

    }

    /**
     * 定时器执行，根据时间修改代金券活动和代金券状态
     */
    @Transactional
    public void changeStatus() {

        DateTime now = DateUtils.current();

        //修改代金券活动状态为VALID
        String jpql = "update VoucherBatch set status = :status1 where status = :status2 and" +
                " startTime <= :startTime and (endTime > :endTime or endTime is null) ";
        Map<String, Object> params = new HashMap<>();
        params.put("status1", VoucherBatchStatus.VALID);
        params.put("status2", VoucherBatchStatus.INVALID);
        params.put("startTime", now);
        params.put("endTime", now);
        int updateVoucherBatchToValidCount = generalDao.update(jpql, params);

        //修改代金券活动状态为INVALID
        jpql = "update VoucherBatch set status = :status1 where status = :status2 and" +
                " (startTime >= :startTime or endTime <= :endTime)";
        params = new HashMap<>();
        params.put("status1", VoucherBatchStatus.INVALID);
        params.put("status2", VoucherBatchStatus.VALID);
        params.put("startTime", now);
        params.put("endTime", now);
        int updateVoucherBatchToInvalidCount = generalDao.update(jpql, params);

        //修改代金券状态为OVERDUE
        jpql = "update Voucher set status = :status1 where status = :status2 and" +
                " deadline <= :deadline";
        params = new HashMap<>();
        params.put("status1", VoucherStatus.OVERDUE);
        params.put("status2", VoucherStatus.UNUSED);
        params.put("deadline", now);
        int updateVoucherToOverdueCount = generalDao.update(jpql, params);

        Logger.debug(String.format("定时器更新代金券状态: 代金券活动更新为有效数量: %d, 代金券活动更新为无效数量: %d, 代金券更新为过期数量: %d",
                updateVoucherBatchToValidCount, updateVoucherBatchToInvalidCount, updateVoucherToOverdueCount));

    }
}