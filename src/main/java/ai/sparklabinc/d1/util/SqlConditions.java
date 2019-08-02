package ai.sparklabinc.d1.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * sql条件动态拼接
 * @author zhouwei
 *
 */
public class SqlConditions {
	
	private  List<String>  conditions = new ArrayList<>();
	private List<Object> parameters = new ArrayList<>();
	private List<String> paramTypes=new ArrayList<>();



	/**
	 * like查询
	 * @param tableFieldName 字段名
	 * @param value 
	 */
	public void createLikeCondions(String tableFieldName, Object value,String tableFieldType) {
		conditions.add( tableFieldName + " like ?");
		parameters.add("%" + value +"%");
		paramTypes.add(tableFieldType);
	}
	/**
	 * 等于
	 * @param tableFieldName
	 * @param value
	 */
	public void createEqualCondition(String tableFieldName, Object value,String tableFieldType) {
		conditions.add( tableFieldName + "= ?");
		parameters.add(value);
		paramTypes.add(tableFieldType);
	}

    /**
     * 等于
     * @param tableFieldName
     * @param value
     */
    public void createNotEqualCondition(String tableFieldName, Object value,String tableFieldType) {
        conditions.add( tableFieldName + "<> ?");
        parameters.add(value);
		paramTypes.add(tableFieldType);
    }
	
	/**
	 * 查询区间，可以查询时间
	 * @param tableFieldName
	 * @param start
	 * @param end
	 */
	public void createBetweenCondition(String tableFieldName, Object start, Object end ,String tableFieldType) {
		conditions.add("(" + tableFieldName + " BETWEEN ? AND ? )" );
		parameters.add(start);
		paramTypes.add(tableFieldType);
		parameters.add(end);
		paramTypes.add(tableFieldType);

	}

	/**
	 * 查询区间，可以查询时间
	 * @param tableFieldName
	 * @param start
	 * @param end
	 */
	public void createBetweenUnknown(String tableFieldName, Object value1, Object value2,String tableFieldType) {
		conditions.add("(" + tableFieldName + " BETWEEN ? AND ? or " + tableFieldName + " BETWEEN ? AND ?)");
		parameters.add(value1);
		paramTypes.add(tableFieldType);

		parameters.add(value2);
		paramTypes.add(tableFieldType);

		parameters.add(value2);
		paramTypes.add(tableFieldType);

		parameters.add(value1);
		paramTypes.add(tableFieldType);
	}

	/**
	 * or条件，一个字段多个值 =
	 * @param tableFieldName
	 * @param objs
	 */
	public void createEqualOneFieldAndMultipleValue(String tableFieldName, Object[] objs,String tableFieldType) {
		List<String> mayClause = new ArrayList<>();
		for (Object obj : objs) {
			mayClause.add(tableFieldName + "=?");
			parameters.add(obj);
			paramTypes.add(tableFieldType);
		}
		conditions.add("(" + org.apache.commons.lang3.StringUtils.join(mayClause," or ") +")");
	}
	/**
	 * or条件，一个字段多个值 like
	 * @param tableFieldName
	 * @param objs
	 */
	public void createLikeOneFieldAndMultipleValue(String tableFieldName, Object[] objs,String tableFieldType) {
		List<String> mayClause = new ArrayList<>();
		
		for (Object obj : objs) {
			mayClause.add(tableFieldName + " like ?");
			parameters.add("%" + obj +"%");
			paramTypes.add(tableFieldType);
		}
		conditions.add("(" + org.apache.commons.lang3.StringUtils.join(mayClause," or ") +")");
	}
	
	/**
	 * or条件，一个值查询多个字段 =
	 * @param tableFieldNames
	 * @param value
	 */
	public void createEqualMultipleFieldAndOneCondition(String[] tableFieldNames,String[] tableFieldTypes,Object value) {
		List<String> mayClause = new ArrayList<>();
		
		for (String fieldName : tableFieldNames) {
			mayClause.add(fieldName + "=?");
			parameters.add(value);
		}
		paramTypes.addAll(Arrays.asList(tableFieldTypes));

		conditions.add("(" + org.apache.commons.lang3.StringUtils.join(mayClause," or ") +")");
	}
	
	/**
	 * or条件，一个值查询多个字段,like
	 * @param tableFieldNames
	 * @param value
	 */
	public void createLikeMultipleFieldAndOneValue(String[] tableFieldNames,Object  value,String[] tableFieldTypes) {
		List<String> mayClause = new ArrayList<>();
		
		for (String fieldName : tableFieldNames) {
			mayClause.add(fieldName + " like ?");
			parameters.add("%" + value +"%");
		}
		paramTypes.addAll(Arrays.asList(tableFieldTypes));
		conditions.add("(" + org.apache.commons.lang3.StringUtils.join(mayClause," or ") +")");
	}
	/**
	 * or 条件 ，多个值查询多个字段 like
	 * @param tableFieldNames
	 */
	public void createLikeMultipleFieldAndMultipleValue(String[] tableFieldNames,Object[]  values) {
		List<String> mayClause = new ArrayList<>();
		for (String fieldName : tableFieldNames) {
			for (Object value : values) {
				mayClause.add(fieldName + " like ?");
				parameters.add("%" + value +"%");
				paramTypes.add("varchar(20)");
			}
		}
		conditions.add("(" + org.apache.commons.lang3.StringUtils.join(mayClause," or ") +")");
	}
	
	public void createSql(String sql) {
		conditions.add(sql);
	}

	public void createTimeIntersection(String timeStart, String timeEnd,Object valueStart,Object valueEnd) {
		conditions.add("NOT ((" + timeEnd + " < ? ) OR ("+timeStart+" > ?))" );
		parameters.add(valueStart);
		paramTypes.add("datetime");
		parameters.add(valueEnd);
		paramTypes.add("datetime");
	}


	/**
	 * in条件，一个字段多个值 =
	 * @param tableFieldName
	 * @param objects
	 */
	public void createInOneFieldAndMultipleValue(String tableFieldName, Object[] objects,String tableFieldType) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(tableFieldName + " in (");

		StringBuilder conditionBuider = new StringBuilder();
		for (Object parameter : objects) {
			conditionBuider.append("?,");
			parameters.add(parameter);
			paramTypes.add(tableFieldType);
		}

		if(conditionBuider.length() > 0){
			conditionBuider.deleteCharAt(conditionBuider.length()-1);
			stringBuilder.append(conditionBuider);
			stringBuilder.append(")");
			conditions.add(stringBuilder.toString());
		}
	}


	/**
	 * 查询区间，可以查询时间(数字)
	 * @param tableFieldName
	 * @param start
	 * @param end
	 */
	public  void createRangeCondition(String tableFieldName, Object start, Object end,String tableFieldType ) {

		if(start == null && end == null){
			return;
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("(");
		if(start != null && end != null ){
			stringBuilder.append(tableFieldName);
			stringBuilder.append(" >= ? ");
			stringBuilder.append(" and ");
			stringBuilder.append(tableFieldName);
			stringBuilder.append(" <= ?");
			parameters.add(start);
			paramTypes.add(tableFieldType);
			parameters.add(end);
			paramTypes.add(tableFieldType);
		}else if(start != null && end == null){
			stringBuilder.append(tableFieldName);
			stringBuilder.append(" >= ? ");
			parameters.add(start);
			paramTypes.add(tableFieldType);

		}else{
			stringBuilder.append(tableFieldName);
			stringBuilder.append(" <= ?");
			parameters.add(end);
			paramTypes.add(tableFieldType);
		}
		stringBuilder.append(")");
		conditions.add(stringBuilder.toString());
	}


	public String getWhereClause() {
		return org.apache.commons.lang3.StringUtils.join(conditions, " AND ");
	}

	public List<Object> getParameters() {
		return parameters;
	}
	public List<String> getParamTypes() {
		return paramTypes;
	}
	public boolean isEmptyConditions() {
		return conditions.isEmpty();
	}

	public static void main(String[] args) {
		System.out.println(new SqlConditions().getWhereClause());
	}
}
