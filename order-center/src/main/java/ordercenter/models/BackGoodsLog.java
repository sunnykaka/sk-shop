package ordercenter.models;

import common.models.utils.EntityClass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;


@Table(name = "backGoodsLog")
@Entity
public class BackGoodsLog implements EntityClass<Integer> {

    private Integer id;

    /**
     * 退货单编号
     */
    private Integer backGoodsId;

    /**
     * 操作者
     */
    private String userName;

    /**
     * 做了什么
     */
    private String doWhat;

    /**
     * 操作说明(类似于客服备注)
     */
    private String remark;

    /**
     * 操作时间
     */
    private DateTime operaTime;

    /**
     * 操作时的退货状态
     */
    private BackGoodsState backState;

    public BackGoodsLog() {
    }

    public BackGoodsLog(BackGoods backGoods, String userName, String doWhat, String remark) {
        this.backGoodsId = backGoods.getId();
        this.userName = userName;
        this.doWhat = doWhat;
        this.remark = remark;
        this.backState = backGoods.getBackState();
        this.operaTime = DateTime.now();
    }

    public BackGoodsLog(BackGoods backGoods, BackGoodsState backGoodsState, String userName, String doWhat, String remark) {
        this.backGoodsId = backGoods.getId();
        this.userName = userName;
        this.doWhat = doWhat;
        this.remark = remark;
        this.backState = backGoodsState;
        this.operaTime = DateTime.now();
    }

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

    @Column(name = "backGoodsId")
    @Basic
    public Integer getBackGoodsId() {
        return backGoodsId;
    }

    public void setBackGoodsId(Integer backGoodsId) {
        this.backGoodsId = backGoodsId;
    }

    @Column(name = "userName")
    @Basic
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "doWhat")
    @Basic
    public String getDoWhat() {
        return doWhat;
    }

    public void setDoWhat(String doWhat) {
        this.doWhat = doWhat;
    }

    /**
     * 操作说明(类似于客服备注)
     */
    @Column(name = "remark")
    @Basic
    public String getRemark() {
        return remark;
    }

    /**
     * 操作说明(类似于客服备注)
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "operaTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getOperaTime() {
        return operaTime;
    }


    public void setOperaTime(DateTime operaTime) {
        this.operaTime = operaTime;
    }

    @Column(name = "backState")
    @Enumerated(EnumType.STRING)
    public BackGoodsState getBackState() {
        return backState;
    }

    public void setBackState(BackGoodsState backState) {
        this.backState = backState;
    }

}