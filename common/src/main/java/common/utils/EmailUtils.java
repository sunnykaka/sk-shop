package common.utils;

import play.Play;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;

/**
 * Created by zhb on 15-4-30.
 */
public class EmailUtils {

    public static void sendEmail(String mailTo,String title,String template) {
        try {
            Email email = new Email();

            email.setFrom(Play.application().configuration().getString("smtp.from", "service@yezaoshu.com"));
            email.addTo(mailTo);
            email.setSubject(title);
            email.setBodyHtml(template);
            MailerPlugin.send(email);
        }catch (Exception e){
            play.Logger.error("发送邮件失败",e);
        }

    }

}
