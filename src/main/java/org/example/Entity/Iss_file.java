package org.example.Entity;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Iss_file {
    String file_name;
    String file_path;
    String created_time;

    String repo_path;

    public String getRepo_path() {
        return repo_path;
    }

    public void setRepo_path(String repo_path) {
        this.repo_path = repo_path;
    }


    public String getFile_name(){
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public void setlast_modified_time() {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Path path = Paths.get(file_path);
        BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        BasicFileAttributes attr;
        try {
            attr = basicview.readAttributes();
            this.created_time = df.format(new Date(attr.lastModifiedTime().toMillis())); ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void path_to_name(){

        String[] temp = file_path.split("\\\\");
        this.file_name=temp[temp.length-1];

    }
}

