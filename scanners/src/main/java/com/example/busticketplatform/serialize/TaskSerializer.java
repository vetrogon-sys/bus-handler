package com.example.busticketplatform.serialize;

import com.example.busticketplatform.scunners.model.entity.Task;

import java.util.Map;

public interface TaskSerializer {

    void storeTasks(Map<String, Task> tasks, Source source);

    Map<String, Task> readTasks(Source source);

}
