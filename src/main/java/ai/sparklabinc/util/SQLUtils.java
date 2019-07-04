package ai.sparklabinc.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SQLUtils {
	private SQLUtils() {

	}

	public static String toSqlString(List<? extends Object> objects) {
		if (objects == null)
			return null;

		if (objects.size() == 0)
			return "";

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < objects.size() - 1; i++) {
			stringBuilder.append("'" + objects.get(i) + "',");
		}

		stringBuilder.append("'" + objects.get(objects.size() - 1) + "'");

		return stringBuilder.toString();
	}

	public static String toSqlString(Object[] objects) {
		if (objects == null)
			return null;

		if (objects.length == 0)
			return "";

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < objects.length; i++) {
			stringBuilder.append("'" + objects[i] + "'" + (i == objects.length - 1 ? "" : ","));
		}

		return stringBuilder.toString();
	}

	/**
	 * 构造精确Equals查询参数
	 * 
	 * @param queryParameterMap
	 * @return
	 * @throws Exception
	 */
	public static String buildAccurateEqualsStringQueryParameterString(Map<String, String> queryParameterMap)
			throws Exception {
		StringBuilder filterParameterString = new StringBuilder();
		for (Map.Entry<String, String> queryParameter : queryParameterMap.entrySet()) {
			String filterParameterKey = queryParameter.getKey();
			String filterParameterValue = queryParameter.getValue();

			// 永远加上" AND"
			filterParameterString.append(" AND");

			// 参数的总左括号
			filterParameterString.append(" (");

			// 参数名
			filterParameterString.append(filterParameterKey);
			// 参数值
			filterParameterValue = filterParameterValue.replace("'", "\'");
			filterParameterString.append(" = '" + filterParameterValue + "'");

			// 参数的总右括号
			filterParameterString.append(")");
		}
		return filterParameterString.toString();
	}

	/**
	 * 构造精确Equals查询参数
	 * 
	 * @param queryParameterMap
	 * @return
	 * @throws Exception
	 */
	public static String buildAccurateEqualsNumberQueryParameterString(Map<String, String> queryParameterMap)
			throws Exception {
		StringBuilder filterParameterString = new StringBuilder();
		for (Map.Entry<String, String> queryParameter : queryParameterMap.entrySet()) {
			String filterParameterKey = queryParameter.getKey();
			String filterParameterValue = queryParameter.getValue();

			// 永远加上" AND"
			filterParameterString.append(" AND");

			// 参数的总左括号
			filterParameterString.append(" (");

			// 参数名
			filterParameterString.append(filterParameterKey);
			// 参数值
			filterParameterString.append(" = " + filterParameterValue);

			// 参数的总右括号
			filterParameterString.append(")");
		}

		return filterParameterString.toString();
	}

	/**
	 * 构造精确候选范围查询参数
	 * 
	 * @param queryParameterMap
	 * @return
	 * @throws Exception
	 */
	public static String buildAccurateInStringQueryParameterString(Map<String, String[]> queryParameterMap)
			throws Exception {
		StringBuilder filterParameterString = new StringBuilder();
		for (Map.Entry<String, String[]> queryParameter : queryParameterMap.entrySet()) {
			String filterParameterKey = queryParameter.getKey();
			String[] filterParameterValues = queryParameter.getValue();

			// 永远加上" AND"
			filterParameterString.append(" AND");

			// 参数的总左括号
			filterParameterString.append(" (");

			if (filterParameterValues.length <= 1000) {
				// 根据Alibaba的Java开发手册，IN后边的集合元素数量控制在1000个之内
				// 参数名
				filterParameterString.append(filterParameterKey);
				filterParameterString.append(" IN (");
				// 参数值
				for (int i = 0; i < filterParameterValues.length; i++) {
					String filterParameterValueSlice = filterParameterValues[i].replace("'", "\'");

					filterParameterString.append(
							"'" + filterParameterValueSlice + "'" + (i == filterParameterValues.length - 1 ? "" : ","));
				}
				filterParameterString.append(")");
			} else {
				// 超过部分分成多个IN
				int countOfIN = filterParameterValues.length / 1000
						+ (filterParameterValues.length % 1000 == 0 ? 0 : 1);
				for (int i = 0; i < countOfIN; i++) {
					filterParameterString.append((i == 0 ? "" : " OR "));
					// 参数名
					filterParameterString.append(filterParameterKey);
					filterParameterString.append(" IN (");

					int jEnd = 1000;
					if (i == countOfIN - 1)
						jEnd = filterParameterValues.length - i * 1000;

					for (int j = 0; j < jEnd; j++) {
						String filterParameterValueSlice = filterParameterValues[i * 1000 + j].replace("'", "\'");

						filterParameterString
								.append("'" + filterParameterValueSlice + "'" + (j == jEnd - 1 ? "" : ","));
					}

					filterParameterString.append(")");
				}
			}
			// 参数的总右括号
			filterParameterString.append(")");
		}

		return filterParameterString.toString();
	}

	/**
	 * 构造精确候选范围查询参数
	 * 
	 * @param queryParameterMap
	 * @return
	 * @throws Exception
	 */
	public static String buildAccurateInNumberQueryParameterString(Map<String, String[]> queryParameterMap)
			throws Exception {
		StringBuilder filterParameterString = new StringBuilder();
		for (Map.Entry<String, String[]> queryParameter : queryParameterMap.entrySet()) {
			String filterParameterKey = queryParameter.getKey();
			String[] filterParameterValues = queryParameter.getValue();

			// 永远加上" AND"
			filterParameterString.append(" AND");

			// 参数的总左括号
			filterParameterString.append(" (");

			if (filterParameterValues.length <= 1000) {
				// 根据Alibaba的Java开发手册，IN后边的集合元素数量控制在1000个之内
				// 参数名
				filterParameterString.append(filterParameterKey);
				filterParameterString.append(" IN (");
				// 参数值
				for (int i = 0; i < filterParameterValues.length; i++) {
					filterParameterString
							.append(filterParameterValues[i] + (i == filterParameterValues.length - 1 ? "" : ","));
				}
				filterParameterString.append(")");
			} else {
				// 超过部分分成多个IN
				int countOfIN = filterParameterValues.length / 1000
						+ (filterParameterValues.length % 1000 == 0 ? 0 : 1);
				for (int i = 0; i < countOfIN; i++) {
					filterParameterString.append((i == 0 ? "" : " OR "));
					// 参数名
					filterParameterString.append(filterParameterKey);
					filterParameterString.append(" IN (");

					int jEnd = 1000;
					if (i == countOfIN - 1)
						jEnd = filterParameterValues.length - i * 1000;

					for (int j = 0; j < jEnd; j++) {
						filterParameterString.append(filterParameterValues[i * 1000 + j] + (j == jEnd - 1 ? "" : ","));
					}

					filterParameterString.append(")");
				}
			}

			// 参数的总右括号
			filterParameterString.append(")");
		}

		return filterParameterString.toString();

	}

	/**
	 * 构造精确日期区间查询参数
	 * 
	 * @param queryParameterMap
	 * @return
	 * @throws Exception
	 */
	public static String buildAccurateDateRangeQueryParameterString(Map<String, String[]> queryParameterMap)
			throws Exception {
		StringBuilder filterParameterString = new StringBuilder();
		for (Map.Entry<String, String[]> queryParameter : queryParameterMap.entrySet()) {
			String filterParameterKey = queryParameter.getKey();
			String[] filterParameterValues = queryParameter.getValue();

			if (filterParameterValues.length == 1) {
				if (DateUtils.isDate(filterParameterValues[0])) {
					// 永远加上" AND"
					filterParameterString.append(" AND (");

					filterParameterString.append("(");
					// 参数名
					filterParameterString.append(filterParameterKey);
					// 参数值1
					filterParameterString.append(" >= '" + filterParameterValues[0] + " 00:00:00'");
					filterParameterString.append(") AND (");
					// 参数名
					filterParameterString.append(filterParameterKey);
					// 参数值2
					filterParameterString.append(" <= '" + filterParameterValues[0] + " 23:59:59'");
					filterParameterString.append(")");

					// 参数的总右括号
					filterParameterString.append(")");
				}else if(DateUtils.isDateTime(filterParameterValues[0])) {
					// 永远加上" AND"
					filterParameterString.append(" AND (");
					// 参数名
					filterParameterString.append(filterParameterKey);
					filterParameterString.append(" = '" + filterParameterValues[0] + "'");
					// 参数的总右括号
					filterParameterString.append(")");
				}
			} else if (filterParameterValues.length == 2) {
				// 有2个参数值的情况
				String filterParameterValue0 = filterParameterValues[0];
				String filterParameterValue1 = filterParameterValues[1];

				if (DateUtils.isDate(filterParameterValue0)
						&& DateUtils.isDate(filterParameterValue1)) {
					// 永远加上" AND"
					filterParameterString.append(" AND (");

					// 都为日期格式时，使用>= & <=从句
					// 比较filterParameterValue0和filterParameterValue1的大小
					DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
					DateTime filterParameterValue0DateTime = DateTime.parse(filterParameterValue0, dateTimeFormatter);
					DateTime filterParameterValue1DateTime = DateTime.parse(filterParameterValue1, dateTimeFormatter);
					if (filterParameterValue0DateTime.getMillis() <= filterParameterValue1DateTime.getMillis()) {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(filterParameterKey);
						// 参数值1
						filterParameterString.append(" >= '" + filterParameterValue0 + " 00:00:00'");
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(filterParameterKey);
						// 参数值2
						filterParameterString.append(" <= '" + filterParameterValue1 + " 23:59:59'");
						filterParameterString.append(")");
					} else {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(filterParameterKey);
						// 参数值1
						filterParameterString.append(" >= '" + filterParameterValue1 + " 00:00:00'");
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(filterParameterKey);
						// 参数值2
						filterParameterString.append(" <= '" + filterParameterValue0 + " 23:59:59'");
						filterParameterString.append(")");
					}

					// 参数的总右括号
					filterParameterString.append(")");
				}else if(DateUtils.isDateTime(filterParameterValue0) && DateUtils.isDateTime(filterParameterValue1)){
					// 永远加上" AND"
					filterParameterString.append(" AND (");
					// 都为日期格式时，使用>= & <=从句
					// 比较filterParameterValue0和filterParameterValue1的大小
					DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
					DateTime filterParameterValue0DateTime = DateTime.parse(filterParameterValue0, dateTimeFormatter);
					DateTime filterParameterValue1DateTime = DateTime.parse(filterParameterValue1, dateTimeFormatter);
					if (filterParameterValue0DateTime.getMillis() <= filterParameterValue1DateTime.getMillis()) {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值1
						filterParameterString.append(" >= '" + filterParameterValue0 + "'");
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值2
						filterParameterString.append(" <= '" + filterParameterValue1 + "'");
						filterParameterString.append(")");
					} else {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值1
						filterParameterString.append(" >= '" + filterParameterValue1 + "'");
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值2
						filterParameterString.append(" <= '" + filterParameterValue0 + "'");
						filterParameterString.append(")");
					}
					// 参数的总右括号
					filterParameterString.append(")");
				}
			} else {
				// 3个值及以上
				// 忽略
			}
		}

		return filterParameterString.toString();
	}

	/**
	 * 构造精确数字区间查询参数
	 * 
	 * @param queryParameterMap
	 * @return
	 * @throws Exception
	 */
	public static String buildAccurateNumberRangeQueryParameterString(Map<String, String[]> queryParameterMap)
			throws Exception {
		StringBuilder filterParameterString = new StringBuilder();
		for (Map.Entry<String, String[]> queryParameter : queryParameterMap.entrySet()) {
			String filterParameterKey = queryParameter.getKey();
			String[] filterParameterValues = queryParameter.getValue();

			if (filterParameterValues.length == 1) {
				if (StringUtils.isNumber(filterParameterValues[0])) {
					double filterParameterValueAsDouble = Double.parseDouble(filterParameterValues[0]);

					// 永远加上" AND"
					filterParameterString.append(" AND (");

					filterParameterString.append("(");
					// 参数名
					filterParameterString.append(filterParameterKey);
					// 参数值1
					filterParameterString.append(" >= " + (filterParameterValueAsDouble - 0.0001d));
					filterParameterString.append(") AND (");
					// 参数名
					filterParameterString.append(filterParameterKey);
					// 参数值2
					filterParameterString.append(" <= " + (filterParameterValueAsDouble + 0.0001d));
					filterParameterString.append(")");

					// 参数的总右括号
					filterParameterString.append(")");
				}
			} else if (filterParameterValues.length == 2) {
				// 有2个参数值的情况
				String filterParameterValue0 = filterParameterValues[0];
				String filterParameterValue1 = filterParameterValues[1];
				
				if(DateUtils.isDate(filterParameterValue0) && DateUtils.isDate(filterParameterValue1)) {
					// 永远加上" AND"
					filterParameterString.append(" AND (");

					Double filterParameterValue0AsDouble = Double.parseDouble(filterParameterValue0);
					Double filterParameterValue1AsDouble = Double.parseDouble(filterParameterValue1);

					if (filterParameterValue0AsDouble.compareTo(filterParameterValue1AsDouble) <= 0) {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(filterParameterKey);
						// 参数值1
						filterParameterString.append(" >= " + (filterParameterValue0AsDouble - 0.0001d));
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(filterParameterKey);
						// 参数值2
						filterParameterString.append(" <= " + (filterParameterValue1AsDouble + 0.0001d));
						filterParameterString.append(")");
					} else {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(filterParameterKey);
						// 参数值1
						filterParameterString.append(" >= " + (filterParameterValue1AsDouble - 0.0001d));
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(filterParameterKey);
						// 参数值2
						filterParameterString.append(" <= " + (filterParameterValue0AsDouble + 0.0001d));
						filterParameterString.append(")");
					}

					// 参数的总右括号
					filterParameterString.append(")");
					/**
					 * 如果一个是Date 一个是Date Time ，参数的key还是一样的话，这个暂时处理不了
					 */
				}else if(DateUtils.isDateTime(filterParameterValue0) && DateUtils.isDateTime(filterParameterValue1)){
					// 都为日期格式时，使用>= & <=从句
					// 比较filterParameterValue0和filterParameterValue1的大小
					DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
					DateTime filterParameterValue0DateTime = DateTime.parse(filterParameterValue0, dateTimeFormatter);
					DateTime filterParameterValue1DateTime = DateTime.parse(filterParameterValue1, dateTimeFormatter);
					if (filterParameterValue0DateTime.getMillis() <= filterParameterValue1DateTime.getMillis()) {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值1
						filterParameterString.append(" >= '" + filterParameterValue0 + "'");
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值2
						filterParameterString.append(" <= '" + filterParameterValue1 + "'");
						filterParameterString.append(")");
					} else {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值1
						filterParameterString.append(" >= '" + filterParameterValue1);
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值2
						filterParameterString.append(" <= '" + filterParameterValue0);
						filterParameterString.append(")");
					}
				}
			} else {
				// 3个值及以上
				// 忽略
			}
		}

		return filterParameterString.toString();
	}

	/**
	 * 构造精确查询参数
	 * 
	 * @param queryParameterMap
	 * @return
	 * @throws Exception
	 */
	public static String buildAccurateQueryParameterString(Map<String, String[]> queryParameterMap) throws Exception {
		StringBuilder filterParameterString = new StringBuilder();
		for (Map.Entry<String, String[]> queryParameter : queryParameterMap.entrySet()) {
			// 永远加上" AND"
			filterParameterString.append(" AND");

			// 参数的总左括号
			filterParameterString.append(" (");

			// 构造此参数查询从句
			// API已排除空参数值，无值的参数，此处不需要再校验参数
			// 日期类型参数只接受yyyy-MM-dd格式参数值
			// 不支持非日期类型的区间查询
			String[] filterParameterValue = queryParameter.getValue();
			if (filterParameterValue.length == 1) {
				// 有1个参数值的情况
				String filterParameterValue0 = filterParameterValue[0];
				if (StringUtils.isNumber(filterParameterValue0)) {
					// 参数名
					filterParameterString.append(queryParameter.getKey());
					// 参数值
					filterParameterString.append(" = " + filterParameterValue0);
				} else if (DateUtils.isDate(filterParameterValue0)) {
					filterParameterString.append("(");
					// 参数名
					filterParameterString.append(queryParameter.getKey());
					// 参数值1
					filterParameterString.append(" >= '" + filterParameterValue0 + " 00:00:00'");
					filterParameterString.append(") AND (");
					// 参数名
					filterParameterString.append(queryParameter.getKey());
					// 参数值2
					filterParameterString.append(" <= '" + filterParameterValue0 + " 23:59:59'");
					filterParameterString.append(")");
				} else if(DateUtils.isDateTime(filterParameterValue0)){
					filterParameterString.append("(");
					filterParameterString.append(queryParameter.getKey());
					filterParameterString.append(" = '" + filterParameterValue0 + "'");
					filterParameterString.append(")");
				}else {
					// 参数名
					filterParameterString.append(queryParameter.getKey());
					// 参数值
					filterParameterValue0 = filterParameterValue0.replace("'", "\'");
					filterParameterString.append(" = '" + filterParameterValue0 + "'");
				}
			} else if (filterParameterValue.length == 2) {
				// 有2个参数值的情况
				String filterParameterValue0 = filterParameterValue[0];
				String filterParameterValue1 = filterParameterValue[1];
				if(DateUtils.isDate(filterParameterValue0) && DateUtils.isDate(filterParameterValue1)) {
					// 都为日期格式时，使用>= & <=从句
					// 比较filterParameterValue0和filterParameterValue1的大小
					DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
					DateTime filterParameterValue0DateTime = DateTime.parse(filterParameterValue0, dateTimeFormatter);
					DateTime filterParameterValue1DateTime = DateTime.parse(filterParameterValue1, dateTimeFormatter);
					if (filterParameterValue0DateTime.getMillis() <= filterParameterValue1DateTime.getMillis()) {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值1
						filterParameterString.append(" >= '" + filterParameterValue0 + " 00:00:00'");
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值2
						filterParameterString.append(" <= '" + filterParameterValue1 + " 23:59:59'");
						filterParameterString.append(")");
					} else {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值1
						filterParameterString.append(" >= '" + filterParameterValue1 + " 00:00:00'");
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值2
						filterParameterString.append(" <= '" + filterParameterValue0 + " 23:59:59'");
						filterParameterString.append(")");
					}
				}else if(DateUtils.isDateTime(filterParameterValue0) && DateUtils.isDateTime(filterParameterValue1)) {
					// 都为日期格式时，使用>= & <=从句
					// 比较filterParameterValue0和filterParameterValue1的大小
					DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
					DateTime filterParameterValue0DateTime = DateTime.parse(filterParameterValue0, dateTimeFormatter);
					DateTime filterParameterValue1DateTime = DateTime.parse(filterParameterValue1, dateTimeFormatter);
					if (filterParameterValue0DateTime.getMillis() <= filterParameterValue1DateTime.getMillis()) {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值1
						filterParameterString.append(" >= '" + filterParameterValue0 + "'");
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值2
						filterParameterString.append(" <= '" + filterParameterValue1 + "'");
						filterParameterString.append(")");
					} else {
						filterParameterString.append("(");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值1
						filterParameterString.append(" >= '" + filterParameterValue1 + "'");
						filterParameterString.append(") AND (");
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值2
						filterParameterString.append(" <= '" + filterParameterValue0 + "'");
						filterParameterString.append(")");
					}
				} else {
					if (StringUtils.isNumber(filterParameterValue0)
							&& StringUtils.isNumber(filterParameterValue1)) {
						// 都为数字格式时，使用>= & <=从句
						Double value0 = Double.parseDouble(filterParameterValue0);
						Double value1 = Double.parseDouble(filterParameterValue1);
						if (value0.compareTo(value1) >= 0) {
							filterParameterString.append("(");
							// 参数名
							filterParameterString.append(queryParameter.getKey());
							filterParameterString.append(" >= " + filterParameterValue1);
							filterParameterString.append(") AND (");
							filterParameterString.append(queryParameter.getKey());
							filterParameterString.append(" <= " + filterParameterValue0);
							filterParameterString.append(")");
						} else {
							// 参数名
							filterParameterString.append(queryParameter.getKey());
							filterParameterString.append(" >= " + filterParameterValue0);
							filterParameterString.append(") AND (");
							filterParameterString.append(queryParameter.getKey());
							filterParameterString.append(" <= " + filterParameterValue1);
							filterParameterString.append(")");
						}
					} else {
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						// 参数值
						filterParameterValue0 = filterParameterValue0.replace("'", "\'");
						filterParameterValue1 = filterParameterValue1.replace("'", "\'");
						filterParameterString
								.append(" IN ('" + filterParameterValue0 + "', '" + filterParameterValue1 + "')");
					}
				}
			} else {
				// 3个值及以上

				// 检查是否全数字类型
				int countOfNumber = 0;
				for (String filterParameterValueSlice : filterParameterValue) {
					if (StringUtils.isNumber(filterParameterValueSlice)) {
						countOfNumber++;
					}
				}

				// 全数字类型
				if (countOfNumber == filterParameterValue.length) {

					if (filterParameterValue.length <= 1000) {
						// 根据Alibaba的Java开发手册，IN后边的集合元素数量控制在1000个之内
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						filterParameterString.append(" IN (");
						// 参数值
						for (int i = 0; i < filterParameterValue.length; i++) {
							filterParameterString.append(
									filterParameterValue[i] + (i == filterParameterValue.length - 1 ? "" : ","));
						}
						filterParameterString.append(")");
					} else {
						// 超过部分分成多个IN
						int countOfIN = filterParameterValue.length / 1000
								+ (filterParameterValue.length % 1000 == 0 ? 0 : 1);
						for (int i = 0; i < countOfIN; i++) {
							filterParameterString.append((i == 0 ? "" : " OR "));
							// 参数名
							filterParameterString.append(queryParameter.getKey());
							filterParameterString.append(" IN (");

							int jEnd = 1000;
							if (i == countOfIN - 1)
								jEnd = filterParameterValue.length - i * 1000;

							for (int j = 0; j < jEnd; j++) {
								filterParameterString
										.append(filterParameterValue[i * 1000 + j] + (j == jEnd - 1 ? "" : ","));
							}

							filterParameterString.append(")");
						}
					}
				} else {
					// 非全数字类型

					if (filterParameterValue.length <= 1000) {
						// 根据Alibaba的Java开发手册，IN后边的集合元素数量控制在1000个之内
						// 参数名
						filterParameterString.append(queryParameter.getKey());
						filterParameterString.append(" IN (");
						// 参数值
						for (int i = 0; i < filterParameterValue.length; i++) {
							String filterParameterValueSlice = filterParameterValue[i].replace("'", "\'");

							filterParameterString.append(
									filterParameterValueSlice + (i == filterParameterValue.length - 1 ? "" : ","));
						}
						filterParameterString.append(")");
					} else {
						// 超过部分分成多个IN
						int countOfIN = filterParameterValue.length / 1000
								+ (filterParameterValue.length % 1000 == 0 ? 0 : 1);
						for (int i = 0; i < countOfIN; i++) {
							filterParameterString.append((i == 0 ? "" : " OR "));
							// 参数名
							filterParameterString.append(queryParameter.getKey());
							filterParameterString.append(" IN (");

							int jEnd = 1000;
							if (i == countOfIN - 1)
								jEnd = filterParameterValue.length - i * 1000;

							for (int j = 0; j < jEnd; j++) {
								String filterParameterValueSlice = filterParameterValue[i * 1000 + j].replace("'",
										"\'");

								filterParameterString.append(filterParameterValueSlice + (j == jEnd - 1 ? "" : ","));
							}

							filterParameterString.append(")");
						}
					}
				}
			}

			// 参数的总右括号
			filterParameterString.append(")");
		}

		return filterParameterString.toString();
	}

	/**
	 * 构造模糊查询参数
	 * 
	 * @param queryParameterMap
	 * @return
	 * @throws Exception
	 */
	public static String buildFuzzyLikeQueryParameterString(Map<String, String> queryParameterMap) throws Exception {
		StringBuilder filterParameterString = new StringBuilder();
		for (Map.Entry<String, String> queryParameter : queryParameterMap.entrySet()) {
			// 永远加上" AND"
			filterParameterString.append(" AND");

			// 参数的总左括号
			filterParameterString.append(" (");

			// 构造此参数查询从句
			// API已排除空参数值，无值的参数，此处不需要再校验参数
			// 日期类型参数只接受yyyy-MM-dd格式参数值
			// 不支持非日期类型的区间查询
			String filterParameterValue = queryParameter.getValue();

			// 参数名
			filterParameterString.append(queryParameter.getKey());
			// 参数值
			filterParameterValue = filterParameterValue.replace("'", "\'");
			filterParameterString.append(" LIKE '%" + filterParameterValue + "%'");

			// 参数的总右括号
			filterParameterString.append(")");
		}

		return filterParameterString.toString();
	}

	public static String buildAccurateDateTimeRangeQueryParameterString(Map<String, String[]> accurateDateTimeRange) {

			return null;
	}

	/**
	 * 构造 where 语句
	 * 
	 * @return
	 */
	public static SQLWherePhrase buildSimpleWherePhrase(List<SQLPhrase> params) {
		SQLWherePhrase phrase = new SQLWherePhrase();
		StringBuilder wherePhrase = new StringBuilder();
		List<Object> paramsList = new ArrayList<>();

		if (params != null && params.size() > 0) {
			wherePhrase.append(" WHERE 1=1 ");
		} else {
			return phrase;
		}

		params.forEach(param -> {
			wherePhrase.append(" AND ").append(param.toString());
			Object[] values = param.getValues();
			paramsList.addAll(Arrays.asList(values));
		});


		phrase.setParams(paramsList);
		phrase.setWherePhrase(wherePhrase.toString());

		return phrase;
	}

	/**
	 * 构造分页字句
	 * 
	 * @param pageable
	 * @return
	 */
	public static String buildeSimplePagePhrase(Pageable pageable) {
		StringBuilder pagePhrase = new StringBuilder();
		if (pageable != null) {
			long offset = pageable.getOffset();
			int pageSize = pageable.getPageSize();
			Sort sort = pageable.getSort();
			if (sort != null) {
				StringBuilder sortPhrase = new StringBuilder(" ORDER BY ");
				sort.iterator().forEachRemaining(order -> {
					String property = order.getProperty();
					Sort.Direction direction = order.getDirection();
					sortPhrase.append(" ").append(property).append(" ").append(direction.toString()).append(",");
				});
				pagePhrase.append(" ").append(sortPhrase.subSequence(0, sortPhrase.length() - 1));
			}
			pagePhrase.append(" LIMIT ").append(offset).append(",").append(pageSize);
		}
		return pagePhrase.toString();
	}




	public static class SQLWherePhrase {
		private String wherePhrase;
		private List<Object> params;

		public String getWherePhrase() {
			return wherePhrase;
		}

		public void setWherePhrase(String wherePhrase) {
			this.wherePhrase = wherePhrase;
		}

		public List<Object> getParams() {
			return params;
		}

		public void setParams(List<Object> params) {
			this.params = params;
		}
	}

	public static Integer objectToInt(Object object) {
		Integer result = null;
		String str = StringUtils.object2String(object);
		if (StringUtils.isNotNullNorEmpty(str)) {
			try {
				result = Integer.parseInt(str);
			} catch (Exception e) {
				// Ignore
			}
		}
		
		return result;
	}
	
	public static Long objectToLong(Object object) {
		Long result = null;
		String str = StringUtils.object2String(object);
		if (StringUtils.isNotNullNorEmpty(str)) {
			try {
				result = Long.parseLong(str);
			} catch (Exception e) {
				// Ignore
			}
		}
		
		return result;
	}
	
	public static BigDecimal objectToBigDecimal(Object object) {
		BigDecimal result = null;
		String str = StringUtils.object2String(object);
		if (StringUtils.isNotNullNorEmpty(str)) {
			try {
				result = new BigDecimal(str);
			} catch (Exception e) {
				// Ignore
			}
		}
		
		return result;
	}

}
