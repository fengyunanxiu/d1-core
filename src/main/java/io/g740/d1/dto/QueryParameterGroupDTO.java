package io.g740.d1.dto;

import java.util.Map;

public class QueryParameterGroupDTO {
	// 模糊查询参数"LIKE"
	private Map<String, String> fuzzyLike;
	// 精确查询参数"="字符串
	private Map<String, String> accurateEqualsString;
//	// 精确查询参数"="数字
//	private Map<String, String> accurateEqualsNumber;
	// 精确查询参数"IN"字符串
	private Map<String, String[]> accurateInString;
//	// 精确查询参数"IN"数字
//	private Map<String, String[]> accurateInNumber;
	// 精确查询参数">= yyyy-MM-dd 00:00:00 AND <= yyyy-MM-dd 23:59:59"
	private Map<String, String[]> accurateDateRange;

	private Map<String, String[]> accurateDateTimeRange;

	// 精确查询参数">= AND <="
	private Map<String, String[]> accurateNumberRange;

	//
	Map<String, String[]> hasNullOrEmptyParameterMap;

	public Map<String, String> getFuzzyLike() {
		return fuzzyLike;
	}

	public void setFuzzyLike(Map<String, String> fuzzyLike) {
		this.fuzzyLike = fuzzyLike;
	}

	public Map<String, String> getAccurateEqualsString() {
		return accurateEqualsString;
	}

	public void setAccurateEqualsString(Map<String, String> accurateEqualsString) {
		this.accurateEqualsString = accurateEqualsString;
	}

	public Map<String, String[]> getAccurateInString() {
		return accurateInString;
	}

	public void setAccurateInString(Map<String, String[]> accurateInString) {
		this.accurateInString = accurateInString;
	}



	public Map<String, String[]> getAccurateDateRange() {
		return accurateDateRange;
	}

	public void setAccurateDateRange(Map<String, String[]> accurateDateRange) {
		this.accurateDateRange = accurateDateRange;
	}

	public Map<String, String[]> getAccurateDateTimeRange() {
		return accurateDateTimeRange;
	}

	public void setAccurateDateTimeRange(Map<String, String[]> accurateDateTimeRange) {
		this.accurateDateTimeRange = accurateDateTimeRange;
	}

	public Map<String, String[]> getAccurateNumberRange() {
		return accurateNumberRange;
	}

	public void setAccurateNumberRange(Map<String, String[]> accurateNumberRange) {
		this.accurateNumberRange = accurateNumberRange;
	}

	public Map<String, String[]> getHasNullOrEmptyParameterMap() {
		return hasNullOrEmptyParameterMap;
	}

	public void setHasNullOrEmptyParameterMap(Map<String, String[]> hasNullOrEmptyParameterMap) {
		this.hasNullOrEmptyParameterMap = hasNullOrEmptyParameterMap;
	}
}
