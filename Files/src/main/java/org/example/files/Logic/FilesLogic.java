package org.example.files.Logic;

import com.google.gson.Gson;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriteParam;
import org.example.files.model.FileInPage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class FilesLogic {
    @Value("${loadpath}")
    private String startFileSystem;

    private boolean createDir(File dir){
        if(!dir.exists()){
            dir.mkdir();
            return true;
        }
        return false;
    }
    private static String cache="C:/Users/nazar/OneDrive/Desktop/Projects/.cache";
    public static void clearCache(){
        File[] files= new File(cache).listFiles();
        for (int i = 0; i < files.length; i++) {
            System.out.println( files[i].delete());
        }
    }
    public boolean createProject(String projectName, String group_name){
        createDir(new File(startFileSystem));
        if(createDir(new File(startFileSystem,group_name))){
            if(projectName.equals("")){
                return true;
            }else{
                boolean b =  createDir(new File(new File(startFileSystem,group_name),projectName));
                return b;
            }
        }else{
            return false;
        }
    }

    public JSONObject getFiles(String path) throws FileNotFoundException {
        JSONObject answer = new JSONObject();
        JSONArray array = new JSONArray();
        File dir = new File(startFileSystem,path);
        if(!dir.exists()){
            dir.mkdir();
        }
        File[] files = dir.listFiles();
        if(dir.exists() && dir.isDirectory()){
            for (int i = 0; i < files.length; i++) {
                FileInPage file = new FileInPage(files[i]);
                array.put(file.toJSON());
            }
        }
        answer.put("files", array);
        return answer;
    }

    public boolean createFolder(String projectName, String group_name){
        File file = new File(startFileSystem+"/"+group_name+"/"+projectName);
        boolean b =  createDir(file);
        return b;
    }

    public static void FileSave(MultipartFile file, File userFolder) throws IOException, InterruptedException, ExecutionException {
        Files.copy(file.getInputStream(), userFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        if(file.getOriginalFilename().endsWith(".tif")||file.getOriginalFilename().endsWith(".tiff")){
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "magick",
                    userFolder.getAbsolutePath(),
                    "-compress", "Zip",
                    "output.tif"
            );
            processBuilder.directory(userFolder.getParentFile());
            processBuilder.redirectErrorStream(true);

            try {
                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                int exitCode = process.waitFor();
                System.out.println("Process exited with code: " + exitCode);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }





}
