package com.lhc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lihaisteward
 * @ClassName Result
 * @description:  RestFul API 方法返回值格式统一实体类
 **/
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 694858559908048578L;
    private Integer code;
    private String msg;
    private Integer count = 0;
    private T data;

    public Result(){}

    public Result(Integer code, String msg, Integer count, T data) {
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.data = data;
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    
    /**
     * @Title: success
     * @Description: 成功  (无参)  默认 code : " 0 "  msg : "请求成功" , count : 0 , data: null
     **/
    public Result success(){
        return success((T) null);
    }

    /**
     * @Title: success
     * @Description:   成功   默认 code : " 1 "  msg : "请求成功"
     * @param count : 数据条数
     * @param data :  数据
     **/
    public Result success(Integer count,T data){
        return new Result(1,"请求成功!",count,data);
    }

    /**
     * @Title: success
     * @Description:  成功   默认 code : " 1 "
     * @param msg :  提示信息
     * @param count :  数据条数
     * @param data :   数据
     **/
    public Result success(String msg,Integer count,T data){
        return new Result(1,msg,count,data);
    }

    /**
     * @Title: success
     * @Description:  成功   默认 code : " 1 " , msg : "请求成功"
     * @param data :  数据
     **/
    public Result success(T data){
        return new Result(1,"请求成功!",data);
    }

    /**
     * @Title: success
     * @Description:  成功   默认 code : " 1 "
     * @param msg :  提示信息
     * @param data :  数据
     **/
    public Result success(String msg,T data){
        return new Result(1,msg,data);
    }

    /**
     * @Title: success
     * @Description:  成功   默认 code : " 1 "
     * @param code :  枚举类代码
     * @param data :  数据
     **/
    public Result success(Code code,T data){
        return new Result(code.getCode(),code.getMsg(),data);
    }

    /**
     * @Title: success
     * @Description:  成功   默认 code : " 1 "
     * @param code :  枚举类代码
     **/
    public Result success(Code code){
        return new Result(code.getCode(),code.getMsg(),null);
    }

    
    /**
     * @Title: error
     * @Description:  错误   默认 data : null
     * @param code : 错误代码
     * @param msg : 错误信息
     **/
    public Result error(Integer code,String msg){
        return new Result(code,msg,null);
    }

    /**
     * @Title: error
     * @Description:  错误   默认 data : null
     * @param code :  枚举类错误代码
     **/
    public Result error(Code code){
        return new Result(code.getCode(),code.getMsg(),null);
    }

    public Result error(T data){
        return new Result(-1,"请求失败!",data);
    }

    public Result error(){
        return error((T)null);
    }

}
