package org.example.files.Controller;

import org.example.files.Logic.Images.Compressor;
import org.example.files.Logic.Images.ScanTest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/files/images")
public class ImageController {

    @Value("${loadpath}")
    private String loadpath;

    @Value("${domen}")
    private String domen;


    @PostMapping("/dpichange")
    public ResponseEntity<String> dpichange(@RequestBody String json){
        System.out.println(json);
        try {
            JSONObject obj = new JSONObject(json);
            String filename = obj.getString("filename");
            String additipnalPath = obj.optString("additionalPath","");
            String nickname = obj.getString("nickname");
            File file = new File(loadpath+"\\"+nickname+"\\"+additipnalPath,filename);
            Integer dpi = obj.optInt("dpi",400);
            BufferedImage image = ImageIO.read(new FileInputStream(file));
            ScanTest.saveGridImage(file,image,dpi);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/compress")
    public ResponseEntity<String> compress(@RequestBody String json){
        try {
            JSONObject obj = new JSONObject(json);
            String filename = obj.getString("filename");
            String additipnalPath = obj.optString("additionalPath","");
            String nickname = obj.getString("nickname");
            File file = new File(loadpath+"\\"+nickname+"\\"+additipnalPath,filename);
            if(filename.contains("tiff") ){
                String compressType = obj.getString("compress");
                Compressor.convertToTiff(file,file,compressType);
            }else if(filename.contains("jpeg") ){
                String compressing = obj.getString("compress");
                Float compress_power=1 - Float.parseFloat(compressing.replace("%",""))/100;
                Compressor.compress(file.getAbsolutePath(),compress_power);
            }
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
