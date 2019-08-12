package ai.sparklabinc.d1.component;

import ai.sparklabinc.d1.dao.DataSourceDao;
import ai.sparklabinc.d1.dao.DbBasicConfigDao;
import ai.sparklabinc.d1.dao.DfKeyBasicConfigDao;
import ai.sparklabinc.d1.dao.DsTreeMenuCacheDao;
import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DfKeyInfoDTO;
import ai.sparklabinc.d1.dto.TableAndViewInfoDTO;
import ai.sparklabinc.d1.entity.DsTreeMenuCacheDO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.util.List;

/**
 * @function:
 * @author: DAM
 * @date: 2019/8/9 14:32
 * @description:
 * @version: V1.0
 */
@Component
public class CacheComponent {
    @Resource(name = "DbBasicConfigDao")
    private DbBasicConfigDao dbBasicConfigDao;

    @Resource(name = "DataSourceDao")
    private DataSourceDao dataSourceDao;

    @Resource(name = "DfKeyBasicConfigDao")
    private DfKeyBasicConfigDao dfKeyBasicConfigDao;

    @Resource(name = "DsTreeMenuCacheDao")
    private DsTreeMenuCacheDao dsTreeMenuCacheDao;

    public List<DbInforamtionDTO> selectDataSources(Long dsId) throws Exception {
        return dbBasicConfigDao.selectDataSources(dsId);
    }

    public List<DbInforamtionDTO> selectAllSchema(Long dsId) throws Exception {
        DsTreeMenuCacheDO dsTreeMenuCache = dsTreeMenuCacheDao.getDsTreeMenuCache(dsId);
        if(dsTreeMenuCache==null||StringUtils.isBlank( dsTreeMenuCache.getDsSchemaInfo())){
           return null;
        }
        String dsSchemaInfoJSON = dsTreeMenuCache.getDsSchemaInfo();
        List<DbInforamtionDTO> dbInforamtionDTOS = JSONObject.parseArray(dsSchemaInfoJSON, DbInforamtionDTO.class);
        return dbInforamtionDTOS;
    }

    public List<TableAndViewInfoDTO> selectAllTableAndView(Long dsId) throws Exception {
        DsTreeMenuCacheDO dsTreeMenuCache = dsTreeMenuCacheDao.getDsTreeMenuCache(dsId);
        if(dsTreeMenuCache==null||StringUtils.isBlank( dsTreeMenuCache.getDsTableViewInfo())){
            return null;
        }
        String dsTableViewInfoJSON = dsTreeMenuCache.getDsTableViewInfo();
        List<TableAndViewInfoDTO> tableAndViewInfoDTOS = JSONObject.parseArray(dsTableViewInfoJSON, TableAndViewInfoDTO.class);
        return tableAndViewInfoDTOS;
    }


    public List<DfKeyInfoDTO> getAllDataFacetKey() throws Exception {
        return dfKeyBasicConfigDao.getAllDataFacetKey();
    }


    public List<DbInforamtionDTO> selectAllSchemaPut(Long dsId) throws Exception {
        List<DbInforamtionDTO> dbInforamtionDTOS = dataSourceDao.selectAllSchema(dsId);
        if(!CollectionUtils.isEmpty(dbInforamtionDTOS)){
            String jsonString = JSON.toJSONString(dbInforamtionDTOS);
            DsTreeMenuCacheDO dsTreeMenuCache = dsTreeMenuCacheDao.getDsTreeMenuCache(dsId);
            if(dsTreeMenuCache!=null){
                dsTreeMenuCache.setDsSchemaInfo(jsonString);
                dsTreeMenuCacheDao.updateDsTreeMenuCache(dsTreeMenuCache);
            }else {
                DsTreeMenuCacheDO newDsTreeMenuCache = new DsTreeMenuCacheDO();
                newDsTreeMenuCache.setDsId(dsId);
                newDsTreeMenuCache.setDsSchemaInfo(jsonString);
                dsTreeMenuCacheDao.addDsTreeMenuCache(newDsTreeMenuCache) ;
            }

        }
        return dbInforamtionDTOS;

    }

    public List<TableAndViewInfoDTO> selectAllTableAndViewPut(Long dsId) throws Exception {
        List<TableAndViewInfoDTO> tableAndViewInfoDTOS = dataSourceDao.selectAllTableAndView(dsId);
        if(!CollectionUtils.isEmpty(tableAndViewInfoDTOS)){
            String jsonString = JSON.toJSONString(tableAndViewInfoDTOS);
            DsTreeMenuCacheDO dsTreeMenuCache = dsTreeMenuCacheDao.getDsTreeMenuCache(dsId);
            if(dsTreeMenuCache!=null){
                dsTreeMenuCache.setDsTableViewInfo(jsonString);
                dsTreeMenuCacheDao.updateDsTreeMenuCache(dsTreeMenuCache);
            }else {
                DsTreeMenuCacheDO newDsTreeMenuCache = new DsTreeMenuCacheDO();
                newDsTreeMenuCache.setDsId(dsId);
                newDsTreeMenuCache.setDsTableViewInfo(jsonString);
                dsTreeMenuCacheDao.addDsTreeMenuCache(newDsTreeMenuCache) ;
            }

        }
        return tableAndViewInfoDTOS;
    }


//    @Cacheable(value = "selectAllSchema")
//    public List<DbInforamtionDTO> selectAllSchema(Long dsId) throws Exception {
//        return null;
//    }
//
//    @Cacheable(value = "selectAllTableAndView")
//    public List<TableAndViewInfoDTO> selectAllTableAndView(Long dsId) throws Exception {
//        return null;
//    }
//
//    @Cacheable(value = "getAllDataFacetKey")
//    public List<DfKeyInfoDTO> getAllDataFacetKey() throws Exception {
//        return dfKeyBasicConfigDao.getAllDataFacetKey();
//    }
//
//
//
//    @CachePut(value = "selectAllSchema")
//    public List<DbInforamtionDTO> selectAllSchemaPut(Long dsId) throws Exception {
//        return dataSourceDao.selectAllSchema(dsId);
//    }
//
//    @CachePut(value = "selectAllTableAndView")
//    public List<TableAndViewInfoDTO> selectAllTableAndViewPut(Long dsId) throws Exception {
//        return dataSourceDao.selectAllTableAndView(dsId);
//    }
//
//
//
//    /**
//     * 清除多个缓存
//     */
//    @Caching(evict = {@CacheEvict(value = "selectAllSchema", key = "#dsId"),
//            @CacheEvict(value = "selectAllTableAndView", key = "#dsId"),
//            @CacheEvict(value = "getAllDataFacetKey")})
//    public void clearDataSourceTreeAllCache(Long dsId) {
//    }

}

