package org.example.files.Controller;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import jakarta.ws.rs.NotFoundException;
import org.example.files.Logic.FilesLogic;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.example.files.Logic.Images.Compressor.convertToTiff;

@RestController
@RequestMapping("/api/files")
public class FileController {


    @Autowired
    private FilesLogic filesLogic;


    @Value("${loadpath}")
    private String loadpath;

    @Value("${domen}")
    private String domen;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("works");
        return ResponseEntity.ok("test");
    }


    private void tileimage(MultipartFile file) throws IOException {
        System.out.println(file.getInputStream().available());
        BufferedImage image = ImageIO.read(file.getInputStream());
        int width_title = image.getWidth()/20;
        int part = 1;
        int height_title = image.getHeight()/20;
        for (int i = 0; i < image.getWidth(); i+=width_title) {
            for (int j = 0; j < image.getHeight(); j+=height_title) {
                try {
                    BufferedImage subimage = image.getSubimage(i,j,width_title,height_title);
                    String fileName = UUID.randomUUID() + "_"+part+"_" + file.getOriginalFilename();
                    File output = new File(Paths.get(loadpath, fileName).toFile().getAbsolutePath());
                    if(file.getOriginalFilename().endsWith(".jpg")) {
                        ImageIO.write(subimage, "jpg", output);
                    }else if(file.getOriginalFilename().endsWith(".tif")){
                        ImageIO.write(subimage, "tif", output);
                        convertToTiff(output,output,"LZW");
                    }else{
                        ImageIO.write(subimage, "jpeg", output);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,@RequestParam("namefolder") String namefolder) throws IOException, ExecutionException, InterruptedException {
        Path filePath = Paths.get(loadpath+"\\"+namefolder, file.getOriginalFilename());

        //Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        filesLogic.FileSave(file, filePath.toFile());
        // Возвращаем URL загруженного файла
        String fileUrl = file.getOriginalFilename();
        JSONObject answer = new JSONObject();
        answer.put("url", fileUrl);
        return ResponseEntity.ok(answer.toString(2));
    }
    @PostMapping("/createFolder")
    public ResponseEntity<String> createFolder(@RequestBody String json) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            filesLogic.createFolder(jsonObj.getString("folderName"),
                    jsonObj.getString("nickname"));
            return ResponseEntity.ok("success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("");
        }
    }
    private JSONObject getRole(HttpHeaders httpHeaders){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                domen+"/validate",
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                String.class
        );
        if(response.getStatusCode().is2xxSuccessful()){
            JSONObject person = new JSONObject().put("nickname",new JSONObject(response.getBody()).get("nickname"));
            return person;
        }else{
            throw new NotFoundException();
        }
    }

    @GetMapping("/viewFs")
    public ResponseEntity<String> viewFs(@RequestHeader HttpHeaders headers) {
        ResponseEntity<String> response = null;
        try {
            String nickname = getRole(headers).getString("nickname");
            JSONObject files = filesLogic.getFiles(nickname+"");
            System.out.println(files);
            response = ResponseEntity.ok(files.toString(2));
        }catch (Exception e){
            response = ResponseEntity.badRequest().body("");
        }
        return response;
    }

}
