package com.lhc.generateFile.methods;

import com.lhc.generateFile.utils.CamelCaseUtil;
import com.lhc.generateFile.utils.DBUtil;
import com.lhc.generateFile.utils.StringUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * 生成相关方法
 */
@Slf4j
public class Methods {

    private static Environment env;

    /**
     * 连接数据库 读取配置  生成文件
     */
    public static void generate() throws IOException {
        // 读取配置文件
        Properties properties = getConf(null);
        // 连接数据库
        Connection connection = DBUtil.getConnection(properties);
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        List<String> tables = new ArrayList<String>();
        if(connection != null){
            try {
                log.info("======================[ 正在读取表... ]=========================\n");
                DatabaseMetaData dm = connection.getMetaData();
                rs = dm.getTables(connection.getCatalog(), properties.getProperty("jdbc.username"), null, new String[]{"TABLE"});
                while(rs.next()) {
                    String tableName = StringUtils.upperCase(rs.getString("TABLE_NAME"));
                    tables.add(tableName);
                }
                System.out.println(tables.toString());
                for (String tableName : tables) {
                    log.info("======================[ 获取表{ "+tableName+" }, 正在读取列... ]=========================\n");
                    List<String> columnList = new ArrayList<>();
                    List<String> columnTypeList = new ArrayList<>();
                    List<Boolean> columnIsNullableList = new ArrayList<>();
                    ps = connection.prepareStatement("SELECT * FROM "+tableName);
                    rs = ps.executeQuery();
                    ResultSetMetaData meta = rs.getMetaData();
                    int columeCount = meta.getColumnCount();
                    for (int i = 1; i < columeCount + 1; i++) {
                        columnList.add(meta.getColumnName(i));
                        columnTypeList.add(sqlTypesToJava(meta.getColumnType(i)));
                        // 判断列是否可以接收NULL值 1 是 0 非
                        columnIsNullableList.add(meta.isNullable(i) == 0);
                    }
                    log.info("======================[ 正在读取列和注释... ]=========================\n");
                    List<String> commentList = new ArrayList<String>();
                    ps1 = connection.prepareStatement("SHOW FULL COLUMNS FROM "+tableName);
                    rs1 = ps1.executeQuery();
                    while (rs1.next()) {
                        commentList.add(rs1.getString("Comment"));
                    }
                    // 生成
                    String tablePrefix = properties.getProperty("tablePrefix");
                    String newTableName = CamelCaseUtil.underscoreToCamelCase(StringUtils.clearPrefix(tableName, tablePrefix));
                    log.info("======================[ 读取完成, 执行生成... ]=========================\n");
                    generateModel(properties, newTableName, columnList, columnTypeList, commentList, columnIsNullableList);
                    generateMapperOrService(properties, newTableName, "mapper");
                    generateMapperOrService(properties, newTableName, "service");
                    generateServiceImpl(properties, newTableName);
                    generateController(properties, newTableName);
                }
            } catch (SQLException | IOException e) {
                log.error("----------------------{ 执行出现异常!!! }------------------------\n");
                log.error("异常信息: " + e);
                e.printStackTrace();
            }finally {
                DBUtil.closeDB(connection, ps, rs);
                DBUtil.closeDB(connection, ps1, rs1);
            }
        }else{
            log.error("----------------------{ 连接数据库失败!!!  请核对配置文件!!! }------------------------\n");
        }
        log.info("*******   SUCCESS    ***********[ 所有数据已生成! ]**********   SUCCESS    ********\n\n");
    }

    /**
     * 生成实体类
     * @param tableName 表名
     * @param columns 列集合
     * @param columnTypes 列属性集合
     * @param comments 列注释集合
     * @throws IOException
     */
    private static void generateModel(Properties properties, String tableName, List<String> columns, List<String> columnTypes, List<String> comments, List<Boolean> columnIsNullableList) throws IOException {
        //创建Freemarker配置实例
        Map<String, Object> map = new HashMap<String, Object>();
        Writer out = null;
        Configuration cfg = new Configuration();
        //加载模板文件
        cfg.setClassForTemplateLoading(Methods.class, "/generate/ftl");
        try {
            Template temp = cfg.getTemplate("model.ftl");
            map.put("modelPackage", properties.getProperty("modelPackage"));
            map.put("columnTypes", columnTypes);
            boolean useSwagger = Objects.requireNonNull(properties.getProperty("useSwagger")).equalsIgnoreCase("true");
            // 是否启用Swagger
            if(useSwagger){
                map.put("tableApi", "@ApiModel(description = " + tableName + ")");
            }
            map.put("tableName", tableName);
            map.put("import", "import javax.validation.constraints.NotBlank;\n" +
                    "import javax.validation.constraints.NotNull;");
            StringBuilder sBuffer = new StringBuilder("\n");
            // 循环列 类型
            for (int i = 0; i < columns.size(); i++) {
                String column = CamelCaseUtil.underscoreToCamelCase(columns.get(i));
                System.out.println(column);
                String columnType = columnTypes.get(i);
                // 拼装属性
                sBuffer.append("\n\t/**\n" + "\t* ").append("".equals(comments.get(i)) ? column : comments.get(i)).append(" \n").append("\t*/");
                if(useSwagger){
                    sBuffer.append("\n\t@ApiModelProperty(\"").append(comments.get(i)).append("\")\n");
                }
                // 第一个默认是ID
                if(i == 0){
                    sBuffer.append("\n\t@TableId(value = \"").append(column).append("\", type = IdType.AUTO)");
                }else{
                    if(column.equalsIgnoreCase("createTime") || column.equalsIgnoreCase("isDel")){
                        sBuffer.append("\n\t@TableField(value = \"").append(column).append("\" , fill = FieldFill.INSERT)");
                    }else{
                        sBuffer.append("\n\t@TableField(value = \"").append(column).append("\")");
                    }
                    // 非自动填充字段
                    if(!(column.equalsIgnoreCase("createTime") || column.equalsIgnoreCase("isDel"))){
                        // 当非空时验证
                        if(columnIsNullableList.get(i)){
                            if(columnType.equalsIgnoreCase("Long") || columnType.equalsIgnoreCase("Integer") || columnType.equalsIgnoreCase("BigDecimal")){
                                sBuffer.append("\n\t@NotNull(message = \"").append(comments.get(i)).append("不能为空\")");
                            }else{
                                sBuffer.append("\n\t@NotBlank(message = \"").append(comments.get(i)).append("不能为空\")");
                            }
                        }
                    }
                }
                sBuffer.append("\n\tprivate ").append(columnType).append(" ").append(columns.get(i)).append("; \n");
            }
            map.put("property", sBuffer.toString());
            out = new OutputStreamWriter(generateFile(properties.getProperty("filePath")+"\\"+ Objects.requireNonNull(properties.getProperty("modelPackage")).replaceAll("\\.", "\\\\"), tableName+".java"));
            temp.process(map, out);
            log.info("================================[ Model 已生成 ]==================================\n");
        } catch (IOException | TemplateException e) {
            log.error("================================[ Model 生成异常 ]==================================\n");
            e.printStackTrace();
        } finally {
            assert out != null;
            out.flush();
        }
    }

    /**
     * 生成Dao/Service层接口
     * @param tableName 表名
     * @param templateName 模版名称 接收mapper 生成mapper 接收service层 生成service层 其他值默认 生成mapper层
     * @throws IOException
     */
    private static void generateMapperOrService(Properties properties, String tableName, String templateName) throws IOException {
        String tempName = "";
        String path = "";
        Map<String, Object> map = new HashMap<String, Object>();
        Writer out = null;
        Configuration cfg = new Configuration();
        //加载模板文件
        cfg.setClassForTemplateLoading(Methods.class, "/generate/ftl");
        try {
            if("service".equalsIgnoreCase(templateName)){
                tempName = "service.ftl";
                map.put("modelPackage", properties.getProperty("modelPackage"));
                map.put("servicePackage", properties.getProperty("servicePackage"));
                path = Objects.requireNonNull(properties.getProperty("servicePackage")).replaceAll("\\.", "\\\\");
            } else {
                tempName = "mapper.ftl";
                map.put("modelPackage", properties.getProperty("modelPackage"));
                map.put("daoPackage", properties.getProperty("daoPackage"));
                path = Objects.requireNonNull(properties.getProperty("daoPackage")).replaceAll("\\.", "\\\\");
            }
            Template temp = cfg.getTemplate(tempName);
            map.put("tableName", tableName);
            if("service".equalsIgnoreCase(templateName)){
                out = new OutputStreamWriter(generateFile(properties.getProperty("filePath")+"\\" + path, tableName+"Service.java"));
            }else{
                out = new OutputStreamWriter(generateFile(properties.getProperty("filePath")+"\\" + path, tableName+"Mapper.java"));
            }
            temp.process(map, out);
            log.info("================================[ "+StringUtils.upperCase(templateName)+" 已生成 ]==================================\n");
        } catch (IOException | TemplateException e) {
            log.error("================================[ "+StringUtils.upperCase(templateName)+" 生成异常 ]==================================\n");
            e.printStackTrace();
        } finally {
            assert out != null;
            out.flush();
        }
    }

    /**
     * 生成Service实现类
     * @param tableName 表名
     * @throws IOException
     */
    private static void generateServiceImpl(Properties properties, String tableName) throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        Writer out = null;
        try {
            Configuration cfg = new Configuration();
            //加载模板文件
            cfg.setClassForTemplateLoading(Methods.class, "/generate/ftl");
            Template temp = cfg.getTemplate("serviceImpl.ftl");
            map.put("tableName", tableName);
            map.put("modelPackage", properties.getProperty("modelPackage"));
            map.put("daoPackage", properties.getProperty("daoPackage"));
            map.put("servicePackage", properties.getProperty("servicePackage"));
            map.put("serviceImplPackage", properties.getProperty("serviceImplPackage"));
            out = new OutputStreamWriter(generateFile(properties.getProperty("filePath")+"\\"+ Objects.requireNonNull(properties.getProperty("serviceImplPackage")).replaceAll("\\.", "\\\\"), tableName+"ServiceImpl.java"));
            temp.process(map, out);
            log.info("================================[ ServiceImpl 已生成 ]==================================\n");
        } catch (TemplateException e) {
            log.error("================================[ ServiceImpl 生成异常 ]==================================\n");
            e.printStackTrace();
        } finally {
            assert out != null;
            out.flush();
        }
    }

    /**
     * 生成Controller控制层
     * @param properties
     * @param tableName
     */
    private static void generateController(Properties properties, String tableName) throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        Writer out = null;
        try {
            Configuration cfg = new Configuration();
            //加载模板文件
            cfg.setClassForTemplateLoading(Methods.class, "/generate/ftl");
            Template temp = cfg.getTemplate("controller.ftl");
            map.put("requestMapping", StringUtils.toLowerCaseFirstOne(tableName));
            map.put("tableName", tableName);
            map.put("modelPackage", properties.getProperty("modelPackage"));
            map.put("servicePackage", properties.getProperty("servicePackage"));
            map.put("controllerPackage", properties.getProperty("controllerPackage"));
            out = new OutputStreamWriter(generateFile(properties.getProperty("filePath")+"\\"+ Objects.requireNonNull(properties.getProperty("controllerPackage")).replaceAll("\\.", "\\\\"), tableName+"Controller.java"));
            temp.process(map, out);
            log.info("================================[ Controller 已生成 ]==================================\n");
        } catch (IOException | TemplateException e) {
            log.error("================================[ Controller 生成异常 ]==================================\n");
            e.printStackTrace();
        } finally {
            assert out != null;
            out.flush();
        }
    }

    /**
     * 获取配置文件信息
     * @param confPath
     * @return
     * @throws IOException
     */
    public static Properties getConf(String confPath) throws IOException {
        InputStream is= Methods.class.getResourceAsStream(confPath != null && !confPath.equals("") ? confPath : "/generate/conf/config.properties");
        Properties properties = new Properties();
        properties.load(is);
        return properties;
    }


    /**
     * @Title: sqlTypesToJava
     * @Description: 数据库字段类型值 对应的java类型
     * @param @param code
     * @return String
     * @author lhc
     */
    private static String sqlTypesToJava(Integer code){
        Integer[] IntegerType = {4,-6,5};
        Integer[] LongType = {-5};
        Integer[] StringType = {12,1,-1};
        Integer[] BigDecimalType = {3,8};
        String type = "";
        if(StringUtils.containsArr(IntegerType, code)){
            type="Integer";
        }else if(StringUtils.containsArr(StringType, code)){
            type="String";
        }else if(StringUtils.containsArr(LongType, code)){
            type="Long";
        }else if(StringUtils.containsArr(BigDecimalType, code)){
            type="BigDecimal";
        }else{
            type="String";
        }
        return type;
    }

    /**
     * @Title: generateFile
     * @Description: 生成文件
     * @author lihaisteward
     */
    private static FileOutputStream generateFile(String path, String fileName) {
        File parentPath=new File(path);
        if(!parentPath.exists()){
            parentPath.mkdirs();
        }
        FileOutputStream out = null;
        String realPath = path+"//"+fileName;
        File realFile=new File(realPath);
        if(!realFile.exists()){
            try {
                realFile.createNewFile();
                out = new FileOutputStream(realFile,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            realFile.delete();
            try {
                realFile.createNewFile();
                out = new FileOutputStream(realFile,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out;
    }
}
