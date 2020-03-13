package com.lhc.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.lhc.common.Code;
import com.lhc.common.Result;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;


/**
 * @author lihaisteward
 * @ClassName ResultUtils
 * @description:
 **/
public class ResultUtils {

    /**
     * @Title: success
     * @Description: 无参成功返回   默认值  code : "1" , msg : "请求成功" , count : 1 , data : null
     **/
    public static Result success(){
        return success(Code.SUCCESS);
    }


    public static Result success(Object object){
        return success(0,object);
    }


    /**
     * @Title: success
     * @Description:  有参成功返回   默认值  code : "1" , msg : "请求成功"
     * @param count :  数据条数
     * @param object : 数据
     **/
    public static Result success(Integer count,Object object){
        return new Result().success(count,object);
    }

    /**
     * @Title: success
     * @Description:  有参成功返回   默认值  code : "1"
     * @param msg : 提示信息
     * @param count :  数据条数
     * @param object :  数据
     **/
    public static Result success(String msg,Integer count,Object object){
        return new Result().success(msg,count,object);
    }

    /**
     * @Title: error
     * @Description: 有参成功返回     默认值  code : "1"
     * @param code :
     * @param object : 数据
     **/
    public static Result success(Code code, Object object){
        return new Result().success(code,object);
    }

    /**
     * @Title: error
     * @Description: 有参成功返回     默认值  code : "1" data : null
     * @param code : 枚举类代码
     **/
    public static Result success(Code code){
        return new Result().success(code);
    }


    public static Result getDataForLimit(Integer total, List<?>  t){
        return success(total,t);
    }

    /**
     * @Title: getDataForLimit
     * @Description: PageHelper分页
     * @param page : 分页参数
     **/
    public static <T> Result getDataForLimit(Page<T> page){
        int count = 0;
        if((int)page.getTotal() == 0 && page.getRecords().size() > 0){
            count = page.getRecords().size();
        }else if((int)page.getTotal() > 0 && page.getRecords().size() > 0){
            count = (int)page.getTotal();
        }
        return success(count,page.getRecords());
    }

    public static Result error(){
        return error(Code.EXCEPTION_ERROR);
    }

    /**
     * @Title: error
     * @Description: 错误返回格式     默认值 data : null
     * @param code : 错误代码
     * @param msg : 提示信息
     **/
    public static Result error(Integer code,String msg){
        return new Result().error(code,msg);
    }
    
    
    /**
     * @Title: error
     * @Description: 错误返回格式     默认值 data : null
     * @param code : 枚举类错误代码
     **/
    public static Result error(Code code){
        return new Result().error(code);
    }


    /**
     * @Title: returnOneData
     * @Description: 增删改 成功条数只能为1时 的判断和返回
     * @param result : 增删改 返回的 成功条数
     * @param object : 需要返回的数据
     **/
    public static Result returnOneData(int result,Object object){
        if(result != 1){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtils.error(Code.OPERATE_DATA_ERROR);
        }
        return new Result().success(object);
    }

    /**
     * @Title: returnMultiData
     * @Description: 增删改 成功条数非0 的判断和返回
     * @param result : 增删改 返回的 成功条数
     * @param object : 需要返回的数据
     **/
    public static Result returnMultiData(int result,Object object){
        if(result <= 0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtils.error(Code.OPERATE_DATA_ERROR);
        }
        return new Result().success(object);
    }

}