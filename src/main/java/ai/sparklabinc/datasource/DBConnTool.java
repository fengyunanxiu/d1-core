package ai.sparklabinc.datasource;

import java.sql.SQLException;

/**
 * @Function:
 * @Author: DAM
 * @Date: 2019/7/2 14:33
 * @Description:
 * @Version V1.0
 */
public class DBConnTool {
    /**
     * 获取默认数据库连接
     *
     * @return
     */
    public static DBConn getConnection() {
        DBConn dbConn = new DBConn();
        if (!dbConn.createConnection()) {
            // 如果创建连接失败
            DBSemaphore.unLock();
            return null;
        }

        // 连接成功，设置该连接属性
        try {
            // 特殊处理连接的AutoCommit是否已经被设置
            dbConn.setAutoCommit(true);
            dbConn.setInUse();
            DBSemaphore.unLock();
            return dbConn;
        } catch (Exception ex) {
            ex.printStackTrace();
            DBSemaphore.unLock();
            return null;
        }

    }

    /**
     * 通过URI地址获取指定数据库连接
     *
     * @param uri
     * @return
     */
    public static DBConn getConnection(String uri) {
        DBConn dbConn = new DBConn(uri);
        if (!dbConn.createConnection()) {
            // 如果创建连接失败
            DBSemaphore.unLock();
            return null;
        }
        try {
            // 特殊处理连接的AutoCommit是否已经被设置
            dbConn.setAutoCommit(true);
            dbConn.setInUse();
            DBSemaphore.unLock();
            return dbConn;
        } catch (Exception ex) {
            ex.printStackTrace();
            // DBSemaphore.UnLock();
            return null;
        }

    }

    public static void main(String[] args) {
        // 测试连接池
        // 1、连接mysql 数据库
        DBConnTool.getConnection("mysql");

//        if (conn == null) {
//            System.out.println("获取连接失败！");
//        } else {
//            System.out.println("获取连接成功");
//        }


    }
}
