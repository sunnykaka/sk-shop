package ordercenter.models;


import common.models.Receiver;
import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * User: liubin
 * Date: 14-3-28
 */
@Table(name = "invoice")
@Entity
public class Invoice implements EntityClass<Integer> {

    private Integer id;

    private Receiver receiver;

    private String shippingNo;

    private String shippingComp;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @Column(name = "shipping_no")
    @Basic
    public String getShippingNo() {
        return shippingNo;
    }

    public void setShippingNo(String shippingNo) {
        this.shippingNo = shippingNo;
    }


    @Column(name = "shipping_comp")
    @Basic
    public String getShippingComp() {
        return shippingComp;
    }

    public void setShippingComp(String shippingComp) {
        this.shippingComp = shippingComp;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

}
