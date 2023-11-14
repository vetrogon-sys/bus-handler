package com.example.busticketplatform.serialize;

import com.example.busticketplatform.scunners.SiteCrawler;
import com.example.busticketplatform.scunners.Task;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class KVTaskSerializer implements TaskSerializer {
    private static final Logger log = getLogger(KVTaskSerializer.class);

    @Override
    public void storeTasks(Map<String, Task> tasks, Source source, SiteCrawler crawler) {
        String fileName = getFileNameForSourceAndCrawler(source, crawler);
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tasks);
            oos.close();
            log.info("Stored kv: {} tasks", tasks.size());
        } catch (IOException e) {
            log.error("Error via store tasks");
        }
    }

    @Override
    public Map<String, Task> readTasks(Source source, SiteCrawler crawler) {
        Map<String, Task> taskMap = new HashMap<>();
        String fileName = getFileNameForSourceAndCrawler(source, crawler);
        log.info("Start read kv {}", fileName);
        try {
            FileInputStream fin = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fin);
            taskMap = new HashMap<>((Map<String, Task>) ois.readObject());
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error via read tasks");
        }
        log.info("Restored {} tasks", taskMap.size());
        return taskMap;
    }

    private String getFileNameForSourceAndCrawler(Source source, SiteCrawler crawler) {
        return source.toString() + "\\" + crawler.getStringName();
    }

}
