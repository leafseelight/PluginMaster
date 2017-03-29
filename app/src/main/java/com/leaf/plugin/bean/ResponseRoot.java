package com.leaf.plugin.bean;

/**
 * Desc:  <br/>
 * Author: YJG <br/>
 * Email: ye.jg@outlook.com <br/>
 * Date: 2017/3/28 0028 <br/>
 */
public class ResponseRoot extends BaseBean {

    private String message;

    private String code;

    private String data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
