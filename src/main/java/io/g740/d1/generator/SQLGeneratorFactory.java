package io.g740.d1.generator;

import io.g740.d1.generator.impl.MySQLGenerator;
import io.g740.d1.generator.impl.OralceGenerator;
import io.g740.d1.generator.impl.PostgreSQLGenerator;
import io.g740.d1.generator.impl.SqlServerGenerator;
import org.springframework.stereotype.Component;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/29 16:12
 * @description:
 * @version: V1.0
 */
@Component
public class SQLGeneratorFactory {
    private MySQLGenerator mySQLGenerator;
    private OralceGenerator oralceGenerator;
    private PostgreSQLGenerator postgreSQLGenerator;
    private SqlServerGenerator sqlServerGenerator;

    public SQLGenerator builder(String sqlType) {
        switch (sqlType) {
            case SQLType.SQL_TYPE_MYSQL:
                return this.getMySQLGenerator();
            case SQLType.SQL_TYPE_ORACLE:
                return this.getOralceGenerator();
            case SQLType.SQL_TYPE_POSTGRESQL:
                return this.getPostgreSQLGenerator();
            case SQLType.SQL_TYPE_SQLSERVER:
                return this.getSqlServerGenerator();
            default:
                return this.getMySQLGenerator();
        }
    }

    private MySQLGenerator getMySQLGenerator() {
        if (mySQLGenerator == null) {
            mySQLGenerator = new MySQLGenerator();
        }
        return mySQLGenerator;
    }

    private OralceGenerator getOralceGenerator() {
        if (oralceGenerator == null) {
            oralceGenerator = new OralceGenerator();
        }
        return oralceGenerator;
    }

    private PostgreSQLGenerator getPostgreSQLGenerator() {
        if (postgreSQLGenerator == null) {
            postgreSQLGenerator = new PostgreSQLGenerator();
        }
        return postgreSQLGenerator;
    }

    private SqlServerGenerator getSqlServerGenerator() {
        if(sqlServerGenerator==null){
            sqlServerGenerator=new SqlServerGenerator();
        }
        return sqlServerGenerator;
    }

    public static class SQLType {
        final static String SQL_TYPE_MYSQL = "MYSQL";
        final static String SQL_TYPE_ORACLE = "ORACLE";
        final static String SQL_TYPE_SQLSERVER = "SQLSERVER";
        final static String SQL_TYPE_POSTGRESQL = "POSTGRESQL";
    }
}
