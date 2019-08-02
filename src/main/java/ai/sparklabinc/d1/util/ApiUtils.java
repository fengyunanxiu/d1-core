package ai.sparklabinc.d1.util;

import org.springframework.data.domain.Pageable;

import java.util.*;

public class ApiUtils {
	private ApiUtils() {

	}

	private static final String KEY_SPLITITOR = "#";


	/**
	 * URL参数精确保留字 Spring默认使用page和size支持分页，使用sort=name&name.dir=desc形式支持排序.
	 * "token"作为access token参数
	 */
	public static final List<String> RESERVED_QUERY_PARAMETER_NAME_LIST = new ArrayList<String>(
			Arrays.asList("page", "size", "sort", "token","apiKey","sign","t", "api_key", "timestamp", "nonce_str", "data_source_key","moreWhereClauses","more_where_clauses"));


	/**
	 * URL参数模糊保留字 Spring默认使用sort=name&name.dir=desc形式支持排序
	 */
	public static final String RESERVED_QUERY_PARAMETER_NAME_REGEX_PATTERN = "^[a-zA-Z]{1}[a-zA-Z0-9_]*\\.dir$";

	public static Map<String, String[]> removeReservedParameters(Map<String, String[]> originalParameterMap) {
		Map<String, String[]> copyParameterMap = new HashMap<String, String[]>();
		for (Map.Entry<String, String[]> entry : originalParameterMap.entrySet()) {
			// 排除URL参数保留字
			if (!RESERVED_QUERY_PARAMETER_NAME_LIST.contains(entry.getKey())
					&& !entry.getKey().matches(RESERVED_QUERY_PARAMETER_NAME_REGEX_PATTERN)) {

				// 排除无值的参数
				if (entry.getValue() != null && entry.getValue().length > 0) {

					List<String> validValues = new ArrayList<String>();
					for (String value : entry.getValue()) {
						// 排除参数值为空的值
						if (StringUtils.isNotNullNorEmpty(value)) {
							validValues.add(value.trim());
						}
					}

					if (validValues.size() > 0) {
						String[] validValueArray = validValues.toArray(new String[validValues.size()]);
						copyParameterMap.put(entry.getKey(), validValueArray);
					}
				}
			}
		}

		return copyParameterMap;
	}
	/**
	 * 这是一个保险的方法，查询时如果没有分页参数，使用pageable默认的分页，如果有分页参数，也不会影响结果
	 * @param requestParameterMap
	 */
	public static Map<String, String[]> restructurePage2Parameter(Map<String, String[]> requestParameterMap, Pageable pageable) {
		Map<String, String[]> parameterMap = new LinkedHashMap<>();
		//先copy map的值
		for(Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
			parameterMap.put(entry.getKey(), entry.getValue());
		}
		long offset = pageable.getOffset();
		int size = pageable.getPageSize();
		long page = offset / size;
		parameterMap.put("page", new String[] {page + ""} );
		parameterMap.put("size", new String[] {size + ""});
		//sort，暂时还没有需求需要控制
		
		return parameterMap;
	}


	public static Map<String, String[]> restructureParameter(Map<String, String[]> requestParameterMap) {
		Map<String, String[]> parameterMap = new LinkedHashMap<>();
		//先copy map的值
		for(Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
			parameterMap.put(entry.getKey(), entry.getValue());
		}
		return parameterMap;
	}




}
