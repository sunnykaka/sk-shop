package cmscenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 推送，设备token
 *
 * Created by Zhb on 2015/9/23.
 */
@Entity
@Table(name = "devicetoken")
public class DeviceToken implements EntityClass<Integer> {

    public static final int BADGE_DEFAULT_INT = 1;

    private Integer id;

    private String token;

    private int badge;

    public DeviceToken(){}

    public DeviceToken(String token,int badge){
        this.token = token;
        this.badge = badge;
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

    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(name = "badge")
    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }
}
