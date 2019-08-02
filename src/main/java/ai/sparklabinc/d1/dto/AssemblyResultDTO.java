package ai.sparklabinc.d1.dto;

import ai.sparklabinc.d1.entity.DsFormTableSettingDO;

import javax.sql.DataSource;
import java.util.List;

public class AssemblyResultDTO {
	
	private String countSql;
	
	private String querySql;
	
	private List<DsFormTableSettingDO> dsFormTableSettingDOS;

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

	public List<DsFormTableSettingDO> getDsFormTableSettingDOS() {
		return dsFormTableSettingDOS;
	}

	public void setDsFormTableSettingDOS(List<DsFormTableSettingDO> dsFormTableSettingDOS) {
		this.dsFormTableSettingDOS = dsFormTableSettingDOS;
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
