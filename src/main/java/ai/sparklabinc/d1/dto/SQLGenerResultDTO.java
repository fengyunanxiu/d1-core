package ai.sparklabinc.d1.dto;
import java.util.List;

public class SQLGenerResultDTO {
	
	private String countSql;
	
	private String querySql;

	private List<Object> paramsValue;

	private List<String> paramsType;

	private String sqlType;


	public String getCountSql() {
		return countSql;
	}

	public void setCountSql(String countSql) {
		this.countSql = countSql;
	}

	public String getQuerySql() {
		return querySql;
	}

	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}

	public List<Object> getParamsValue() {
		return paramsValue;
	}

	public void setParamsValue(List<Object> paramsValue) {
		this.paramsValue = paramsValue;
	}

	public List<String> getParamsType() {
		return paramsType;
	}

	public void setParamsType(List<String> paramsType) {
		this.paramsType = paramsType;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}
}
