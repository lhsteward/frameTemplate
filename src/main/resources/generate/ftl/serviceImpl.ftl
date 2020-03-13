package ${serviceImplPackage};

import ${modelPackage}.${tableName};
import ${daoPackage}.${tableName}Mapper;
import ${servicePackage}.${tableName}Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


/** 
*@Title ${tableName}ServiceImpl.java
*@description:  ${tableName}ServiceImpl
*@author lihaisteward
**/
@Service
public class ${tableName}ServiceImpl extends ServiceImpl<${tableName}Mapper, ${tableName}> implements ${tableName}Service{

}
