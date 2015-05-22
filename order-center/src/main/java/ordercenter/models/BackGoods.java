package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import ordercenter.services.BackGoodsService;
import ordercenter.services.OrderService;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import usercenter.constants.AccountType;

import javax.persistence.*;
import java.util.*;

/**
 * 退货单数据.
 */
@Table(name = "backGoods")
@Entity
public class BackGoods implements EntityClass<Integer>, OperableData {

    /**
     * 退货单编号
     */
    private Integer id;

    /**
     * 订单编号
     */
    private Long orderNo;

    /**
     * 冗余订单ID
     */
    private Integer orderId;


    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 用户名(第三方账户可能会重复)
     */
    private String userName;

    /**
     * 用户账户类型(账户类型, QQ、weibo 等, KRQ 代表我们自己)
     */
    private AccountType accountType;

    /**
     * 退货单物流编号
     */
    private String expressNo;


    /**
     *退货的详细描述
     */
    private String backReason;


    /**
     * 退货原因  质量问题    非质量问题
     */
    private BackReason backReasonReal;

    /**
     *退货的处理方式  退货 退款 保修  目前数据库还没有此字段，以后可能会加
     */
    private ProcessMode processMode;

    /**
     *  退货地址
     */
    private String backAddress;

    /**
     * 联系人姓名
     */
    private String backShopperName;

    /**
     * 联系人号码
     */
    private String backPhone;

    /**
     * 退货金额
     */
    private Money backPrice = Money.valueOf(0);

    /**
     * 上传图片的地址
     */
    private String uploadFiles;

    /**
     * 退货状态
     */
    private BackGoodsState backState;

    /**
     * 退货类型, 已发货 或 未发货. 默认是已发货
     */
    private BackGoodsState.BackGoodsType backType = BackGoodsState.BackGoodsType.YetSend;

    /**
     * 必须的订单前置状态, 主要在 SQL 更新时, 不写入数据库.
     */
    private BackGoodsState mustPreviousState;

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 更新时间
     */
    private DateTime updateTime;

    /**
     * 是否删除(1 表示已删除, 0 表示未删除), 默认是 0
     */
    private boolean deleted = false;

    /**
     * 退货单项
     */
    private List<BackGoodsItem> backGoodsItemList = new ArrayList<BackGoodsItem>();

    public void addBackGoodsItem(BackGoodsItem backGoodsItem) {
        backGoodsItemList.add(backGoodsItem);
    }

    /**
     * 检查退货单是否能退款.
     *
     * @return 若不能退款则返回 true.
     */
    public boolean checkCanNotRefunds() {
        return backType.checkCanNotRefunds(backState);
    }

    /**
     * 创建退货单项.
     */
//    public void createBackItems(BackGoodsService backGoodsService){
//        for (BackGoodsItem backItem : backGoodsItemList) {
//            // 创建退货单项
//            backItem.setBackGoodsId(id);
//
//            backGoodsService.createBackGoodsItem(backItem);
//        }
//    }

    /** 检查退货项数据, 并统计退货价格 */
//    public void checkBackItemAndCalculatePrice(OrderService orderService, BackGoodsService backGoodsService) {
//        // 判断退货数量
//        List<Integer> backGoodsIdList = backGoodsService.queryBackGoodsIdByOrderNoAndUserId(orderNo, userId);
//        for (BackGoodsItem backGoodsItem : backGoodsItemList) {
//            // 判断退货单项数量和是否有退过货
//            backGoodsItem.checkSpecification(backGoodsIdList, orderService, backGoodsService);
//
//            backPrice += backGoodsItem.totalPrice();
//        }
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

    @Column(name = "orderNo")
    @Basic
    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    @Column(name = "userId")
    @Basic
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "userName")
    @Basic
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "accountType")
    @Enumerated(EnumType.STRING)
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @Column(name = "uploadFiles")
    @Basic
    public String getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(String uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    @Column(name = "expressNo")
    @Basic
    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    @Column(name = "backReasonReal")
    @Enumerated(EnumType.STRING)
    public BackReason getBackReasonReal() {
        return backReasonReal;
    }

    public void setBackReasonReal(BackReason backReasonReal) {
        this.backReasonReal = backReasonReal;
    }

    @Column(name = "backAddress")
    @Basic
    public String getBackAddress() {
        return backAddress;
    }

    public void setBackAddress(String backAddress) {
        this.backAddress = backAddress;
    }

    @Column(name = "backReason")
    @Basic
    public String getBackReason() {
        return backReason;
    }

    public void setBackReason(String backReason) {
        this.backReason = backReason;
    }

    @Column(name = "processMode")
    @Enumerated(EnumType.STRING)
    public ProcessMode getProcessMode() {
        return processMode;
    }

    public void setProcessMode(ProcessMode processMode) {
        this.processMode = processMode;
    }

    @Column(name = "backShopperName")
    @Basic
    public String getBackShopperName() {
        return backShopperName;
    }

    public void setBackShopperName(String backShopperName) {
        this.backShopperName = backShopperName;
    }

    @Column(name = "backPhone")
    @Basic
    public String getBackPhone() {
        return backPhone;
    }

    public void setBackPhone(String backPhone) {
        this.backPhone = backPhone;
    }

    @Column(name = "backPrice")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getBackPrice() {
        return backPrice;
    }

    public void setBackPrice(Money backPrice) {
        this.backPrice = backPrice;
    }

    @Column(name = "backState")
    @Enumerated(EnumType.STRING)
    public BackGoodsState getBackState() {
        return backState;
    }

    public void setBackState(BackGoodsState backState) {
        this.backState = backState;
    }

    /**
     * 退货类型, 已发货 或 未发货. 默认是已发货
     */
    @Column(name = "backType")
    @Enumerated(EnumType.STRING)
    public BackGoodsState.BackGoodsType getBackType() {
        return backType;
    }

    /**
     * 退货类型, 已发货 或 未发货. 默认是已发货
     */
    public void setBackType(BackGoodsState.BackGoodsType backType) {
        this.backType = backType;
    }

    /**
     * 必须的订单前置状态, 主要在 SQL 更新时, 不写入数据库.
     */
    @Transient
    public BackGoodsState getMustPreviousState() {
        return mustPreviousState;
    }

    /**
     * 必须的订单前置状态, 主要在 SQL 更新时, 不写入数据库.
     */
    @Transient
    public void setMustPreviousState(BackGoodsState mustPreviousState) {
        this.mustPreviousState = mustPreviousState;
    }

    @Column(name = "createTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Column(name = "updateTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getUpdateTime() {
        return updateTime;
    }

    @Column(name = "isDelete")
    @Basic
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "backGoodsId")
    public List<BackGoodsItem> getBackGoodsItemList() {
        return backGoodsItemList;
    }

    public void setBackGoodsItemList(List<BackGoodsItem> backGoodsItemList) {
        this.backGoodsItemList = backGoodsItemList;
    }

    @Column(name = "orderId")
    @Basic
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public static enum BackReason {
        /**
         * 质量问题
         */
        QualityProblem,

        /**
         * 非质量问题
         */
        NoQualityProblem;

        private static Map<BackReason, String> mapping = new HashMap<BackReason, String>();

        static {
            mapping.put(QualityProblem, "质量问题");
            mapping.put(NoQualityProblem, "非质量问题");
        }

        public String toDesc() {
            return mapping.get(this);
        }

        }

    public static enum ProcessMode {
        /**
         * 退货
         */
        BackGood("退货"),
        /**
         * 退款
         */
        Reimburse("退款"),
        /**
         * 保修
         */
        repair("保修");

        ProcessMode(String value) {
            this.value = value;
        }

        private String value;

    }

    public static class UploadFiles {
        private String original;

        private String compress;

        public UploadFiles(){}

        public UploadFiles(String original, String compress) {
            this.original = original;
            this.compress = compress;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }

        public String getCompress() {
            return compress;
        }

        public void setCompress(String compress) {
            this.compress = compress;
        }
    }


}
