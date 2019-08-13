package ai.sparklabinc.d1.dao;

import ai.sparklabinc.d1.entity.DsTreeMenuCacheDO;

/**
 * @function:
 * @author: DAM
 * @date: 2019/8/11 20:52
 * @description:
 * @version: V1.0
 */
public interface DsTreeMenuCacheDao {
    Integer addDsTreeMenuCache(DsTreeMenuCacheDO dsTreeMenuCacheDO) throws Exception;

    Integer updateDsTreeMenuCache(DsTreeMenuCacheDO dsTreeMenuCacheDO) throws Exception;

    DsTreeMenuCacheDO getDsTreeMenuCache(Long dsId) throws Exception;

    Integer clearDataSourceCacheByDsId(Long dsId) throws Exception;
}
