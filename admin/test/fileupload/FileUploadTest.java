package fileupload;

import base.PrepareOrderData;
import common.utils.file.FileContentType;
import common.utils.file.FileUpload;
import common.utils.file.LocalFileUpload;
import org.junit.Test;
import play.test.WithApplication;

import java.io.File;
import java.util.Optional;

/**
 * @auth amos
 * 15-4-9.
 */
public class FileUploadTest extends WithApplication {

    @Test
    public void testCreateMutiDirectory(){
        File file = new File("/home/amos/test/test2");
        if(!file.exists()){
            file.mkdirs();
        }

        File file2 = new File("/home/amos/test/test3");
        if(!file2.exists()){
            file2.mkdirs();
        }
    }

    @Test
    public void testFormat(){
        String fileString = "/home/amos/%s/%s/%s";
        File file = new File(String.format(fileString,"2015","04","09"));
        if(!file.exists()){
            file.mkdirs();
        }
    }

    @Test
    public void testUpload(){
        File  file = new File("/home/amos/log/buy/buy.log");
        FileUpload upload = new LocalFileUpload();
        Optional<String> result  = upload.upload(file, FileContentType.Assert);
        System.out.println(result.get());
    }
}
