package ai.sparklabinc.d1.util;

import ai.sparklabinc.d1.constant.QueryParamConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

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
            return  PageRequest.of(page, size, sort);
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

}
