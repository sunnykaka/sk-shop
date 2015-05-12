package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.TableTimeData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hujie on 15-5-7.
 */
@Table(name = "valuation")
@Entity
public class Valuation implements EntityClass<Integer>,TableTimeData {

    private int id;

    private int userId;

    private String userName;

    private String content;

    private int orderItemId;

    private int productId;

    private DateTime orderCreateDate;

    private DateTime createDate;

    private DateTime updateDate;

    private int point;

    private int operatorId;

    private String operator;

    private String replyContent;

    private DateTime replyTime;

    /**
     * 追加评价
     */
    private String appendContent;

    /**
     * 追加时间
     */
    private DateTime appendDate;


    /**
     * 追加回复评价
     */
    private String appendReplyContent;

    /**
     * 追加回复评价的操作者
     */
    private String appendOperator;

    /**
     * 追加回复时间
     */
    private DateTime appendReplyDate;

    private static Map<Integer, String> pointName = new HashMap<>();

    static {
        pointName.put(0, "好评");
        pointName.put(1, "中评");
        pointName.put(2, "差评");
    }

    @GeneratedValue (strategy = GenerationType.AUTO)
    @Id
    @Override
    public Integer getId ( ) {
        return id;
    }

    @Override
    public void setId ( Integer id ) {
        this.id = id;
    }
    @Column(name = "userId")
    @Basic
    public int getUserId ( ) {
        return userId;
    }

    public void setUserId ( int userId ) {
        this.userId = userId;
    }

    @Column(name = "userName")
    @Basic
    public String getUserName ( ) {
        return userName;
    }

    public void setUserName ( String userName ) {
        this.userName = userName;
    }
    @Column(name = "content")
    @Basic
    public String getContent ( ) {
        return content;
    }

    public void setContent ( String content ) {
        this.content = content;
    }
    @Column(name = "orderItemId")
    @Basic
    public int getOrderItemId ( ) {
        return orderItemId;
    }

    public void setOrderItemId ( int orderItemId ) {
        this.orderItemId = orderItemId;
    }
    @Column(name = "productId")
    @Basic
    public int getProductId ( ) {
        return productId;
    }

    public void setProductId ( int productId ) {
        this.productId = productId;
    }
    @Column(name = "orderCreateDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Basic
    public DateTime getOrderCreateDate ( ) {
        return orderCreateDate;
    }

    public void setOrderCreateDate ( DateTime orderCreateDate ) {
        this.orderCreateDate = orderCreateDate;
    }
    @Column(name = "createDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Basic
    public DateTime getCreateDate ( ) {
        return createDate;
    }

    public void setCreateDate ( DateTime createDate ) {
        this.createDate = createDate;
    }
    @Column(name = "updateDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Basic
    public DateTime getUpdateDate ( ) {
        return updateDate;
    }

    public void setUpdateDate ( DateTime updateDate ) {
        this.updateDate = updateDate;
    }
    @Column(name = "point")
    @Basic
    public int getPoint ( ) {
        return point;
    }

    public void setPoint ( int point ) {
        this.point = point;
    }
    @Column(name = "operatorId")
    @Basic
    public int getOperatorId ( ) {
        return operatorId;
    }

    public void setOperatorId ( int operatorId ) {
        this.operatorId = operatorId;
    }
    @Column(name = "operator")
    @Basic
    public String getOperator ( ) {
        return operator;
    }

    public void setOperator ( String operator ) {
        this.operator = operator;
    }
    @Column(name = "replyContent")
    @Basic
    public String getReplyContent ( ) {
        return replyContent;
    }

    public void setReplyContent ( String replyContent ) {
        this.replyContent = replyContent;
    }
    @Column(name = "replyTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Basic
    public DateTime getReplyTime ( ) {
        return replyTime;
    }

    public void setReplyTime ( DateTime replyTime ) {
        this.replyTime = replyTime;
    }
    @Column(name = "appendContent")
    @Basic
    public String getAppendContent ( ) {
        return appendContent;
    }

    public void setAppendContent ( String appendContent ) {
        this.appendContent = appendContent;
    }
    @Column(name = "appendDate")
    @Type (type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Basic
    public DateTime getAppendDate ( ) {
        return appendDate;
    }

    public void setAppendDate ( DateTime appendDate ) {
        this.appendDate = appendDate;
    }
    @Column(name = "appendReplyContent")
    @Basic
    public String getAppendReplyContent ( ) {
        return appendReplyContent;
    }

    public void setAppendReplyContent ( String appendReplyContent ) {
        this.appendReplyContent = appendReplyContent;
    }
    @Column(name = "appendOperator")
    @Basic
    public String getAppendOperator ( ) {
        return appendOperator;
    }

    public void setAppendOperator ( String appendOperator ) {
        this.appendOperator = appendOperator;
    }
    @Column(name = "appendReplyDate")
    @Basic
    public DateTime getAppendReplyDate ( ) {
        return appendReplyDate;
    }

    public void setAppendReplyDate ( DateTime appendReplyDate ) {
        this.appendReplyDate = appendReplyDate;
    }
}
