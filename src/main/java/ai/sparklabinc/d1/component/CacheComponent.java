package ai.sparklabinc.d1.component;

import ai.sparklabinc.d1.dao.DataSourceDao;
import ai.sparklabinc.d1.dao.DbBasicConfigDao;
import ai.sparklabinc.d1.dao.DfKeyBasicConfigDao;
import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DfKeyInfoDTO;
import ai.sparklabinc.d1.dto.TableAndViewInfoDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

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


    @Cacheable(value = "selectAllSchema")
    public List<DbInforamtionDTO> selectAllSchema(Long dsId) throws Exception {
        return null;
    }

    @Cacheable(value = "selectAllTableAndView")
    public List<TableAndViewInfoDTO> selectAllTableAndView(Long dsId) throws Exception {
        return null;
    }

    @Cacheable(value = "getAllDataFacetKey")
    public List<DfKeyInfoDTO> getAllDataFacetKey() throws Exception {
        return dfKeyBasicConfigDao.getAllDataFacetKey();
    }



    @CachePut(value = "selectAllSchema")
    public List<DbInforamtionDTO> selectAllSchemaPut(Long dsId) throws Exception {
        return dataSourceDao.selectAllSchema(dsId);
    }

    @CachePut(value = "selectAllTableAndView")
    public List<TableAndViewInfoDTO> selectAllTableAndViewPut(Long dsId) throws Exception {
        return dataSourceDao.selectAllTableAndView(dsId);
    }



    /**
     * 清除多个缓存
     */
    @Caching(evict = {@CacheEvict(value = "selectAllSchema", key = "#dsId"),
            @CacheEvict(value = "selectAllTableAndView", key = "#dsId"),
            @CacheEvict(value = "getAllDataFacetKey")})
    public void clearDataSourceTreeAllCache(Long dsId) {
    }

}

