package ai.sparklabinc.d1.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Kingzer
 * @date : 2019-07-03 13:53
 * @description : d1 数据库操作中会使用的一些常量
 */
public interface DsConstants {

    enum FormFieldQueryTypeEnum{
        SINGLE_DATE("SINGLE_DATE"),
        DATE_RANGE("DATE_RANGE"),
        DATE_TIME_RANGE("DATE_TIME_RANGE"),
        SINGLE_CHOICE_LIST("SINGLE_CHOICE_LIST"),
        MULTIPLE_CHOICE_LIST("MULTIPLE_CHOICE_LIST"),


//        暂时组件没提供，不考虑RADIOBOX_CHOICE   CHECKBOX_CHOICE
        RADIOBOX_CHOICE("RADIOBOX_CHOICE"),
        CHECKBOX_CHOICE("CHECKBOX_CHOICE"),


        //精确查询文本类型
        EXACT_MATCHING_TEXT("EXACT_MATCHING_TEXT"),
        //模糊查询文本类型
        FUZZY_MATCHING_TEXT("FUZZY_MATCHING_TEXT"),
        NUMBER("NUMBER"),
        NUMBER_RANGE("NUMBER_RANGE"),

        SWITCH("SWITCH"),
        SINGLE_CHOICE_LIST_W_EMPTY("SINGLE_CHOICE_LIST_W_EMPTY"),
        NULL_VALUE("NULL_VALUE"),
        EMPTY_VALUE("EMPTY_VALUE"),
        SEGMENTATION_TEXT("SEGMENTATION_TEXT"),
        AUTO_COMPLETION("AUTO_COMPLETION");



        private String val;
        public String getVal(){
            return val;
        }
        FormFieldQueryTypeEnum(String val) {
            this.val = val;
        }


        public static FormFieldQueryTypeEnum getFormFieldQueryTypeEnumByVal(String val){
            for (FormFieldQueryTypeEnum fieldQueryTypeEnum : values()) {
                if(fieldQueryTypeEnum.getVal().equals(val)){
                    return fieldQueryTypeEnum;
                }
            }
            return null;
        }

        /**
         * 下拉框的类型；这里用于判断；可以获取字典值
         */
        public static List<String> getChoiceList(){
            List<String> choiceList = new ArrayList<>();
            choiceList.add(SINGLE_CHOICE_LIST.getVal());
            choiceList.add(MULTIPLE_CHOICE_LIST.getVal());
            choiceList.add(SINGLE_CHOICE_LIST_W_EMPTY.getVal());
            return choiceList;
        }

        /**
         * 获取所有类型；主要用于查询时，如果不是这些类型使用精确查询
         */
        public static List<String> getAllTypeValList(){
            List<String> allTypeValList = new ArrayList<>();
            for (FormFieldQueryTypeEnum typeEnum : values()) {
                allTypeValList.add(typeEnum.getVal());
            }
            return allTypeValList;
        }


        /**
         * 获取所有类型；主要用于查询时，如果不是这些类型使用精确查询
         */
        public static List<String> getRangeTypeValList(){
            List<String> rangeTypeList = new ArrayList<>();
            rangeTypeList.add(DATE_RANGE.getVal());
            rangeTypeList.add(DATE_TIME_RANGE.getVal());
            rangeTypeList.add(NUMBER_RANGE.getVal());
            return rangeTypeList;
        }
    }


    /**
     * Range类型时可能会传两个参数，会使用这些后缀进行拼接，注意做响应判断
     */
    enum RangeFieldSuffixEnum {
        SUFFIX_START("__start__"),
        SUFFIX_END("__end__");
        private String val;
        public String getVal(){
            return val;
        }
        RangeFieldSuffixEnum(String val) {
            this.val = val;
        }
    }


    String urlSuffix = "?useUnicode=true&characterEncoding=UTF-8";

}
