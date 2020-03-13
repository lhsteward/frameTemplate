package com.lhc.generateFile.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Component
public class DBUtil {

    /**
     * @Title: getConnection
     * @Description: 获取数据库连接
     * @return Connection
     * @author lihaisteward
     */
    public static Connection getConnection(Properties properties) throws IOException {
        log.info("======================[ 正在连接数据库... ]=========================\n\n");
        Connection connection = null;
            try {
                if (Objects.requireNonNull(properties.getProperty("jdbc.driver")).contains("mysql")) {
                    Class.forName(properties.getProperty("jdbc.driver"));
                    connection = DriverManager.getConnection(Objects.requireNonNull(properties.getProperty("jdbc.url")), properties.getProperty("jdbc.username"), properties.getProperty("jdbc.password"));
                }else{
                    log.error("数据库有误!此配置只支持MySQL数据库!");
                }
            } catch (ClassNotFoundException | SQLException e) {
                log.error("======================[ 数据库连接失败! ]==========================");
                e.printStackTrace();
            }
        log.info("======================[ 数据库连接成功! ]==========================");
        return connection;
    }

    /**
     * 关闭数据库
     * @param conn
     * @param ps
     * @param rs
     */
    public static void closeDB(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            rs = null;

            if (ps != null) {
                ps.close();
            }
            ps = null;

            if (conn != null) {
                conn.close();
            }
            conn = null;
        } catch (Exception e) {
            log.error("数据源关闭失败", e);
        }
    }

}
