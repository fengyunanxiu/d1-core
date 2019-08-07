package ai.sparklabinc.d1.component;

import ai.sparklabinc.d1.constant.DsConstants;
import ai.sparklabinc.d1.dto.QueryParameterGroupDTO;
import ai.sparklabinc.d1.entity.DfFormTableSettingDO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : Kingzer
 * @date : 2019-07-03 17:32
 * @description :
 */
@Component
public class DfFormTableSettingComponent {


    public QueryParameterGroupDTO transformQueryParameterMap(String dataFacetKey, Map<String, String[]> queryParameterMap, List<DfFormTableSettingDO> dfFormTableSettingDOList) {
        QueryParameterGroupDTO queryParameterGroup = new QueryParameterGroupDTO();

        // 模糊查询参数Map
        Map<String, String> fuzzyLikeQueryParameterMap = new HashMap<>();
        queryParameterGroup.setFuzzyLike(fuzzyLikeQueryParameterMap);

        // 精确等于字符串查询参数Map
        Map<String, String> accurateEqualsStringQueryParameterMap = new HashMap<>();
        queryParameterGroup.setAccurateEqualsString(accurateEqualsStringQueryParameterMap);

//        // 精确等于数字查询参数Map
//        Map<String, String> accurateEqualsNumberQueryParameterMap = new HashMap<>();
//        queryParameterGroup.setAccurateEqualsNumber(accurateEqualsNumberQueryParameterMap);


//        // 精确候选值数字查询参数Map
//        Map<String, String[]> accurateInNumberQueryParameterMap = new HashMap<>();
//        queryParameterGroup.setAccurateInNumber(accurateInNumberQueryParameterMap);

        // 精确数字区间查询
        Map<String, String[]> accurateNumberRangeQueryParameterMap = new HashMap<>();
        queryParameterGroup.setAccurateNumberRange(accurateNumberRangeQueryParameterMap);



        // 精确日期区间查询参数Map
        Map<String, String[]> accurateDateRangeQueryParameterMap = new HashMap<>();
        queryParameterGroup.setAccurateDateRange(accurateDateRangeQueryParameterMap);

        // 精确日期区间查询参数Map
        Map<String, String[]> accurateDateTimeRangeQueryParameterMap = new HashMap<>();
        queryParameterGroup.setAccurateDateRange(accurateDateRangeQueryParameterMap);

        // 精确候选值字符串查询参数Map
        Map<String, String[]> accurateInStringQueryParameterMap = new HashMap<>();
        queryParameterGroup.setAccurateInString(accurateInStringQueryParameterMap);


        for (Map.Entry<String, String[]> entry : queryParameterMap.entrySet()) {
            String frontFieldName = entry.getKey();
            String[] queryParameterValueArray = entry.getValue();

            Optional<DfFormTableSettingDO> optionalDfFormTableSettingDO  = dfFormTableSettingDOList.stream()
                    .filter(item ->  item.getDbFieldName() != null && (frontFieldName.equals(item.getDbFieldName()) || (frontFieldName).equals(item.getDbFieldName() + DsConstants.RangeFieldSuffixEnum.SUFFIX_START.getVal())
                       || (frontFieldName).equals(item.getDbFieldName() + DsConstants.RangeFieldSuffixEnum.SUFFIX_END.getVal()))).findFirst();
            DfFormTableSettingDO dfFormTableSettingDO = null;
            if(optionalDfFormTableSettingDO.isPresent()){
                dfFormTableSettingDO = optionalDfFormTableSettingDO.get();
            }

            String formFieldQueryType = dfFormTableSettingDO.getFormFieldQueryType();
            DsConstants.FormFieldQueryTypeEnum formFieldQueryTypeEnum = DsConstants.FormFieldQueryTypeEnum.getFormFieldQueryTypeEnumByVal(formFieldQueryType);

            //没在query_setting中配置，但出现在查询参数里边（可能是错误，需要注意）
            if(dfFormTableSettingDO == null || formFieldQueryTypeEnum == null){
                if (queryParameterValueArray.length > 1) {
                    accurateInStringQueryParameterMap.put(frontFieldName, queryParameterValueArray);
                } else {
                    accurateEqualsStringQueryParameterMap.put(frontFieldName, queryParameterValueArray[0]);
                }
                // 处理下一个
                continue;
            }


            String fieldName = dfFormTableSettingDO.getDbFieldName();
            switch (formFieldQueryTypeEnum){
                case SINGLE_DATE:
                case SINGLE_CHOICE_LIST:
                case RADIOBOX_CHOICE:{
                    accurateEqualsStringQueryParameterMap.put(fieldName, queryParameterValueArray[0]);
                    break;
                }
                case MULTIPLE_CHOICE_LIST:
                case CHECKBOX_CHOICE:{
                    if (queryParameterValueArray.length == 1) {
                        accurateEqualsStringQueryParameterMap.put(fieldName, queryParameterValueArray[0]);
                    }else{
                        accurateInStringQueryParameterMap.put(fieldName, queryParameterValueArray);
                    }
                    break;
                }


                case TEXT:{
                    Boolean formFieldIsExactly = dfFormTableSettingDO.getFormFieldIsExactly();
                    if(formFieldIsExactly){
                        accurateEqualsStringQueryParameterMap.put(fieldName, queryParameterValueArray[0]);
                    }else{
                        fuzzyLikeQueryParameterMap.put(fieldName,queryParameterValueArray[0]);
                    }
                    break;
                }

                case DATE_RANGE:
                    {
                    String[] rangeArr = accurateDateRangeQueryParameterMap.get(fieldName);
                    if(rangeArr == null){
                        rangeArr = new String[2];
                        accurateDateRangeQueryParameterMap.put(fieldName, rangeArr);
                    }
                    if((fieldName + DsConstants.RangeFieldSuffixEnum.SUFFIX_START.getVal()).equals(frontFieldName)){
                        rangeArr[0] = queryParameterValueArray[0];
                    }else if((fieldName + DsConstants.RangeFieldSuffixEnum.SUFFIX_END.getVal()).equals(frontFieldName)){
                        rangeArr[1] = queryParameterValueArray[0];
                    }
                    break;
                }

                case DATE_TIME_RANGE:{
                    String[] dateTimeRangeArr = accurateDateTimeRangeQueryParameterMap.get(fieldName);
                    if(dateTimeRangeArr == null){
                        dateTimeRangeArr = new String[2];
                        accurateDateTimeRangeQueryParameterMap.put(fieldName, dateTimeRangeArr);
                    }
                    if((fieldName + DsConstants.RangeFieldSuffixEnum.SUFFIX_START.getVal()).equals(frontFieldName)){
                        dateTimeRangeArr[0] = queryParameterValueArray[0];
                    }else if((fieldName + DsConstants.RangeFieldSuffixEnum.SUFFIX_END.getVal()).equals(frontFieldName)){
                        dateTimeRangeArr[1] = queryParameterValueArray[0];
                    }
                    break;
                }

                case NUMBER_RANGE:{
                    String[] numberRangeArr = accurateNumberRangeQueryParameterMap.get(fieldName);
                    if(numberRangeArr == null){
                        numberRangeArr = new String[2];
                        accurateNumberRangeQueryParameterMap.put(fieldName, numberRangeArr);
                    }
                    if((fieldName + DsConstants.RangeFieldSuffixEnum.SUFFIX_START.getVal()).equals(frontFieldName)){
                        numberRangeArr[0] = queryParameterValueArray[0];
                    }else if((fieldName + DsConstants.RangeFieldSuffixEnum.SUFFIX_END.getVal()).equals(frontFieldName)){
                        numberRangeArr[1] = queryParameterValueArray[0];
                    }
                    break;
                }
                default:{
                    accurateEqualsStringQueryParameterMap.put(fieldName, queryParameterValueArray[0]);
                    break;
                }




            }




















        }


        return queryParameterGroup;



    }
}
