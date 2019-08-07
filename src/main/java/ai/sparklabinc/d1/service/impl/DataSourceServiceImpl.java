package ai.sparklabinc.d1.service.impl;

import ai.sparklabinc.d1.component.MysqlDataSourceComponent;
import ai.sparklabinc.d1.constant.DsConstants;
import ai.sparklabinc.d1.dao.*;
import ai.sparklabinc.d1.datasource.ConnectionService;
import ai.sparklabinc.d1.datasource.Constants;
import ai.sparklabinc.d1.datasource.DataSourceFactory;
import ai.sparklabinc.d1.dto.*;
import ai.sparklabinc.d1.entity.DbBasicConfigDO;
import ai.sparklabinc.d1.entity.DbSecurityConfigDO;
import ai.sparklabinc.d1.entity.DfFormTableSettingDO;
import ai.sparklabinc.d1.entity.DfKeyBasicConfigDO;
import ai.sparklabinc.d1.exception.custom.IllegalParameterException;
import ai.sparklabinc.d1.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.d1.service.DataSourceService;
import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.Session;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Resource(name = "DbBasicConfigDao")
    private DbBasicConfigDao dbBasicConfigDao;

    @Resource(name = "DbSecurityConfigDao")
    private DbSecurityConfigDao dbSecurityConfigDao;

    @Resource(name = "DataSourceDao")
    private DataSourceDao mysqlDataSourceDao;

    @Resource(name = "DfKeyBasicConfigDao")
    private DfKeyBasicConfigDao dfKeyBasicConfigDao;

    @Resource(name = "DfFormTableSettingDao")
    private DfFormTableSettingDao dfFormTableSettingDao;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private MysqlDataSourceComponent mysqlDataSourceComponent;


    @Override
    public boolean Connection2DataSource(Long dsId) throws SQLException, IOException {
        Connection connection = null;
        try {
            DataSource mysql = dataSourceFactory.builder(Constants.DATABASE_TYPE_MYSQL, dsId);
            connection = mysql.getConnection();
            if (connection != null) {
                return true;
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
        return false;
    }

    @Override
    public DbInforamtionDTO addDataSources(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws IOException, SQLException {
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
    public boolean deleteDataSources(Long dsId) throws IOException, SQLException {
        Integer delete = dbBasicConfigDao.delete(dsId);
        Integer delete1 = dbSecurityConfigDao.delete(dsId);
        if (delete > 0 && delete1 > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<DbInforamtionDTO> selectDataSources(Long dsId, Integer dfKeyFilter) throws IOException, SQLException {

        /*********************************************************************
         * step1 拿到前端需要展示的第一层信息
         * *******************************************************************
         */
        List<DbInforamtionDTO> result = dbBasicConfigDao.selectDataSources(dsId);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        /*********************************************************************
         * step2 连接数据库
         * *******************************************************************
         */

        if (dsId != null) {
            DbInforamtionDTO dbInforamtionDTO = result.get(0);
            dsId = dbInforamtionDTO.getId();

            boolean connection = Connection2DataSource(dbInforamtionDTO.getId());

            if (connection) {
                /*********************************************************************
                 * step3 拿到所有的数据库名称
                 * *******************************************************************
                 */
                List<DbInforamtionDTO> schemas = mysqlDataSourceDao.selectAllSchema(dsId);
                if (CollectionUtils.isEmpty(schemas)) {
                    return result;
                }
                dbInforamtionDTO.setChildren(schemas);
                /*********************************************************************
                 * step4 拿到所有schema所有的表和视图,还有所有的data facet key，提高性能
                 * *******************************************************************
                 */
                //所有schema所有的表和视图
                List<TableAndViewInfoDTO> tableAndViewInfoDTOS = mysqlDataSourceDao.selectAllTableAndView(dsId);
                //获取所有的data facet key
                List<DfKeyInfoDTO> allDataFacetKey = dfKeyBasicConfigDao.getAllDataFacetKey();

                if (CollectionUtils.isEmpty(tableAndViewInfoDTOS)) {
                    return result;
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
                     * step5 拿到表和视图的data facet key
                     * *******************************************************************
                     */

                    getDfKeyOfTableAndView(dsId, dfKeyFilter, schema, tableAndViews, allDataFacetKey);

                    //加入 table and view
                    schema.setChildren(tableAndViews);
                }

                //如果选了过滤df key则过滤掉没有tableAndView 为空的schema和ds
                if (dfKeyFilter == 1 || dfKeyFilter == 2) {
                    List<DbInforamtionDTO> schemasHasChildren = schemas.stream()
                            .filter(e -> e.getChildren() != null && e.getChildren().size() > 0)
                            .collect(Collectors.toList());
                    dbInforamtionDTO.setChildren(schemasHasChildren);
                    //删除其他的数据源信息
                    int size = result.size();
                    for (int i = size - 1; i > 0; i--) {
                        result.remove(i);
                    }
                }

            }
        }


        return result;
    }


    private void getDfKeyOfTableAndView(Long dsId, Integer dfKeyFilter, DbInforamtionDTO schema, List<DbInforamtionDTO> tableAndViews, List<DfKeyInfoDTO> allDataFacetKey) throws IOException, SQLException {
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
            switch (dfKeyFilter) {
                case 1:
                    if (CollectionUtils.isEmpty(dataFacetKeys)) {
                        tableAndViewsIterator.remove();
                    }
                    tableAndView.setChildren(dataFacetKeys);
                    break;
                case 2:
                    if (!CollectionUtils.isEmpty(dataFacetKeys)) {
                        tableAndViewsIterator.remove();
                    }
                    break;
                default:
                    if (CollectionUtils.isEmpty(dataFacetKeys)) {
                        continue;
                    }
                    tableAndView.setChildren(dataFacetKeys);
            }
        }
    }


    @Override
    public List<Map<String, Object>> selectDataSourceProperty(Long dsId) throws IOException, SQLException {
        List<Map<String, Object>> result = dbBasicConfigDao.selectDataSourceProperty(dsId);
        return result;
    }

    @Override
    public boolean editDataSourceProperty(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws IOException, SQLException {
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

        return updateResult;
    }


    @Override
    public DbInforamtionDTO addDataFacetKey(DfKeyBasicConfigDTO dfKeyBasicConfigDTO) throws Exception {
        DfKeyBasicConfigDO dfKeyBasicConfigByDfKey = dfKeyBasicConfigDao.getDfKeyBasicConfigByDfKey(dfKeyBasicConfigDTO.getDfKey());
        //新加的df key 是否已经存在
        if (dfKeyBasicConfigByDfKey != null) {
            throw new IllegalParameterException("data facet key already exists!");
        }
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dfKeyBasicConfigDTO.getFkDbId());
        switch (dbBasicConfigDO.getDbType()) {
            case Constants.DATABASE_TYPE_MYSQL:
                return mysqlDataSourceComponent.addDataFacetKeyProcess(dfKeyBasicConfigDTO);
            case Constants.DATABASE_TYPE_POSTGRESQL:
                return null;
            default:
                return mysqlDataSourceComponent.addDataFacetKeyProcess(dfKeyBasicConfigDTO);
        }
    }


    @Override
    public List<Map<String, Object>> selectAllDfFormTableSettingByDfKey(String dfKey) throws Exception {
        List<Map<String, Object>> allDfFormTableSettingByDfKey = dfFormTableSettingDao.selectAllDfFormTableSettingByDfKey(dfKey);
        DfKeyBasicConfigDO dfKeyBasicConfigDO = dfKeyBasicConfigDao.getDfKeyBasicConfigByDfKey(dfKey);
        if (dfKeyBasicConfigDO == null) {
            throw new ResourceNotFoundException("ds config is not found!");
        }
        //获取data facet key真实的table字段
        List<TableColumnsDetailDTO> tableColumnsDetailDTOList = mysqlDataSourceDao.selectTableColumnsDetail(dfKeyBasicConfigDO.getFkDbId(),
                dfKeyBasicConfigDO.getSchemaName(),
                dfKeyBasicConfigDO.getTableName());

        List<String> colunmNames = tableColumnsDetailDTOList.stream()
                .map(TableColumnsDetailDTO::getColumnName)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(colunmNames)) {
            throw new ResourceNotFoundException("table is not exist");
        }

        allDfFormTableSettingByDfKey.forEach(e -> {
            if (!colunmNames.contains(e.get("db_field_name"))) {
                e.put("column_is_exist", 0);
            } else {
                e.put("column_is_exist", 1);
            }
        });
        return allDfFormTableSettingByDfKey;
    }


    @Override
    public boolean updateDataFacetKey(String dfKey, String newDfKey, String description) throws IOException, SQLException {
        boolean updateResult = false;
        int updateRows = dfKeyBasicConfigDao.updateDataFacetKey(dfKey, newDfKey, description);
        if (updateRows > 0) {
            updateRows = dfFormTableSettingDao.updateDataFacetKey(dfKey, newDfKey);
            if (updateRows > 0) {
                updateResult = true;
            }
        }
        return updateResult;
    }

    @Override
    public boolean deleteDataFacetKey(String dfKey) throws IOException, SQLException {
        boolean updateResult = false;
        int updateRows = dfKeyBasicConfigDao.deleteDataFacetKey(dfKey);
        if (updateRows > 0) {
            updateRows = dfFormTableSettingDao.deleteDataFacetKey(dfKey);
            if (updateRows > 0) {
                updateResult = true;
            }
        }
        return updateResult;
    }

    @Override
    public boolean dataSourceTestConnection(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws Exception {
        return connectionService.createConnection(dbBasicConfigDTO, dbSecurityConfigDTO);
    }

    @Override
    public Boolean saveDfFormTableSetting(List<DfFormTableSettingDO> dfFormTableSettingDOSForUpdate, List<DfFormTableSettingDO> dfFormTableSettingDOSForAdd) throws Exception{
        //更新操作
        if(!CollectionUtils.isEmpty(dfFormTableSettingDOSForUpdate)){
            for(DfFormTableSettingDO dfFormTableSettingDO : dfFormTableSettingDOSForUpdate){
                Integer updateResult = dfFormTableSettingDao.updateDfFormTableSetting(dfFormTableSettingDO);
                if(updateResult<=0){
                    return false;
                }
            }
        }
        //添加操作
        if(!CollectionUtils.isEmpty(dfFormTableSettingDOSForAdd)){
            for(DfFormTableSettingDO dfFormTableSettingDO : dfFormTableSettingDOSForAdd){
                Integer add = dfFormTableSettingDao.add(dfFormTableSettingDO);
                if(add<=0){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public List<Map<String, Object>> refreshDfFormTableSetting(String dfKey) throws Exception {
        DfKeyBasicConfigDO dfKeyBasicConfigDO = dfKeyBasicConfigDao.getDfKeyBasicConfigByDfKey(dfKey);
        if (dfKeyBasicConfigDO == null) {
            throw new ResourceNotFoundException("data facet key config is not found!");
        }
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dfKeyBasicConfigDO.getFkDbId());
        switch (dbBasicConfigDO.getDbType()) {
            case Constants.DATABASE_TYPE_MYSQL:
                return mysqlDataSourceComponent.refreshDfFormTableSettingProcess(dfKey, dfKeyBasicConfigDO);
            case Constants.DATABASE_TYPE_POSTGRESQL:
                return null;
            default:
                return mysqlDataSourceComponent.refreshDfFormTableSettingProcess(dfKey, dfKeyBasicConfigDO);
        }
    }


    @Override
    public DfKeyBasicConfigDO getDfKeyBasicInfo(String dfKey) throws Exception {
        return this.dfKeyBasicConfigDao.getDfKeyBasicConfigByDfKey(dfKey);
    }


}
