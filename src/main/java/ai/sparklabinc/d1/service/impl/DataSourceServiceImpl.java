package ai.sparklabinc.d1.service.impl;

import ai.sparklabinc.d1.component.CacheComponent;
import ai.sparklabinc.d1.constant.DsConstants;
import ai.sparklabinc.d1.dao.DbBasicConfigDao;
import ai.sparklabinc.d1.dao.DbSecurityConfigDao;
import ai.sparklabinc.d1.dao.DfKeyBasicConfigDao;
import ai.sparklabinc.d1.dao.DsTreeMenuCacheDao;
import ai.sparklabinc.d1.datasource.ConnectionService;
import ai.sparklabinc.d1.datasource.Constants;
import ai.sparklabinc.d1.datasource.DataSourceFactory;
import ai.sparklabinc.d1.dto.*;
import ai.sparklabinc.d1.entity.DbBasicConfigDO;
import ai.sparklabinc.d1.entity.DbSecurityConfigDO;
import ai.sparklabinc.d1.entity.DsTreeMenuCacheDO;
import ai.sparklabinc.d1.exception.ServiceException;
import ai.sparklabinc.d1.service.DataSourceService;
import ai.sparklabinc.d1.util.FileReaderUtil;
import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Function:
 * @Author: DAM
 * @Date: 2019/7/1 20:33
 * @Description:
 * @Version: V1.0
 */

@Service
public class DataSourceServiceImpl implements DataSourceService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DataSourceServiceImpl.class);

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Autowired
    private CacheComponent cacheComponent;

    @Resource(name = "DbBasicConfigDao")
    private DbBasicConfigDao dbBasicConfigDao;

    @Resource(name = "DbSecurityConfigDao")
    private DbSecurityConfigDao dbSecurityConfigDao;

    @Resource(name = "DfKeyBasicConfigDao")
    private DfKeyBasicConfigDao dfKeyBasicConfigDao;

    @Resource(name = "DsTreeMenuCacheDao")
    private DsTreeMenuCacheDao dsTreeMenuCacheDao;

    @Autowired
    private ConnectionService connectionService;


    @Override
    public void Connection2DataSource(Long dsId) throws Exception {
        Connection connection = null;
        try {
            DataSource mysql = dataSourceFactory.builder(Constants.DATABASE_TYPE_MYSQL, dsId);
            connection = mysql.getConnection();
            if (connection == null) {
                throw new ServiceException("connection is to db is failed");
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("error>>>>" + e.getMessage());
                }
            }
        }
    }

    @Override
    public DbInforamtionDTO addDataSources(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception {
        DbBasicConfigDO dbBasicConfigDO = new DbBasicConfigDO();
        BeanUtils.copyProperties(dbBasicConfigDTO, dbBasicConfigDO);
        if (dbBasicConfigDTO.getOtherParams() != null) {
            String jsonString = JSON.toJSONString(dbBasicConfigDTO.getOtherParams());
            dbBasicConfigDO.setOtherParams(jsonString);
        }
        String urlSuffix = DsConstants.urlSuffix;
        if (dbSecurityConfigDTO.getUseSshTunnel()) {
            if (dbSecurityConfigDTO.getUseSsl() != null && dbSecurityConfigDTO.getUseSsl()) {
                urlSuffix += "&useSSL=true";
            } else {
                urlSuffix += "&useSSL=false";
            }
        }
        dbBasicConfigDO.setDbUrl(urlSuffix);

        Long dsId = dbBasicConfigDao.add(dbBasicConfigDO);
        if (dbSecurityConfigDTO == null) {
            dbSecurityConfigDTO = new DbSecurityConfigDTO();
        }
        if (dsId > 0L) {
            DbSecurityConfigDO dbSecurityConfigDO = new DbSecurityConfigDO();
            BeanUtils.copyProperties(dbSecurityConfigDTO, dbSecurityConfigDO);
            if (StringUtils.isNotBlank(dbSecurityConfigDO.getSshKeyFile())) {
                String sshKeyContent = FileReaderUtil.readFile(dbSecurityConfigDO.getSshKeyFile());
                dbSecurityConfigDO.setSshKeyContent(sshKeyContent);
            }

            dbSecurityConfigDO.setId(dsId);
            int row = dbSecurityConfigDao.add(dbSecurityConfigDO);
            if (row > 0) {
                DbInforamtionDTO dbInforamtionDTO = new DbInforamtionDTO();
                dbInforamtionDTO.setLabel(dbBasicConfigDO.getDbName());
                dbInforamtionDTO.setLevel(1);
                dbInforamtionDTO.setId(dsId);
                return dbInforamtionDTO;
            }

        }
        return null;
    }

    @Override
    public void deleteDataSources(Long dsId) throws Exception {
        Integer delete = dbBasicConfigDao.delete(dsId);
        Integer delete1 = dbSecurityConfigDao.delete(dsId);
        if (delete > 0 && delete1 > 0) {
            //清楚缓存
            cacheComponent.clearDataSourceCacheByDsId(dsId);
        }
    }

    @Override
    public List<DbInforamtionDTO> selectDataSources() throws Exception {

        /*********************************************************************
         * step1 拿到前端需要展示的第一层信息
         * *******************************************************************
         */
        long startTime = System.currentTimeMillis();
        List<DbInforamtionDTO> result = dbBasicConfigDao.selectDataSources(null);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        //获取所有的data facet key
        List<DfKeyInfoDTO> allDataFacetKey = cacheComponent.getAllDataFacetKey();

        //获取所有缓存
        List<DsTreeMenuCacheDO> dsTreeMenuCache = dsTreeMenuCacheDao.getDsTreeMenuCache();
        LOGGER.info("selectDataSources、getAllDataFacetKey、getDsTreeMenuCache耗时：{}", System.currentTimeMillis() - startTime);

        startTime = System.currentTimeMillis();
        for (DbInforamtionDTO dbInforamtionDTO : result) {

            /*********************************************************************
             * step2 拿到所有的数据库名称
             * *******************************************************************
             */
            Long dsId = dbInforamtionDTO.getId();
            DsTreeMenuCacheDO dsTreeMenuCacheDO=null;
            Optional<DsTreeMenuCacheDO> first = dsTreeMenuCache.stream().filter(e -> e.getDsId().equals(dsId)).findFirst();
            if(first.isPresent()){
                dsTreeMenuCacheDO = first.get();
            }
            List<DbInforamtionDTO> schemas = cacheComponent.selectAllSchema(dsTreeMenuCacheDO);
            if (CollectionUtils.isEmpty(schemas)) {
                continue;
            }
            dbInforamtionDTO.setChildren(schemas);
            /*********************************************************************
             * step3 拿到所有schema所有的表和视图,还有所有的data facet key，提高性能
             * *******************************************************************
             */
            //所有schema所有的表和视图
            List<TableAndViewInfoDTO> tableAndViewInfoDTOS = cacheComponent.selectAllTableAndView(dsTreeMenuCacheDO);

            if (CollectionUtils.isEmpty(tableAndViewInfoDTOS)) {
                continue;
            }

            for (DbInforamtionDTO schema : schemas) {
                List<DbInforamtionDTO> tableAndViews = new LinkedList<>();
                //获取schema的talbe
                List<TableAndViewInfoDTO> collect = tableAndViewInfoDTOS.stream()
                        .filter(e -> schema.getLabel().equalsIgnoreCase(e.getTableSchema()))
                        .collect(Collectors.toList());

                if (CollectionUtils.isEmpty(collect)) {
                    continue;
                }

                //封装数据
                collect.forEach(e -> {
                    DbInforamtionDTO dbInfo = new DbInforamtionDTO();
                    dbInfo.setLabel(e.getTableName());
                    dbInfo.setType(e.getType());
                    dbInfo.setLevel(e.getLevel());
                    tableAndViews.add(dbInfo);
                });

                /*********************************************************************
                 * step4 拿到表和视图的data facet key
                 * *******************************************************************
                 */

                getDfKeyOfTableAndView(dsId, schema, tableAndViews, allDataFacetKey);

                //加入 table and view
                schema.setChildren(tableAndViews);
            }
            LOGGER.info("selectAllTableAndView、getDfKeyOfTableAndView>>>：{},{}", dbInforamtionDTO.getLabel(), System.currentTimeMillis() - startTime);
        }
        LOGGER.info("selectAllTableAndView、getDfKeyOfTableAndView：{}", System.currentTimeMillis() - startTime);
        return result;
    }


    @Override
    public DbInforamtionDTO refreshDataSources(Long dsId) throws Exception {
        /*********************************************************************
         * step1 拿到前端需要展示的第一层信息
         * *******************************************************************
         */
        List<DbInforamtionDTO> result = dbBasicConfigDao.selectDataSources(dsId);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        DbInforamtionDTO dbInforamtionDTO = result.get(0);
        /*********************************************************************
         * step2 拿到所有的数据库名称
         * *******************************************************************
         */
        List<DbInforamtionDTO> schemas = cacheComponent.selectAllSchemaPut(dsId);
        if (CollectionUtils.isEmpty(schemas)) {
            return dbInforamtionDTO;
        }
        dbInforamtionDTO.setChildren(schemas);
        /*********************************************************************
         * step3 拿到所有schema所有的表和视图,还有所有的data facet key，提高性能
         * *******************************************************************
         */
        //所有schema所有的表和视图
        List<TableAndViewInfoDTO> tableAndViewInfoDTOS = cacheComponent.selectAllTableAndViewPut(dsId);
        //获取所有的data facet key
        List<DfKeyInfoDTO> allDataFacetKey = cacheComponent.getAllDataFacetKey();
        if (CollectionUtils.isEmpty(tableAndViewInfoDTOS)) {
            return dbInforamtionDTO;
        }

        for (DbInforamtionDTO schema : schemas) {
            List<DbInforamtionDTO> tableAndViews = new LinkedList<>();
            //获取schema的talbe
            List<TableAndViewInfoDTO> collect = tableAndViewInfoDTOS.stream()
                    .filter(e -> schema.getLabel().equalsIgnoreCase(e.getTableSchema()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(collect)) {
                continue;
            }

            //封装数据
            collect.forEach(e -> {
                DbInforamtionDTO dbInfo = new DbInforamtionDTO();
                dbInfo.setLabel(e.getTableName());
                dbInfo.setType(e.getType());
                dbInfo.setLevel(e.getLevel());
                tableAndViews.add(dbInfo);
            });

            /*********************************************************************
             * step4 拿到表和视图的data facet key
             * *******************************************************************
             */
            getDfKeyOfTableAndView(dsId, schema, tableAndViews, allDataFacetKey);
            //加入 table and view
            schema.setChildren(tableAndViews);
        }
        return dbInforamtionDTO;
    }

    private void getDfKeyOfTableAndView(Long dsId, DbInforamtionDTO schema, List<DbInforamtionDTO> tableAndViews, List<DfKeyInfoDTO> allDataFacetKey) throws IOException, SQLException {
        Iterator<DbInforamtionDTO> tableAndViewsIterator = tableAndViews.iterator();
        while (tableAndViewsIterator.hasNext()) {
            DbInforamtionDTO tableAndView = tableAndViewsIterator.next();
            //从内存中拿数据筛选
            List<DfKeyInfoDTO> dfKeyInfoDTOList = allDataFacetKey.stream()
                    .filter(e -> e.getFkDbId().equals(dsId)
                            && e.getSchemaName().equals(schema.getLabel())
                            && e.getTableName().equals(tableAndView.getLabel()))
                    .collect(Collectors.toList());
            List<DbInforamtionDTO> dataFacetKeys = new LinkedList<>();
            //封装数据
            dfKeyInfoDTOList.forEach(e -> {
                DbInforamtionDTO dbInforamtionDTO = new DbInforamtionDTO();
                dbInforamtionDTO.setLevel(e.getLevel());
                dbInforamtionDTO.setLabel(e.getLabel());
                dbInforamtionDTO.setId(e.getId());
                dataFacetKeys.add(dbInforamtionDTO);
            });
            //List<DbInforamtionDTO> dataFacetKeys = dfKeyBasicConfigDao.getDataFacetKey(dsId, schema.getLabel(), tableAndView.getLabel());
            tableAndView.setChildren(dataFacetKeys);
        }
    }


    @Override
    public List<Map<String, Object>> selectDataSourceProperty(Long dsId) throws IOException, SQLException {
        List<Map<String, Object>> result = dbBasicConfigDao.selectDataSourceProperty(dsId);
        return result;
    }

    @Override
    public void editDataSourceProperty(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception {
        boolean updateResult = false;
        DbBasicConfigDO dbBasicConfigDO = new DbBasicConfigDO();
        BeanUtils.copyProperties(dbBasicConfigDTO, dbBasicConfigDO);
        if (dbBasicConfigDTO.getOtherParams() != null) {
            String jsonString = JSON.toJSONString(dbBasicConfigDTO.getOtherParams());
            dbBasicConfigDO.setOtherParams(jsonString);
        }

        String urlSuffix = DsConstants.urlSuffix;
        if (dbSecurityConfigDTO.getUseSshTunnel()) {
            if (dbSecurityConfigDTO.getUseSsl() != null && dbSecurityConfigDTO.getUseSsl()) {
                urlSuffix += "&useSSL=true";
            } else {
                urlSuffix += "&useSSL=false";
            }
        }
        dbBasicConfigDO.setDbUrl(urlSuffix);

        Integer dbBasicUpdate = dbBasicConfigDao.editDataSourceProperty(dbBasicConfigDO);

        DbSecurityConfigDO dbSecurityConfigDO = new DbSecurityConfigDO();
        BeanUtils.copyProperties(dbSecurityConfigDTO, dbSecurityConfigDO);
        dbSecurityConfigDO.setId(dbBasicConfigDTO.getId());
        if (StringUtils.isNotBlank(dbSecurityConfigDO.getSshKeyFile())
                && Constants.SshAuthType.KEY_PAIR.toString().equalsIgnoreCase(dbSecurityConfigDO.getSshAuthType())) {
            String sshKeyFile = dbSecurityConfigDO.getSshKeyFile();
            File file = new File(sshKeyFile);
            //文件存在则更新key文件内容
            if (file.exists()) {
                String sshKeyContent = FileReaderUtil.readFile(sshKeyFile);
                dbSecurityConfigDO.setSshKeyContent(sshKeyContent);
            }
        }
        Integer dbSecurityUpdate = dbSecurityConfigDao.editDataSourceProperty(dbSecurityConfigDO);

        if (dbBasicUpdate > 0 && dbSecurityUpdate > 0) {
            //清除ssh，datasource
            DataSource dataSource = dataSourceFactory.dataSourceMap.get(dbBasicConfigDO.getId());
            if (dataSource != null) {
                //注销
                dataSource.postDeregister();
                dataSourceFactory.dataSourceMap.remove(dbBasicConfigDO.getId());
            }

            Session session = dataSourceFactory.sshSessionMap.get(dbBasicConfigDO.getId());
            if (session != null) {
                //关闭连接
                session.disconnect();
                dataSourceFactory.sshSessionMap.remove(dbBasicConfigDO.getId());
            }
            updateResult = true;
        }
        if(!updateResult){
            throw new ServiceException("update datasource property is failed");
        }
    }


    @Override
    public boolean dataSourceTestConnection(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception {
        return connectionService.createConnection(dbBasicConfigDTO, dbSecurityConfigDTO);
    }


}
