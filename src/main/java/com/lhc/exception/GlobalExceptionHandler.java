package com.lhc.exception;

import com.lhc.common.Code;
import com.lhc.common.Result;
import com.lhc.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @ClassName GlobalExceptionHandler
 * @Description TODO 全局异常处理类
 * @Author lihaisteward
 * @Date 2019-10-11 16:08:29
 * @Version 1.0.0
 **/
@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({GlobalException.class, IllegalAccessException.class})
    public Result handlerGlobalException(GlobalException e) {
        log.error("\r\n异常信息 : \r\n[\r\n 错误代码 >>>>>>> { "+ e.getCode() +" } , 错误信息 >>>>>>> { "+e.getE()+" } \r\n]. \r\n");
        return ResultUtils.error(Code.EXCEPTION_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public  Result handleException(Exception e) {
        log.error("\r\n系统异常 >>>>>> { "+e.getMessage()+" } , \r\n错误信息 >>>>>>>\r\n { "+getExceptionAllinfo(e)+" } \r\n");
        return ResultUtils.error(-1,"执行异常.");
    }


    @ExceptionHandler(value = ConstraintViolationException.class)
    public Object ConstraintViolationExceptionHandler(ConstraintViolationException exception){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            if(i == 0){
                sb.append(violation.getMessageTemplate());
            }
            i++;
        }
        log.error("\r\n参数验证异常 >>>>>> 错误信息 : " + sb.toString() + "\r\n");
        return ResultUtils.error(-1,sb.toString());
    }

    /**
     * 验证异常
     * @param e
     * @return
     * @throws BindException
     */
    @ExceptionHandler(value = BindException.class)
    public Result handleBindException(BindException e) {
        // ex.getFieldError():随机返回一个对象属性的异常信息。如果要一次性返回所有对象属性异常信息，则调用ex.getAllErrors()
        FieldError fieldError = e.getFieldError();
        StringBuilder sb = new StringBuilder();
        sb.append(fieldError.getField()).append(" =[ ").append(fieldError.getRejectedValue()).append(" ] ")
                .append(fieldError.getDefaultMessage());
        log.error("\r\n参数验证异常 >>>>>> 错误信息 : " + sb.toString() + "\r\n");
        return ResultUtils.error(-1,sb.toString());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Object MethodArgumentNotValidHandler(MethodArgumentNotValidException exception){
        StringBuilder sb = new StringBuilder();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            sb.append(error.getField()).append(" =[ ").append(error.getRejectedValue()).append(" ] ")
                    .append(error.getDefaultMessage());
        }
        log.error("\r\n参数验证异常 >>>>>> 错误信息 : " + sb.toString() + "\r\n");
        return ResultUtils.error(-1,sb.toString());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public  Result handleException(HttpRequestMethodNotSupportedException e) {
        System.err.println("\n************************************************************************************");
        System.err.println("\n*     请求异常 : ---> [ { "+e.getMessage()+" } ] .                                 *");
        System.err.println("\n************************************************************************************\n");
        return ResultUtils.error(-1, e.getMessage());
    }


    public String getExceptionAllinfo(Exception ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        ex.printStackTrace(pout);
        String ret = new String(out.toByteArray());
        pout.close();
        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return ret;
    }
}
