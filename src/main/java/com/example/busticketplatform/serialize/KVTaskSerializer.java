package com.example.busticketplatform.serialize;

import com.example.busticketplatform.scunners.Task;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class KVTaskSerializer implements TaskSerializer {
    private static final Logger log = getLogger(KVTaskSerializer.class);

    private final Map<String, Task> cache = new ConcurrentHashMap<>();

    @Override
    public void storeTasks(Map<String, Task> tasks, Source source) {
        String fileName = getFileNameForSourceAndCrawler(source, source.getCrawler().getName());
        String hashCodeFileName = getFileNameForSourceAndCrawler(source, source.name());
        String stringHash = String.valueOf(tasks.hashCode());
        String storedCache = getStoredCache(hashCodeFileName);
        if (!stringHash.equals(storedCache)) {
            try {
                storeObject(tasks, source, fileName);
                storeObject(stringHash, source, hashCodeFileName);
                cache.putAll(tasks);
            } catch (IOException e) {
                log.error("Error via store tasks");
                return;
            }
        }
        log.info("Stored kv: {} tasks, with hashCode {}", tasks.size(), tasks.hashCode());
    }

    private void storeObject(Object data, Source source, String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            createFileDirectory(source);
            file.setWritable(true);
        }
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
        outputStream.writeObject(data);
        outputStream.close();
    }

    private void createFileDirectory(Source source) throws IOException {
        File file = new File(getFileFolders(source));
        file.mkdirs();
        file.createNewFile();
        file.deleteOnExit();
    }

    private String getStoredCache(String fileName) {
        String storedHashCode = "";
        try {
            File file = new File(fileName);
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                storedHashCode = (String) ois.readObject();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException ignored) {
        }
        return storedHashCode;
    }

    @Override
    public Map<String, Task> readTasks(Source source) {
        Map<String, Task> taskMap = new HashMap<>();
        String fileName = getFileNameForSourceAndCrawler(source, source.getCrawler().getName());
        String storedCache = getStoredCache(getFileNameForSourceAndCrawler(source, source.name()));
        if (String.valueOf(cache.hashCode()).equals(storedCache)) {
            return cache;
        }
        log.info("Start read kv {}", fileName);
        try {
            File file = new File(fileName);
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                taskMap = new HashMap<>((Map<String, Task>) ois.readObject());
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error via read tasks");
        }
        log.info("Restored {} tasks", taskMap.size());
        cache.putAll(taskMap);
        return taskMap;
    }

    private String getFileNameForSourceAndCrawler(Source source, String name) {
        return getFileFolders(source) + "/" + getFileNameForString(name);
    }

    private String getFileFolders(Source source) {
        return "serialized/" + source.toString();
    }

    private String getFileNameForString(String name) {
        return name + ".kv";
    }

}
