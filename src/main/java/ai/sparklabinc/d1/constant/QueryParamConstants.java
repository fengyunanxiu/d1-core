package ai.sparklabinc.d1.constant;

/**
 * @author : Kingzer
 * @date : 2019-07-03 13:53
 * @description :
 */
public interface QueryParamConstants {

    /**
     * 用于自定义的sql查询条件，map参数自定义sql条件的key关键词
     */
    String SQL_PARAMS_KEY_FOR_CUSTOMER_SQL_CONDITION_CLAUSE = "more_where_clauses";

    /**
     * 用于sql分页查询，map参数页数的key关键词
     */
    String SQL_PARAMS_KEY_FOR_SQL_PAGE = "page";

    /**
     * 用于sql分页查询，map参数size的key关键词
     */
    String SQL_PARAMS_KEY_FOR_SQL_SIZE = "size";

    /**
     * 用于sql排序，map参数排序的key关键词
     */
    String SQL_PARAMS_KEY_FOR_SQL_SORT = "sort";
    
    /**
     * 用于sql排序方式，默认作为sort的value的一部分 ，  例如： sort = id,DESC
     */
    String SQL_PARAMS_KEY_FOR_SQL_SORT_DESC = "DESC";
    
    /**
     * 用于sql排序方式，默认作为sort的value的一部分 ，  例如： sort = id,ASC
     */
    String SQL_PARAMS_KEY_FOR_SQL_SORT_ASC = "ASC";

}
