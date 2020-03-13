package com.lhc.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lhc.utils.DateUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * TODO Mybatis-Plus 自动填充处理
 * @Author lihaisteward
 **/
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object createTime = getFieldValByName("createTime",metaObject);
        Object isDel = getFieldValByName("isDel",metaObject);
        if(createTime == null){
            setFieldValByName("createTime", DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss",String.class), metaObject);
        }
        if(isDel == null){
            setFieldValByName("isDel", 0, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
