package com.example.busticketplatform.web.link;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Parameter {

    final String name;

    final String value;

    @Override
    public String toString() {
        return name + '=' + value;
    }
}
