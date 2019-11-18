package io.g740.d1.util;

import io.g740.d1.constant.DsConstants;
import io.g740.d1.constant.QueryParamConstants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author : Kingzer
 * @date : 2019-07-09 16:29
 * @description :
 */
public class ParameterHandlerUtils {


    public static Pageable extractPageable(Map<String, String[]> params) {
        String[] pages = params.get(QueryParamConstants.SQL_PARAMS_KEY_FOR_SQL_PAGE);
        String[] sizes = params.get(QueryParamConstants.SQL_PARAMS_KEY_FOR_SQL_SIZE);
        String[] sorts = params.get(QueryParamConstants.SQL_PARAMS_KEY_FOR_SQL_SORT);
        Integer page = null;
        Integer size = null;
        Sort sort = null;
        if (pages != null && pages.length > 0) {
            page = Integer.valueOf(pages[0]);
        }
        if (sizes != null && sizes.length > 0) {
            size = Integer.valueOf(sizes[0]);
        }
        if (sorts != null && sorts.length > 0) {
            for (int i = 0; i < sorts.length; i++) {
                String sortParam = sorts[i];
                String[] sortArray = sortParam.split(",");
                Sort sigleSort = null;
                if (sortArray.length == 1) {
                    sigleSort = new Sort(Sort.Direction.DESC, sorts[0]);
                } else if (sortArray.length == 2) {
                    String direction = sortArray[1];
                    String fieldName = sortArray[0];
                    if (QueryParamConstants.SQL_PARAMS_KEY_FOR_SQL_SORT_DESC.equalsIgnoreCase(direction)) {
                        sigleSort = new Sort(Sort.Direction.DESC, fieldName);
                    } else if (QueryParamConstants.SQL_PARAMS_KEY_FOR_SQL_SORT_ASC.equalsIgnoreCase(direction)) {
                        sigleSort = new Sort(Sort.Direction.ASC, fieldName);
                    }
                }
                if(sort == null) {
                    sort = sigleSort;
                }else {
                    sort = sort.and(sigleSort);
                }
            }
        }
        if (page != null && size != null) {
            return  new PageRequest(page, size, sort);
        }else if(sort != null && sorts.length > 0){
            return  new PageRequest(0, DsConstants.SIZE_WITHOUT_PAGEABLE , sort);
        }
        return null;
    }


    public static String extractMoreClause(Map<String, String[]> params) {
        String moreWhereClause = null;
        String[] moreWhereClauses = params.get(QueryParamConstants.SQL_PARAMS_KEY_FOR_CUSTOMER_SQL_CONDITION_CLAUSE);
        if (moreWhereClauses != null && moreWhereClauses.length > 0) {
            moreWhereClause = " and " + moreWhereClauses[0];
        }
        return moreWhereClause;
    }


    /*
    * 解析json字符串
    * */
    public static String extractMoreClause(JSONObject d1ParamsObj) {
        String moreWhereClause = null;
        if(StringUtils.isNotNullNorEmpty(d1ParamsObj.getString("more_where_clauses"))){
            moreWhereClause = " and " + d1ParamsObj.getString("more_where_clauses");
        }
        return moreWhereClause;
    }


    /*
    * 获取参数
    * */
    public static Map<String, String[]> extractParameterMap(JSONObject d1ParamsObj) {
        JSONObject sqlParam = d1ParamsObj.getJSONObject("sql_param");
        Map<String, String[]> parameterMap = new HashMap<>();

        if(sqlParam != null){
            Set<String> fieldKeys = sqlParam.keySet();
            for (String fieldKey : fieldKeys) {
               JSONArray fieldValArr = sqlParam.getJSONArray(fieldKey);
               if(fieldValArr !=null && !fieldValArr.isEmpty()){
                   String[] fileldStrValArr = ArrayUtils.toStringArray(fieldValArr.toArray());
                   parameterMap.put(fieldKey, fileldStrValArr);
               }
            }
        }
        return parameterMap;
    }

    public static Pageable extractPageable(JSONObject d1ParamsObj) {
        Integer page = d1ParamsObj.getInteger("page");
        Integer size = d1ParamsObj.getInteger("size");

        JSONArray sortJSONArray = d1ParamsObj.getJSONArray("sort");
        Sort sort = null;

        if(sortJSONArray != null && !sortJSONArray.isEmpty()){
            for (Object sortObj : sortJSONArray) {
                JSONObject sortJSONObj = (JSONObject) sortObj;
                String[] propertiesArr = ArrayUtils.toStringArray(sortJSONObj.getJSONArray("properties").toArray());
                String direction = sortJSONObj.getString("direction");
                Sort.Direction sortDirection = null;
                Sort subSort = null;
                if(direction != null && Sort.Direction.ASC.name().equalsIgnoreCase(direction)){
                    sortDirection = Sort.Direction.ASC;
                }else{
                    sortDirection = Sort.Direction.DESC;
                }
                subSort = new Sort(sortDirection,propertiesArr);

                if(sort == null){
                    sort = subSort;
                }else{
                    sort = sort.and(subSort);
                }
            }

        }
        if(size == null){
            page = 0;
            size = 0;
        }

        return new PageRequest(page,size,sort);


    }
}
