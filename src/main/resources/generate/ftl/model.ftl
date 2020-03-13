package ${modelPackage};


<#list columnTypes as list>
	<#if list == 'BigDecimal'>
import java.math.BigDecimal;
	</#if>
</#list>
${import}
import lombok.Data;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/** 
*@Title ${tableName}.java 
*@description:  ${tableName}
*@author lihaisteward
**/
@Data
<#if tableApi??>
${tableApi}
</#if>
public class ${tableName} extends Model<${tableName}>{
	${property}
}
