package com.thinkgem.jeesite.modules.api.model;

import com.thinkgem.jeesite.common.persistence.Page;

import java.io.Serializable;

/**
 * Created by zchuanzhao on 2016/10/16.
 */
public class RespModel<T> implements Serializable{

    //-2参数错误，-1操作失败，0操作成功，1成功刷新当前页，2成功并跳转到url，3成功并刷新iframe的父界面
    private String code;

    private String msg;

    private String url;

    private T data;

    private Page page;

    public RespModel(String code) {
        this.code = code;
        if("-2".equalsIgnoreCase(code)){
            setMsg("参数错误");
        }else if("-1".equalsIgnoreCase(code)){
            setMsg("操作失败");
        }else if("0".equalsIgnoreCase(code)){
            setMsg("操作成功");
        }
    }

    public RespModel(String code, Page page) {
        this.code = code;
        this.page = page;
    }

    public RespModel(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RespModel(String code, String msg, String url) {
        this.code = code;
        this.msg = msg;
        this.url = url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
