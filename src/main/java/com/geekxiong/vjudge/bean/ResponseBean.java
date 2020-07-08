package com.geekxiong.vjudge.bean;

/**
 * @Description ResponseBean
 * @Author xiong
 * @Date 2020/02/27 19:48
 * @Version 1.0
 */
public class ResponseBean {
    private Object data;
    private String msg;
    private ResponseSate state;

    public static ResponseBean newResponse(){
        return new ResponseBean();
    }

    public static ResponseBean newResponse(Object data, String msg, ResponseSate state){
        ResponseBean bean = new ResponseBean();
        bean.setData(data);
        bean.setMsg(msg);
        if(state.equals(ResponseSate.SUCCESS)){
            bean.isSuccess();
        }else {
            bean.isFail();
        }
        return bean;
    }

    public void isSuccess(){
        this.state = ResponseSate.SUCCESS;
    }

    public void isFail(){
        this.state = ResponseSate.FAIL;
    }

    public enum ResponseSate {
        SUCCESS, FAIL
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResponseSate getState() {
        return state;
    }
}
