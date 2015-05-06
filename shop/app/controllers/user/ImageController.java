package controllers.user;

import common.utils.JsonResult;
import org.apache.commons.lang3.RandomStringUtils;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * 图片验证码生成器
 * Created by zhb on 15-5-5.
 */

@org.springframework.stereotype.Controller
public class ImageController extends Controller {

    public static final String SESSION_KEY_IMAGECODE = "imageCode";

    private static int WIDTH = 100;
    private static int HEIGHT = 40;
    private static int NUM = 4;

    private static int RED = 240;
    private static int GREEN = 238;
    private static int BLUE = 229;

    /**
     * 生成图片验证码
     */
    public Result randomImageCode() {

        BufferedImage image = randomImage();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "JPG", bos);
            return ok(bos.toByteArray());
        } catch (Exception e) {
            return ok();
        }
    }


    /**
     * 效验图片验证码
     *
     * @param imageCode
     */
    public Result checkImageCode(String imageCode) {
        Object imageCodeInSession = Http.Context.current().session().get(SESSION_KEY_IMAGECODE);
        if (null == imageCode || null == imageCodeInSession || !imageCode.equals(imageCodeInSession.toString())) {
            return ok(new JsonResult(false, "验证码错误").toNode());
        } else {
            return ok(new JsonResult(true).toNode());
        }
    }

    private BufferedImage randomImage() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();
        g.setColor(new Color(RED, GREEN, BLUE));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        String code = RandomStringUtils.randomNumeric(NUM);
        for (int i = 0; i < NUM; i++) {
            g.setColor(new Color(0, 0, 0));
            g.setFont(new Font(Integer.valueOf(Font.ITALIC).toString(), Font.ITALIC, HEIGHT + 10));
            g.drawString(code.substring(i, i + 1), (((i * WIDTH) / NUM) * 90) / 100, HEIGHT);
        }
        Http.Context.current().session().put(SESSION_KEY_IMAGECODE, code);

        return image;
    }
}
