package common.utils;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Play;

import com.esms.*;
import com.esms.common.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Created by liubin on 15-5-7.
 */
public class SmsUtils {

    public static boolean sendSms(String[] phones, String content) {
        Preconditions.checkArgument(phones != null && phones.length > 0, "短信未发送, 手机号码不能为空");
        for(String phone : phones) {
            Preconditions.checkArgument(RegExpUtils.isPhone(phone), String.format("短信未发送, 手机号码[%s]不正确", phones));
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(content), "短信未发送, 内容不能为空");

        boolean mock = Play.application().configuration().getBoolean("sms.mock");
        if(mock) {
            play.Logger.info(String.format("mock环境不发送真实短信 收信手机:[%s], 短信内容:[%s]", Arrays.toString(phones), content));

        } else {
            if(play.Logger.isDebugEnabled()) {
                play.Logger.debug(String.format("发送短信, 收信手机:[%s], 短信内容:[%s]", Arrays.toString(phones), content));
            }

            try {
                //doSendSms(phones, content);
            } catch (Exception e) {
                Logger.error("短信发送失败", e);
                return false;
            }

        }

        return true;
    }

    public static boolean sendSms(String phone, String content) {
        return sendSms(new String[]{phone}, content);
    }

    private static void doSendSms(String[] phones, String content) throws Exception{

        String user = Play.application().configuration().getString("sms.user");
        String password = Play.application().configuration().getString("sms.password");
        Account ac = new Account(user, password);
        PostMsg pm = new PostMsg();
        String cmHost = Play.application().configuration().getString("sms.cm.host");
        int cmPort = Play.application().configuration().getInt("sms.cm.port");
        String wsHost = Play.application().configuration().getString("sms.ws.host");
        int wsPort = Play.application().configuration().getInt("sms.ws.port");

        pm.getCmHost().setHost(cmHost, cmPort);//设置网关的IP和port，用于发送信息
        pm.getWsHost().setHost(wsHost, wsPort);//设置网关的 IP和port，用于获取账号信息、上行、状态报告等等

        List<MessageData> messageDataList = new ArrayList<>();

        for(String phone : phones) {
            messageDataList.add(new MessageData(phone, content));
        }

        MTPack pack = new MTPack();
        pack.setBatchID(UUID.randomUUID());
        pack.setBatchName("短信测试批次");
        pack.setMsgType(MTPack.MsgType.SMS);
        pack.setBizType(0);
        pack.setDistinctFlag(false);
        pack.setSendType(MTPack.SendType.MASS);
        pack.setMsgs(messageDataList);

        GsmsResponse resp = pm.post(ac, pack);

        if(Logger.isDebugEnabled()) {
            Logger.debug("短信发送结果: " + resp);
        }

    }

//import java.io.IOException;
//import java.net.ConnectException;
//import java.net.URLDecoder;
//import java.util.ArrayList;
//import java.util.UUID;
//
//import com.esms.*;
//import org.junit.Test;
//
//import com.esms.common.entity.*;
//import com.esms.common.util.MediaUtil;
//
//public class PostMsgTest {
//
//    @Test
//    public void test(){
//        try {
//            extend(); //扩展接口范例
////			compatibility(); //兼容接口范例
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //扩展接口范例
//    public static void extend() throws Exception{
//        Account ac = new Account("zgt21@zgt1", "fj39sl20@");//
//        PostMsg pm = new PostMsg();
//        pm.getCmHost().setHost("211.147.239.62", 9080);//设置网关的IP和port，用于发送信息
//        pm.getWsHost().setHost("211.147.239.62", 9070);//设置网关的 IP和port，用于获取账号信息、上行、状态报告等等
//
////		/**代理上网设置,如果需要*/
////		HostInfo proxyHost = new HostInfo("192.168.0.47", 1080);
////		proxyHost.setType(HostInfo.ConnectionType.SOCKET4);  	//设置连接类型
////		proxyHost.setUsername("004");						//设置用户名
////		proxyHost.setPassword("123");							//设置密码
////		pm.getProxy().setProxy(proxyHost);					//设置代理
//
//        doSendSms(pm, ac); //短信下行
////		doSendMms(pm, ac); //彩信下行
//
////		doGetAccountInfo(pm, ac); //获取账号信息
////		doModifyPwd(pm, ac); //修改密码
//
////		doFindResps(pm, ac); //查询提交报告
////		doFindReports(pm, ac); //查询状态报告
//
////		doGetMos(pm, ac); //获取上行信息
////		doGetResps(pm, ac); //获取提交报告
////		doGetReports(pm, ac); //获取状态报告
//    }
//
//    /**
//     * 短信下发范例
//     * @param pm
//     * @param ac
//     */
//    public static void doSendSms(PostMsg pm, Account ac) throws Exception{
//        MTPack pack = new MTPack();
//        pack.setBatchID(UUID.randomUUID());
//        pack.setBatchName("短信测试批次");
//        pack.setMsgType(MTPack.MsgType.SMS);
//        pack.setBizType(0);
//        pack.setDistinctFlag(false);
//        ArrayList<MessageData> msgs = new ArrayList<MessageData>();
//
////		/** 单发，一号码一内容 */
////		msgs.add(new MessageData("18682000593", "短信单发测试"));
//		pack.setMsgs(msgs);
//
//        /** 群发，多号码一内容 */
//        pack.setSendType(MTPack.SendType.MASS);
//        String content = "我好想射点什么";
//        msgs.add(new MessageData("18682000593", content));
//        msgs.add(new MessageData("18948326080", content));
//        msgs.add(new MessageData("15886409226", content));
////        pack.setMsgs(msgs);
//
////		/** 组发，多号码多内容 */
////		pack.setSendType(SendType.GROUP);
////		msgs.add(new MessageData("13430258111", "短信组发测试111"));
////		msgs.add(new MessageData("13430258222", "短信组发测试222"));
////		msgs.add(new MessageData("13430258333", "短信组发测试333"));
////		pack.setMsgs(msgs);
//
//        GsmsResponse resp = pm.post(ac, pack);
//        System.out.println(resp);
//    }
//
//    /**
//     * 彩信下发范例
//     * @param pm
//     * @param ac
//     */
//    public static void doSendMms(PostMsg pm, Account ac) throws Exception{
//        MTPack pack = new MTPack();
//        pack.setBatchID(UUID.randomUUID());
//        pack.setBatchName("彩信测试批次");
//        pack.setMsgType(MTPack.MsgType.MMS);
//        pack.setBizType(1);
//        pack.setDistinctFlag(false);
//        ArrayList<MessageData> msgs = new ArrayList<MessageData>();
//
//        String path = PostMsgTest.class.getClassLoader().getResource("mms_test").getPath();
//        path = URLDecoder.decode(path, "utf-8");
//
//        //设置公共彩信资源
//        pack.setMedias(MediaUtil.getMediasFromFolder(path));
//
////		/** 单发，一号码一内容 */
////		msgs.add(new MessageData("13430258111", null));
////		pack.setMsgs(msgs);
//
//        /** 群发，多号码一内容 */
//        pack.setSendType(MTPack.SendType.MASS);
//        msgs.add(new MessageData("13430258111", null));
//        msgs.add(new MessageData("13430258222", null));
//        msgs.add(new MessageData("13430258333", null));
//        pack.setMsgs(msgs);
//
////		/** 组发，多号码多内容 */
////		pack.setSendType(MTPack.SendType.GROUP);
////		//设置私有彩信资源
////		MessageData msg1 = new MessageData("13430258111", null);
////		msg1.setMedias(MediaUtil.getMediasFromFolder("F:/mms_test/msg1"));
////		msgs.add(msg1);
////		MessageData msg2 = new MessageData("13430258222", null);
////		msg2.setMedias(MediaUtil.getMediasFromFolder("F:/mms_test/msg2"));
////		msgs.add(msg2);
////		MessageData msg3 = new MessageData("13430258333", null);
////		msg3.setMedias(MediaUtil.getMediasFromFolder("F:/mms_test/msg3"));
////		msgs.add(msg3);
////		pack.setMsgs(msgs);
//
//        GsmsResponse resp = pm.post(ac, pack);
//        System.out.println(resp);
//    }
//
//    /**
//     * 获取账号信息
//     * @param pm
//     * @param account
//     * @throws Exception
//     */
//    public static void doGetAccountInfo(PostMsg pm, Account ac) throws Exception{
//        System.out.println(pm.getAccountInfo(ac));   //获取账号详细信息
//
//        BusinessType[] bizTypes = pm.getBizTypes(ac); //获取账号绑定业务类型
//        if(bizTypes != null){
//            for(BusinessType bizType : bizTypes){
//                System.out.println(bizType);
//            }
//        }
//    }
//
//    /**
//     * 获取上行信息
//     * @param pm
//     * @param account
//     * @throws Exception
//     */
//    public static void doGetMos(PostMsg pm, Account ac) throws Exception{
//        MOMsg[] mos = pm.getMOMsgs(ac, 100);
//        if(mos != null){
//            for(MOMsg mo : mos){
//                System.out.println(mo);
//            }
//        }
//    }
//
//    /**
//     * 查询提交报告
//     * @param pm
//     * @param ac
//     * @throws Exception
//     */
//    public static void doFindResps(PostMsg pm, Account ac) throws Exception{
//        UUID batchID = UUID.fromString("3e1f13f4-1677-41f1-b67d-702f2c01eafb"); //如果需要按批次ID来查询
//        MTResponse[] foundMtResps = pm.findResps(ac, 1, batchID, null, 0);
//        if(foundMtResps != null){
//            for(MTResponse resp : foundMtResps){
//                System.out.println(resp);
//            }
//        }
//    }
//
//    /**
//     * 获取提交报告
//     * @param pm
//     * @param ac
//     * @throws Exception
//     */
//    public static void doGetResps(PostMsg pm, Account ac) throws Exception{
//        MTResponse[] mtResps = pm.getResps(ac, 100);
//        if(mtResps != null){
//            for(MTResponse resp : mtResps){
//                System.out.println(resp);
//            }
//        }
//    }
//
//    /**
//     * 查询状态报告
//     * @param pm
//     * @param ac
//     * @throws Exception
//     */
//    public static void doFindReports(PostMsg pm, Account ac) throws Exception {
//        UUID batchID = UUID.fromString("3e1f13f4-1677-41f1-b67d-702f2c01eafb"); //如果需要按批次ID来查询
//        MTReport[] foundMtReports = pm.findReports(ac, 1, batchID, null, 0);
//        if(foundMtReports != null){
//            for(MTReport report : foundMtReports){
//                System.out.println(report);
//            }
//        }
//    }
//
//    /**
//     * 获取状态报告
//     * @param pm
//     * @param ac
//     * @throws Exception
//     */
//    public static void doGetReports(PostMsg pm, Account ac) throws Exception{
//        MTReport[] mtReports = pm.getReports(ac, 100);
//        if(mtReports != null){
//            for(MTReport report : mtReports){
//                System.out.println(report);
//            }
//        }
//    }
//
//    /**
//     * 修改密码
//     * @param pm
//     * @param ac
//     * @throws Exception
//     */
//    public static void doModifyPwd(PostMsg pm, Account ac) throws Exception{
//        System.out.println(pm.modifyPassword(ac, "123456"));
//    }
//
//    //兼容接口范例
//    public static void compatibility() throws ConnectException{
//
//        //设置部分
//        PostMsg pm = new PostMsg("admin", "123456");		//创建实例时，需输入用户名与密码
//        pm.getCmHost().setHost("127.0.0.1", 8089);  	//设置网关的IP和port
//        pm.getWsHost().setHost("127.0.0.1", 8088);  //设置WebService的 IP和port
////		pm.getProxy().setProxy(ProxyServer.PROXY_TYPE_SOCKS4, "192.168.0.47", 1080); //设置代理
////
////
//        int resp = -1;
//        /** 单发，一号码一内容 */
//        resp = pm.post("13430258111", "短信单发测试", "");
////
////		/** 群发，多号码一内容 */
////		resp = pm.post(new String[]{"13430258111", "13430258222", "13430258333"}, "短信群发测试", "");
////
////		/** 组发，多号码多内容 */
////		MessageData[] msgs = new MessageData[3];
////		msgs[0] = new MessageData("13430258111", "短信组发测试111");
////		msgs[1] = new MessageData("13430258222", "短信组发测试222");
////		msgs[2] = new MessageData("13430258333", "短信组发测试333");
////		resp = pm.post(msgs, "");
//
//        System.out.println("响应：" + resp);
//
//        //修改密码
////		System.out.println(pm.modifyPassword("123456"));
//
////		System.out.println(pm.getConfigInfo()); 					//获取用户详细信息
////		MOMsg[] momsgs = pm.getMOMsg();								//获取上行信息
////		if(momsgs != null){
////			for(MOMsg momsg : momsgs){
////				System.out.println(momsg);
////			}
////		}
//    }
//}

}
