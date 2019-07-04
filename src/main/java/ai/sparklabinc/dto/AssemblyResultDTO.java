package ai.sparklabinc.dto;

import ai.sparklabinc.entity.DsFormTableSettingDO;
import java.util.List;

public class AssemblyResultDTO {
	
	private String countSql;
	
	private String querySql;
	
	List<DsFormTableSettingDO> dsFormTableSettingDOS;

	List<Object> paramList;

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
}
