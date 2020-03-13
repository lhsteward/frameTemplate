package com.lhc.common;

/**
 * @author lihaichao
 * @ClassName Code
 * @description: 自定义提示枚举类
 * @time 2018/11/28 18:53
 **/
public enum Code {


    /**
     * @Description:  请求成功状态码   code : 1
     **/
    SUCCESS(1,"请求成功"),

    /**
     * @Description:  请求失败状态码   code : -1
     **/
    EXCEPTION_ERROR(-1, "异常错误"),
    OPERATE_DATA_ERROR(-1, "操作数据失败"),
    MISS_REQUIRED_PARAMETER(-1, "缺少请求参数"),
    ERROR(-1,"请求失败");


    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    Code(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

}
