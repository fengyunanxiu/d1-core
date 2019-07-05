package ai.sparklabinc.util;

import java.util.List;
import java.util.Map;

/**
 * @author : Kingzer
 * @date : 2019-07-04 14:50
 * @description :
 */
public class D1SQLUtils {
    private D1SQLUtils() {
    }

    public static void buildFuzzyLikeQueryParameterString(Map<String, String> fuzzyLike, SqlConditions sqlConditions) {

        for (Map.Entry<String, String> paramterEntry : fuzzyLike.entrySet()) {
            String parameter = paramterEntry.getKey();
            String parameterVal = paramterEntry.getValue();
            if(StringUtils.isNotNullNorEmpty(parameterVal)){
                sqlConditions.createLikeCondions(parameter, parameterVal);
            }
        }

    }

    public static void buildAccurateEqualsStringQueryParameterString(Map<String, String> accurateEqualsString, SqlConditions sqlConditions) {
        for (Map.Entry<String, String> paramterEntry : accurateEqualsString.entrySet()) {
            String parameter = paramterEntry.getKey();
            String parameterVal = paramterEntry.getValue();
            if(StringUtils.isNotNullNorEmpty(parameterVal)){
                sqlConditions.createEqualCondition(parameter, parameterVal);
            }
        }

    }

    public static void buildAccurateInStringQueryParameterString(Map<String, String[]> accurateInString, SqlConditions sqlConditions) {
        for (Map.Entry<String, String[]> parameterEntry : accurateInString.entrySet()) {
            String parameter = parameterEntry.getKey();
            String[] parameterValArr = parameterEntry.getValue();

            if(parameterValArr != null && parameterValArr.length >0) {
                sqlConditions.createInOneFieldAndMultipleValue(parameter, parameterValArr);
            }
        }
    }

    public static void buildAccurateDateRangeQueryParameterString(Map<String, String[]> accurateDateRange, SqlConditions sqlConditions) {
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
                sqlConditions.createRangeCondition(parameter, parameterVal0, parameterVal1);
            }
        }
    }


    public static void buildAccurateDateTimeRangeQueryParameterString(Map<String, String[]> accurateDateTimeRange, SqlConditions sqlConditions) {
        for (Map.Entry<String, String[]> parameterEntry : accurateDateTimeRange.entrySet()) {
            String parameter = parameterEntry.getKey();
            String[] parameterValArr = parameterEntry.getValue();
            // 前面设定了只要有值肯定是两个值
            if(parameterValArr.length == 2){
                sqlConditions.createRangeCondition(parameter, parameterValArr[0], parameterValArr[1]);
            }
        }
    }

    public static void buildAccurateNumberRangeQueryParameterString(Map<String, String[]> accurateNumberRange, SqlConditions sqlConditions) {
        for (Map.Entry<String, String[]> parameterEntry : accurateNumberRange.entrySet()) {
            String parameter = parameterEntry.getKey();
            String[] parameterValArr = parameterEntry.getValue();
            // 前面设定了只要有值肯定是两个值
            if(parameterValArr.length == 2){
                sqlConditions.createRangeCondition(parameter, parameterValArr[0], parameterValArr[1]);
            }
        }
    }
}
