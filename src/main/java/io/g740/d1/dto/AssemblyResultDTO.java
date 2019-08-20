package io.g740.d1.dto;

import io.g740.d1.entity.DfFormTableSettingDO;

import javax.sql.DataSource;
import java.util.List;

public class AssemblyResultDTO {
	
	private String countSql;
	
	private String querySql;
	
	private List<DfFormTableSettingDO> dfFormTableSettingDOS;

	private List<Object> paramList;

	private DataSource dataSource;

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

	public List<DfFormTableSettingDO> getDfFormTableSettingDOS() {
		return dfFormTableSettingDOS;
	}

	public void setDfFormTableSettingDOS(List<DfFormTableSettingDO> dfFormTableSettingDOS) {
		this.dfFormTableSettingDOS = dfFormTableSettingDOS;
	}

	public List<Object> getParamList() {
		return paramList;
	}

	public void setParamList(List<Object> paramList) {
		this.paramList = paramList;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
