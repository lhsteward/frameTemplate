package com.lhc.exception;


import com.lhc.common.Code;

/**
 * @author lihaichao
 * @ClassName GlobalException
 * @description: 自定义异常
 * @time 2018/11/2818:00
 **/
public class GlobalException extends RuntimeException {

    /**
     * 异常状态码
     */
    private Integer code;

    /**
     * 异常信息
     */
    private String message;

    private String e;

    public GlobalException(Code enums){
        super();
        this.code=enums.getCode();
        this.message=enums.getMsg();
    }

    public GlobalException(Code enums, String e){
        super();
        this.code=enums.getCode();
        this.message=enums.getMsg();
        this.e = e;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }
}
