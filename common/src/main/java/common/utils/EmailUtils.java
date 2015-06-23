package common.utils;

import play.Play;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhb on 15-4-30.
 */
public class EmailUtils {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void sendEmail(String mailTo,String title,String template) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
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
        });

    }

}
