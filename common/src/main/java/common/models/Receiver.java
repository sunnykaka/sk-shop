package common.models;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

/**
 * User: liubin
 * Date: 14-3-28
 */
@Embeddable
public class Receiver implements Cloneable {

    private String receiverName;

    private String receiverPhone;

    private String receiverMobile;

    private String receiverZip;

    private String receiverState;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;



    @javax.persistence.Column(name = "receiver_name")
    @Basic
    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }


    @javax.persistence.Column(name = "receiver_phone")
    @Basic
    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }


    @javax.persistence.Column(name = "receiver_mobile")
    @Basic
    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }


    @javax.persistence.Column(name = "receiver_zip")
    @Basic
    public String getReceiverZip() {
        return receiverZip;
    }

    public void setReceiverZip(String receiverZip) {
        this.receiverZip = receiverZip;
    }


    @javax.persistence.Column(name = "receiver_state")
    @Basic
    public String getReceiverState() {
        return receiverState;
    }

    public void setReceiverState(String receiverState) {
        this.receiverState = receiverState;
    }


    @javax.persistence.Column(name = "receiver_city")
    @Basic
    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }


    @javax.persistence.Column(name = "receiver_district")
    @Basic
    public String getReceiverDistrict() {
        return receiverDistrict;
    }

    public void setReceiverDistrict(String receiverDistrict) {
        this.receiverDistrict = receiverDistrict;
    }


    @javax.persistence.Column(name = "receiver_address")
    @Basic
    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    @Override
    public Receiver clone() {
        Receiver receiver = new Receiver();
        receiver.setReceiverName(this.getReceiverName());
        receiver.setReceiverAddress(this.getReceiverAddress());
        receiver.setReceiverCity(this.getReceiverCity());
        receiver.setReceiverDistrict(this.getReceiverDistrict());
        receiver.setReceiverMobile(this.getReceiverMobile());
        receiver.setReceiverPhone(this.getReceiverPhone());
        receiver.setReceiverState(this.getReceiverState());
        receiver.setReceiverZip(this.getReceiverZip());

        return receiver;
    }

}
