package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ObjectRepository<T>{
    File rootDir;
    ObjectMapper objectMapper = new ObjectMapper();
    Class<T> type;

    public ObjectRepository(File rootDir, Class<T> type){
        this.rootDir = rootDir;
        this.rootDir.mkdirs();
        this.type = type;
    }

    boolean save(File f, T value){
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(f, value);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean save(String filename, T value){
        File file = new File(this.rootDir, filename);
        return save(file, value);
    }

    T load(File file){
        try {
            FileInputStream is = new FileInputStream(file);
            return objectMapper.readValue(is, this.type);
        } catch (IOException e) {
            return null;
        }
    }

    public T load(String filename){
        File file = new File(this.rootDir, filename);
        return load(file);
    }

    public void delete(String filename){
        File file = new File(this.rootDir, filename);
        file.delete();
    }
}
