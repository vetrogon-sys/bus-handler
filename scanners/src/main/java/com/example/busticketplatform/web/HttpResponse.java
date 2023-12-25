package com.example.busticketplatform.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.http.HttpStatusCode;

@Data
@AllArgsConstructor
public class HttpResponse {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private HttpStatusCode status;

    private String body;

    @SneakyThrows
    public JsonNode json() {
        return JSON_MAPPER.readTree(this.body);
    }

    public Document html() {
        return Jsoup.parse(body, "", Parser.xmlParser());
    }

    public String string() {
        return body;
    }

    public static HttpResponse empty() {
        return new HttpResponse(HttpStatusCode.valueOf(404), "Error response");
    }

}
