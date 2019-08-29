package io.g740.d1.util;

import io.g740.d1.entity.DfFormTableSettingDO;
import io.g740.d1.exception.custom.IllegalParameterException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : Kingzer
 * @date : 2019-07-04 14:50
 * @description :
 */
public class D1SQLUtils {
    private D1SQLUtils() {
    }

    public static void buildFuzzyLikeQueryParameterString(Map<String, String> fuzzyLike, SqlConditions sqlConditions, List<DfFormTableSettingDO> dfFormTableSettingDOS) throws IllegalParameterException {

        for (Map.Entry<String, String> paramterEntry : fuzzyLike.entrySet()) {
            String parameter = paramterEntry.getKey();
            String parameterVal = paramterEntry.getValue();
            if(StringUtils.isNotNullNorEmpty(parameterVal)){
                List<String> collect = dfFormTableSettingDOS.stream()
                        .filter(e -> e.getDbFieldName().equalsIgnoreCase(parameter))
                        .map(DfFormTableSettingDO::getDbFieldType)
                        .collect(Collectors.toList());
                if(collect.isEmpty()){
                   throw new IllegalParameterException("parameter name is not exist in from table setting");
                }
                sqlConditions.createLikeCondions(parameter, parameterVal,collect.get(0));
            }
        }

    }

    public static void buildAccurateEqualsStringQueryParameterString(Map<String, String> accurateEqualsString, SqlConditions sqlConditions, List<DfFormTableSettingDO> dfFormTableSettingDOS) throws IllegalParameterException {
        for (Map.Entry<String, String> paramterEntry : accurateEqualsString.entrySet()) {
            String parameter = paramterEntry.getKey();
            String parameterVal = paramterEntry.getValue();
            if(StringUtils.isNotNullNorEmpty(parameterVal)) {
                List<String> collect = dfFormTableSettingDOS.stream()
                        .filter(e -> e.getDbFieldName().equalsIgnoreCase(parameter))
                        .map(DfFormTableSettingDO::getDbFieldType)
                        .collect(Collectors.toList());
                if (collect.isEmpty()) {
                    throw new IllegalParameterException("parameter name is not exist in from table setting");
                }
                sqlConditions.createEqualCondition(parameter, parameterVal,collect.get(0));
            }
        }
    }


    public static void buildAccurateInStringQueryParameterString(Map<String, String[]> accurateInString, SqlConditions sqlConditions, List<DfFormTableSettingDO> dfFormTableSettingDOS) throws IllegalParameterException {
        for (Map.Entry<String, String[]> parameterEntry : accurateInString.entrySet()) {
            String parameter = parameterEntry.getKey();
            String[] parameterValArr = parameterEntry.getValue();

            if(parameterValArr != null && parameterValArr.length >0) {
                List<String> collect = dfFormTableSettingDOS.stream()
                        .filter(e -> e.getDbFieldName().equalsIgnoreCase(parameter))
                        .map(DfFormTableSettingDO::getDbFieldType)
                        .collect(Collectors.toList());
                if (collect.isEmpty()) {
                    throw new IllegalParameterException("parameter name is not exist in from table setting");
                }
                sqlConditions.createInOneFieldAndMultipleValue(parameter, parameterValArr,collect.get(0));
            }
        }
    }



    public static void buildAccurateDateRangeQueryParameterString(Map<String, String[]> accurateDateRange, SqlConditions sqlConditions, List<DfFormTableSettingDO> dfFormTableSettingDOS) throws IllegalParameterException {
        for (Map.Entry<String, String[]> parameterEntry : accurateDateRange.entrySet()) {
            String parameter = parameterEntry.getKey();
            String[] parameterValArr = parameterEntry.getValue();

            // 前面设定了只要有值肯定是两个值
            if(parameterValArr.length == 2){
                String parameterVal0 = parameterValArr[0];
                String parameterVal1 = parameterValArr[1];
                if(parameterVal0 != null){
                    parameterVal0 = parameterVal0 + " 00:00:00";
                }
                if(parameterVal1 != null){
                    parameterVal1 = parameterVal1 + " 23:59:59";
                }
                List<String> collect = dfFormTableSettingDOS.stream()
                        .filter(e -> e.getDbFieldName().equalsIgnoreCase(parameter))
                        .map(DfFormTableSettingDO::getDbFieldType)
                        .collect(Collectors.toList());
                if (collect.isEmpty()) {
                    throw new IllegalParameterException("parameter name is not exist in from table setting");
                }
                sqlConditions.createRangeCondition(parameter, parameterVal0, parameterVal1,collect.get(0));
            }
        }
    }


    public static void buildAccurateDateTimeRangeQueryParameterString(Map<String, String[]> accurateDateTimeRange, SqlConditions sqlConditions, List<DfFormTableSettingDO> dfFormTableSettingDOS) throws IllegalParameterException {
        for (Map.Entry<String, String[]> parameterEntry : accurateDateTimeRange.entrySet()) {
            String parameter = parameterEntry.getKey();
            String[] parameterValArr = parameterEntry.getValue();
            // 前面设定了只要有值肯定是两个值
            if(parameterValArr.length == 2){
                List<String> collect = dfFormTableSettingDOS.stream()
                        .filter(e -> e.getDbFieldName().equalsIgnoreCase(parameter))
                        .map(DfFormTableSettingDO::getDbFieldType)
                        .collect(Collectors.toList());
                if (collect.isEmpty()) {
                    throw new IllegalParameterException("parameter name is not exist in from table setting");
                }
                sqlConditions.createRangeCondition(parameter, parameterValArr[0], parameterValArr[1],collect.get(0));
            }
        }
    }

    public static void buildAccurateNumberRangeQueryParameterString(Map<String, String[]> accurateNumberRange, SqlConditions sqlConditions, List<DfFormTableSettingDO> dfFormTableSettingDOS) throws IllegalParameterException {
        for (Map.Entry<String, String[]> parameterEntry : accurateNumberRange.entrySet()) {
            String parameter = parameterEntry.getKey();
            String[] parameterValArr = parameterEntry.getValue();
            // 前面设定了只要有值肯定是两个值
            if(parameterValArr.length == 2){
                List<String> collect = dfFormTableSettingDOS.stream()
                        .filter(e -> e.getDbFieldName().equalsIgnoreCase(parameter))
                        .map(DfFormTableSettingDO::getDbFieldType)
                        .collect(Collectors.toList());
                if (collect.isEmpty()) {
                    throw new IllegalParameterException("parameter name is not exist in from table setting");
                }
                sqlConditions.createRangeCondition(parameter, parameterValArr[0], parameterValArr[1],collect.get(0));
            }
        }
    }

    public static void buildHasNullOrEmptyParameterString(Map<String, String[]> hasNullOrEmptyParameterMap, SqlConditions sqlConditions, List<DfFormTableSettingDO> dfFormTableSettingDOS) throws IllegalParameterException {
        for (Map.Entry<String, String[]> parameterEntry : hasNullOrEmptyParameterMap.entrySet()) {
            String parameter = parameterEntry.getKey();
            String[] parameterValArr = parameterEntry.getValue();


            List<String> collect = dfFormTableSettingDOS.stream()
                    .filter(e -> e.getDbFieldName().equalsIgnoreCase(parameter))
                    .map(DfFormTableSettingDO::getDbFieldType)
                    .collect(Collectors.toList());
            if (collect.isEmpty()) {
                throw new IllegalParameterException("parameter name is not exist in from table setting");
            }
            // 这里parameterValArr可以为null或者孔
            sqlConditions.createInOneFieldAndMultipleAndHasNullOrEmptyValue(parameter, parameterValArr,collect.get(0));

        }

    }
}
