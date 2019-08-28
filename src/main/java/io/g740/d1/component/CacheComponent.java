package io.g740.d1.component;

import io.g740.d1.dao.DataSourceDao;
import io.g740.d1.dao.DbBasicConfigDao;
import io.g740.d1.dao.DfKeyBasicConfigDao;
import io.g740.d1.dao.DsTreeMenuCacheDao;
import io.g740.d1.dto.DbInformationDTO;
import io.g740.d1.dto.DfKeyInfoDTO;
import io.g740.d1.dto.TableAndViewInfoDTO;
import io.g740.d1.entity.DsTreeMenuCacheDO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final static Logger LOGGER=LoggerFactory.getLogger(CacheComponent.class);

    @Resource(name = "DbBasicConfigDao")
    private DbBasicConfigDao dbBasicConfigDao;

    @Resource(name = "DataSourceDao")
    private DataSourceDao dataSourceDao;

    @Resource(name = "DfKeyBasicConfigDao")
    private DfKeyBasicConfigDao dfKeyBasicConfigDao;

    @Resource(name = "DsTreeMenuCacheDao")
    private DsTreeMenuCacheDao dsTreeMenuCacheDao;

    public List<DbInformationDTO> selectDataSources(Long dsId) throws Exception {
        return dbBasicConfigDao.selectDataSources(dsId);
    }

    public List<DbInformationDTO> selectAllSchema(DsTreeMenuCacheDO dsTreeMenuCache) {
        if(dsTreeMenuCache==null||StringUtils.isBlank( dsTreeMenuCache.getDsSchemaInfo())){
           return null;
        }
        String dsSchemaInfoJSON = dsTreeMenuCache.getDsSchemaInfo();
        List<DbInformationDTO> dbInformationDTOS = JSONObject.parseArray(dsSchemaInfoJSON, DbInformationDTO.class);
        return dbInformationDTOS;
    }

    public List<TableAndViewInfoDTO> selectAllTableAndView(DsTreeMenuCacheDO dsTreeMenuCache) {
        long start=System.currentTimeMillis();
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


    public List<DbInformationDTO> selectAllSchemaPut(Long dsId) throws Exception {
        List<DbInformationDTO> dbInformationDTOS = dataSourceDao.selectAllSchema(dsId);
        if(!CollectionUtils.isEmpty(dbInformationDTOS)){
            String jsonString = JSON.toJSONString(dbInformationDTOS);
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
        return dbInformationDTOS;
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


    public Integer clearDataSourceCacheByDsId(Long dsId) throws Exception {
        return dsTreeMenuCacheDao.clearDataSourceCacheByDsId(dsId);
    }


//    @Cacheable(value = "selectAllSchema")
//    public List<DbInformationDTO> selectAllSchema(Long dsId) throws Exception {
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
//    public List<DbInformationDTO> selectAllSchemaPut(Long dsId) throws Exception {
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

