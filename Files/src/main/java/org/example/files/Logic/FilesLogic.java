package org.example.files.Logic;

import com.google.gson.Gson;
import org.example.files.model.FileInPage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

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







}
