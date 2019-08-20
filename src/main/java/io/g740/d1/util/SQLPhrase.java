package io.g740.d1.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author: zhangxw
 * @date: 2018/1/24 12:42
 */
public class SQLPhrase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SQLPhrase.class);

	private String columnFields;

	private SQLExpress sqlExpress;

	private Object[] values;

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
	private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:dd");

	/**
	 * 此匹配并不精确
	 */
	private static final Pattern datePattern = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$");
	/**
	 * 此匹配并不精确
	 */
	private static final Pattern dateTimePattern = Pattern
			.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$");

	private SQLPhrase(String columnFields, SQLExpress sqlExpress, Object[] values) {
		this.columnFields = columnFields;
		this.sqlExpress = sqlExpress;
		this.values = values;
	}

	public enum SQLExpress {
		// AND(" AND "),
		// OR(" OR "),
		IN(" IN ( %s ) "), NOT_IN(" NOT IN ( %s ) "), EQUAL(" = ? "), NOT_EQUAL(" <> ? "), LT(" < ? "), LTE(" <= ? "), GT(
				" > ? "), GTE(" >= ? "), LIKE(" like ? "), BETWEEN_AND(" BETWEEN ? AND ? ");

		private String symbol;

		SQLExpress(String symbol) {
			this.symbol = symbol;
		}
	}

	@Override
	public String toString() {
		StringBuilder sql = new StringBuilder()
				.append(" (")
				.append(columnFields)
				.append(sqlExpress.symbol)
				.append(") ");

		if (sqlExpress.equals(SQLExpress.IN) || sqlExpress.equals(SQLExpress.NOT_IN)) {
			StringBuilder paramsSB = new StringBuilder();
			for (int i = 0; i < values.length; i++) {
				if (i > 0) {
					paramsSB.append(",");
				}
				paramsSB.append(" ? ");
			}
			String result = String.format(sql.toString(), paramsSB.toString());
			LOGGER.info(result);
			return result;
		}
		return sql.toString();
	}

	public String getColumnFields() {
		return columnFields;
	}

	public SQLExpress getSqlExpress() {
		return sqlExpress;
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public static class SQLPhraseBuilder {

		private String schema;

		private String fieldKey;

		private SQLExpress sqlExpress;

		private Object[] values;

		private List<SQLPhrase> sqlPhraseList;

		public static SQLPhraseBuilder create() {
			SQLPhraseBuilder builder = new SQLPhraseBuilder();
			return builder;
		}

		public SQLPhraseBuilder withSchema(String schema) {
			this.schema = schema;
			return this;
		}

		public SQLPhraseBuilder withFieldKey(String fieldKey) {
			this.fieldKey = fieldKey;
			return this;
		}

		public SQLPhraseBuilder withSQLExpress(SQLExpress sqlExpress) {
			this.sqlExpress = sqlExpress;
			return this;
		}

		public SQLPhraseBuilder withValues(Object[] values) {
			this.values = values;
			return this;
		}

		public List<SQLPhrase> buildList() {
			if (this.sqlPhraseList == null) {
				this.sqlPhraseList = new ArrayList<>();
			}
			if (fieldKey == null || sqlExpress == null || values == null) {
				return this.sqlPhraseList;
			}
			this.sqlPhraseList.add(new SQLPhrase(this.fieldKey, this.sqlExpress, this.values));
			return this.sqlPhraseList;
		}

		public SQLPhrase build() throws Exception {
			if (fieldKey == null || sqlExpress == null || values == null) {
				throw new Exception("fieldkey or sqlexpress or values should not be null");
			}
			return new SQLPhrase(this.fieldKey, this.sqlExpress, this.values);
		}

//		public List<SQLPhrase> buildList(String fieldKey, FieldTypeEnum fieldTypeEnum,
//				FieldQueryFormTypeEnum fieldQueryFormType, String[] values) {
//			List<SQLPhrase> result = new ArrayList<>();
//			if (values == null || values.length <= 0) {
//				return result;
//			}
//
//			if (this.schema != null) {
//				fieldKey = schema + "." + fieldKey;
//			}
//
//			switch (fieldTypeEnum) {
//			case DATE: {
//				if (values.length > 0) {
//					if (DateUtils.isDate(values[0])) {
//						Date date = DATE_FORMAT.parseDateTime(values[0]).toDate();
//						result.add(new SQLPhrase(fieldKey, SQLExpress.EQUAL, new Date[] { date }));
//					} else {
//						Date date = DATETIME_FORMAT.parseDateTime(values[0]).toDate();
//						result.add(new SQLPhrase(fieldKey, SQLExpress.EQUAL, new Date[] { date }));
//					}
//				}
//				break;
//			}
//			case DATE_RANGE: {
//				if (values.length == 1) {
//					if (DateUtils.isDate(values[0])) {
//						Date date = DATE_FORMAT.parseDateTime(values[0]).toDate();
//						result.add(new SQLPhrase(fieldKey, SQLExpress.EQUAL, new Date[] { date }));
//					} else {
//						Date date = DATETIME_FORMAT.parseDateTime(values[0]).toDate();
//						result.add(new SQLPhrase(fieldKey, SQLExpress.EQUAL, new Date[] { date }));
//					}
//				} else if (values.length > 1) {
//					Date date1;
//					Date date2;
//					if (DateUtils.isDate(values[0])) {
//						date1 = DATE_FORMAT.parseDateTime(values[0]).toDate();
//						date2 = DATE_FORMAT.parseDateTime(values[1]).toDate();
//					} else {
//						date1 = DATETIME_FORMAT.parseDateTime(values[0]).toDate();
//						date2 = DATETIME_FORMAT.parseDateTime(values[1]).toDate();
//					}
//					Date[] dates = new Date[2];
//					if (date1.getTime() < date2.getTime()) {
//						dates[0] = date1;
//						dates[1] = date2;
//					} else {
//						dates[0] = date2;
//						dates[1] = date1;
//					}
//					result.add(new SQLPhrase(fieldKey, SQLExpress.GTE, new Date[] { dates[0] }));
//					result.add(new SQLPhrase(fieldKey, SQLExpress.LTE, new Date[] { dates[1] }));
//				}
//				break;
//			}
//			case TEXT:
//				if (values.length > 1) {
//					result.add(new SQLPhrase(fieldKey, SQLExpress.IN, values));
//				} else {
//					result.add(new SQLPhrase(fieldKey, SQLExpress.LIKE, new String[] { "%" + values[0] + "%" }));
//				}
//				break;
//			case NUMBER:
//				if (values.length > 1) {
//					result.add(new SQLPhrase(fieldKey, SQLExpress.IN, values));
//				} else {
//					result.add(new SQLPhrase(fieldKey, SQLExpress.LIKE, new String[] { "%" + values[0] + "%" }));
//				}
//				break;
//			case NUMBER_RANGE: {
//				if (values.length > 1) {
//					Long v1 = Long.valueOf(values[0]);
//					Long v2 = Long.valueOf(values[1]);
//					Long[] numbers = new Long[2];
//					if (v1 > v2) {
//						numbers[0] = v1;
//						numbers[1] = v2;
//					} else {
//						numbers[0] = v2;
//						numbers[1] = v1;
//					}
//
//					result.add(new SQLPhrase(fieldKey, SQLExpress.GT, new Long[] { numbers[0] }));
//					result.add(new SQLPhrase(fieldKey, SQLExpress.LT, new Long[] { numbers[1] }));
//				}
//				break;
//			}
//			case CHOICE_LIST:
//				result.add(new SQLPhrase(fieldKey, SQLExpress.IN, values));
//				break;
//			case VARCHAR:
//				if (values.length > 1) {
//					result.add(new SQLPhrase(fieldKey, SQLExpress.IN, values));
//				} else {
//					result.add(new SQLPhrase(fieldKey, SQLExpress.LIKE, new String[] { "%" + values[0] + "%" }));
//				}
//				break;
//			case INTERSECTION_DATE: {
//				// TODO 存在问题
//				/*
//				 * if (values.length > 1) { Date date1 = null; Date date2 = null; if
//				 * (DateUtils.isDate(values[0])) { date1 =
//				 * DATE_FORMAT.parseDateTime(values[0]).toDate(); date2 =
//				 * DATE_FORMAT.parseDateTime(values[1]).toDate(); } else { date1 =
//				 * DATETIME_FORMAT.parseDateTime(values[0]).toDate(); date2 =
//				 * DATETIME_FORMAT.parseDateTime(values[1]).toDate(); } Date[] dates = new
//				 * Date[2]; if (date1.getTime() < date2.getTime()) { dates[0] = date1; dates[1]
//				 * = date2; } else { dates[0] = date2; dates[1] = date1; } result.add(new
//				 * SQLPhrase(fieldKey, SQLExpress.LTE, new Date[]{dates[1]})); result.add(new
//				 * SQLPhrase(fieldKey, SQLExpress.GTE, new Date[]{dates[0]})); }
//				 */
//				break;
//			}
//			default:
//				break;
//			}return result;
//	}

	}

	public static void main(String[] args) {
		SQLPhrase sqlPhrase = new SQLPhrase("test", SQLExpress.EQUAL, new Object[] { 1231 });
		System.out.println(sqlPhrase);
	}
}
