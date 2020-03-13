package ${controllerPackage};

import ${modelPackage}.${tableName};
import ${servicePackage}.${tableName}Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lhc.common.BaseController;

/** 
*@Title ${tableName}Controller.java
*@description:  ${tableName}Controller
*@author lihaisteward
**/
@RestController
@RequestMapping("${requestMapping}")
public class ${tableName}Controller extends BaseController<${tableName}, ${tableName}Service, Long>{

}
