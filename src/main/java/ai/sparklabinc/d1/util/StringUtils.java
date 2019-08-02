package ai.sparklabinc.d1.util;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

public class StringUtils {
	private StringUtils() {

	}

	// 匹配整数
	private static final String INTEGER_REGEX_PATTERN = "^-?[0-9]\\d*$";

	// 匹配数字mmm.nnn
	private static final String NUMBER_REGEX_PATTERN = "^[0-9]+\\.?[0-9]*$";

	// 匹配日期yyyy-MM-dd
	private static final String DATE_REGEX_PATTERN = "^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))$";

	public static boolean isNotNullNorEmpty(String source) {
		if (source == null)
			return false;

		if (source.trim().isEmpty())
			return false;

		return true;
	}

	public static boolean isNullOrEmpty(String source) {
		if (source == null)
			return true;

		if (source.trim().isEmpty())
			return true;

		return false;
	}

	public static boolean isInteger(String source) {
		return source.matches(INTEGER_REGEX_PATTERN);
	}

	public static boolean isNumber(String source) {
		if (isNotNullNorEmpty(source)) {
			return source.matches(NUMBER_REGEX_PATTERN);
		}
		return false;
	}

	public static boolean isDate(String source) {
		return source.matches(DATE_REGEX_PATTERN);
	}

	/**
	 * 驼峰拼写转换成下划线拼写
	 *
	 * @param source
	 * @return
	 */
	public static String camelToUnderline(String source) {
		StringBuilder dest = new StringBuilder();
		for (int i = 0; i < source.length(); i++) {
			char ch = source.charAt(i);
			if (ch >= 'A' && ch <= 'Z') {
				if (i == 0) {
					dest.append(String.valueOf(ch));
				} else {
					char previousCh = source.charAt(i - 1);
					if (previousCh >= 'A' && previousCh <= 'Z') {
						// 连续大写
						dest.append(String.valueOf(ch));
					} else {
						dest.append('_' + String.valueOf(ch));
					}
				}
			} else {
				dest.append(String.valueOf(ch));
			}
		}

		return dest.toString().toLowerCase();
	}

	public static String object2String(Object object) {
		if (object == null) {
			return "";
		}

		return String.valueOf(object);
	}

	public static String object2StringWithDefault(Object object, String defaultValue) {
		if (object == null) {
			return defaultValue;
		}
		return String.valueOf(object);
	}

	public static <T> String collection2StringWithComma(Collection<T> collection, Function<T, String> toStringFunction) {
		if (collection.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		collection.stream().forEachOrdered(tmp -> sb.append(toStringFunction.apply(tmp)).append(","));
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 去掉前后不可见字符
	 *
	 * @param source
	 * @return
	 */
	public static String removeNonPrintableCharactersFromStartAndEnd(String source) {
		if (isNullOrEmpty(source))
			return source;

		// 先去掉前后空白字符
		source = removeWhitespaceCharactersFromStartAndEnd(source);

		// 再去掉前后不可见字符
		char[] elements = source.toCharArray();
		int start = 0;
		for (; start < elements.length; start++) {
			char element = elements[start];
			if (!Character.isSpaceChar(element) && Character.isDefined(element)) {
				break;
			}
		}

		int end = elements.length - 1;
		for (; end >= start; end--) {
			char element = elements[end];
			if (!Character.isSpaceChar(element) && Character.isDefined(element)) {
				break;
			}
		}

		return source.substring(start, end + 1);
	}

	/**
	 * 去掉前后空白字符
	 *
	 * @param source
	 * @return
	 */
	public static String removeWhitespaceCharactersFromStartAndEnd(String source) {
		if (isNullOrEmpty(source))
			return source;

		source = source.trim();
		char[] elements = source.toCharArray();

		// 去掉前后空白符号
		// \f -> 匹配一个换页
		// \n -> 匹配一个换行符
		// \r -> 匹配一个回车符
		// \t -> 匹配一个制表符
		// \v -> 匹配一个垂直制表符
		int start = 0;
		for (; start < elements.length; start++) {
			char element = elements[start];
			if (!Character.toString(element).matches("\\s+")) {
				break;
			}
		}

		int end = elements.length - 1;
		for (; end >= start; end--) {
			char element = elements[end];
			if (!Character.toString(element).matches("\\s+")) {
				break;
			}
		}

		return source.substring(start, end + 1);
	}

	public static String splitMd5ResultLikeUUID(String md5Str) {
		StringBuilder sb = new StringBuilder(md5Str);
		//向指定位置插入-
		sb.insert(8, "-");
		sb.insert(13, "-");
		sb.insert(18, "-");
		sb.insert(23, "-");
		return sb.toString();
	}

	/**
	 * 参数必须是时间戳才能转换
	 *
	 * @param object
	 * @return
	 */
	public static Date long2Date(Object object) {
		return Optional.ofNullable(object).map(Object::toString).map(Long::valueOf).map(Date::new).orElse(null);
	}

}

