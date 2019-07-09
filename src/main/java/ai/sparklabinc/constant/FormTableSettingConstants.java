package ai.sparklabinc.constant;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/9 10:45
 * @description:
 * @version: V1.0
 */
public interface FormTableSettingConstants {
    /**
     * 表单类型
     */
     enum FormType {
        TEXT,
        DATE_RANGE,
        NUMBER
    }

    /**
     * 字段排序
     */
     enum OrderBy {
        DESC,
        ASC,
        NONE
    }
}

