package com.thinkgem.jeesite.modules.crm.client.entity;

/**
 * Created by rgz on 10/05/2017.
 */
public class MyClient {

    public MyClient() {
    }

    public MyClient(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
}
