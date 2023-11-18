package com.example.busticketplatform.scunners.crawler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ExcecTester {

    static AtomicLong lastM = new AtomicLong(0);

    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        Runnable task = getRunnable(executorService);

        executorService.scheduleWithFixedDelay(task, 0L, 2L, TimeUnit.SECONDS);
    }

    private static Runnable getRunnable(ScheduledExecutorService executorService) {
        Runnable task = () -> {
            URL url = null;
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080"))
                      .header("accept", "application/json")
//                      .header("host", "localhost:8080")
                      .build();
                HttpResponse<String> response = client.send(request,  HttpResponse.BodyHandlers.ofString());

                String responseMessage = response.body();
                if (responseMessage != null && !"0".equals(responseMessage)) {
                    lastM.set(System.currentTimeMillis());
                    System.out.println(responseMessage);
                } else {
                    if (lastM.get() + TimeUnit.SECONDS.toMillis(15) < System.currentTimeMillis()) {
                        System.out.println("Shutdown executor");
                        executorService.shutdown();
                    }
                }
            } catch (IOException ignored) {
                System.err.println(ignored.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        return task;
    }

}
