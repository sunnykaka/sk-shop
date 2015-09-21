package services;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhb on 2015/9/21.
 */
@Service
public class IOSPushService {

    public void push(){

        String deviceToken = "d7e6132895b388cf016433167c9e2d97fe4b76ca5a1692209a3b6e3cb3fdcd9c";
        String alert = "我的push测试";//push的内容
        int badge = 100;//图标小红圈的数值
        String sound = "default";//铃音

        List<String> tokens = new ArrayList<>();
        tokens.add(deviceToken);
        String certificatePath = "D:/work/push.p12";
        String certificatePassword = "123456";//此处注意导出的证书密码不能为空因为空密码会报错
        boolean sendCount = true;

        try {
            PushNotificationPayload payLoad = new PushNotificationPayload();
            payLoad.addAlert(alert); // 消息内容
            payLoad.addBadge(badge); // iphone应用图标上小红圈上的数值
            if (!StringUtils.isBlank(sound)) {
                payLoad.addSound(sound);//铃音
            }
            PushNotificationManager pushManager = new PushNotificationManager();
            //true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, false));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            // 发送push消息
            if (sendCount) {
                Device device = new BasicDevice();
                device.setToken(tokens.get(0));
                PushedNotification notification = pushManager.sendNotification(device, payLoad, true);
                notifications.add(notification);
            } else {
                List<Device> device = new ArrayList<Device>();
                for (String token : tokens) {
                    device.add(new BasicDevice(token));
                }
                notifications = pushManager.sendNotifications(payLoad, device);
            }
            List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
            List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
            int failed = failedNotifications.size();
            int successful = successfulNotifications.size();
            pushManager.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
