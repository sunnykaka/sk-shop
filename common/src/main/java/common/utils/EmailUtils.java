package common.utils;

import org.apache.commons.mail.HtmlEmail;

/**
 * Created by zhb on 15-4-30.
 */
public class EmailUtils {

    public static void sendEmail(String mailTo,String title,String template) {
        try {
            HtmlEmail mail = createHtmlEmail();

            mail.addTo(mailTo);
            mail.setSubject(title);
            mail.setHtmlMsg(template);
            mail.send();
        }catch (Exception e){
            play.Logger.error("发送邮件失败",e);
        }

    }

    private static HtmlEmail createHtmlEmail(){

        HtmlEmail mail = new HtmlEmail();
        try {
            mail.setHostName("smtp.exmail.qq.com");
            mail.setSSLOnConnect(true);
            mail.setSslSmtpPort("465");
            mail.setAuthentication("service@yezaoshu.com","asd123");
            mail.setFrom("service@yezaoshu.com");
        }catch (Exception e){
            play.Logger.error("邮件初始化失败",e);
        }

        return mail;
    }

}
