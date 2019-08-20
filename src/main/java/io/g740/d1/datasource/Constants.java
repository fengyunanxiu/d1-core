package io.g740.d1.datasource;

/**
 * 数据源类型常量
 */
public interface  Constants {
	 String DATABASE_TYPE_MYSQL = "MYSQL";
	 String DATABASE_TYPE_ORACLE = "ORACLE";
	 String DATABASE_TYPE_SQLSERVER = "SQLSERVER";
	 String DATABASE_TYPE_POSTGRESQL = "POSTGRESQL";
	 String DATABASE_TYPE_DB2 = "DB2";
	 String DATABASE_TYPE_SQLITE ="SQLITE";

	/**
	 * ssh验证方式
	 */
	enum SshAuthType {
		/**
		 * 密码
		 */
		PASSWORD,
		/**
		 * 密钥
		 */
		KEY_PAIR
	}

}
