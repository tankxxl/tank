package com.thinkgem.jeesite.modules.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by rgz on 28/04/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Value {

    private Long id;
    private String quote;

    public Value() {

    }

    public Value(Long id, String quote) {
        this.id = id;
        this.quote = quote;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    @Override
    public String toString() {
        return "Value{" +
                "id=" + id +
                ", quote='" + quote + '\'' +
                '}';
    }
}
