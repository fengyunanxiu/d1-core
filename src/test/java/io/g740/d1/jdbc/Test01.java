package io.g740.d1.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Test01 {

    public static void main(String[] args) {
     //四大参数
     String drivername = "com.mysql.jdbc.Driver";
     String url = "jdbc:mysql://192.168.199.231:3306/d1_core";
     String name = "root";
     String password = "FnJUC1XsShQp6cZb";
 
     // 创建sql语句
     String sql = "create table if not exists data_export_task(\n" +
             "    id        bigint primary key auto_increment,\n" +
             "    start_at  varchar(30),\n" +
             "    end_at    varchar(30),\n" +
             "    failed_at varchar(30),\n" +
             "    details   text,\n" +
             "    file_name varchar(256),\n" +
             "    file_path varchar(512)\n" +
             ") charset = utf8;";
     Connection conn = null;
     PreparedStatement pstmt = null;
     int result = 0;
 
     // 连接数据库
     try {
        // 加载驱动
        Class.forName(drivername);
        // 建立连接
        conn = DriverManager.getConnection(url, name, password);
        if (conn != null) {
           System.out.println("数据库连接成功");
           // 创建prepareStatement
           pstmt = conn.prepareStatement(sql);
           result = pstmt.executeUpdate();
        }                 
        // 处理结果
        if (result == 0) {
           System.out.println("添加成功");
        } else {
           System.out.println("添加失败");
        }
 
     } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
     } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
     }
     // 关闭 pstmt,conn
     if (pstmt != null) {
        try {
           pstmt.close();
        } catch (SQLException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
        }
        if (conn != null) {
           try {
              conn.close();
           } catch (SQLException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
           }
        }
     }
 
  }

}
