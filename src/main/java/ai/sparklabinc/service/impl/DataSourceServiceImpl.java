package ai.sparklabinc.service.impl;

import ai.sparklabinc.component.MysqlDataSourceComponent;
import ai.sparklabinc.constant.DsConstants;
import ai.sparklabinc.constant.FormTableSettingConstants;
import ai.sparklabinc.dao.*;
import ai.sparklabinc.datasource.ConnectionService;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.dto.*;
import ai.sparklabinc.entity.DbBasicConfigDO;
import ai.sparklabinc.entity.DbSecurityConfigDO;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.entity.DsKeyBasicConfigDO;
import ai.sparklabinc.exception.custom.IllegalParameterException;
import ai.sparklabinc.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.service.DataSourceService;
import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.Session;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private DbBasicConfigDao dbBasicConfigDao;

    @Autowired
    private DbSecurityConfigDao dbSecurityConfigDao;

    @Autowired
    private MysqlDataSourceDao mysqlDataSourceDao;

    @Autowired
    private DsKeyBasicConfigDao dsKeyBasicConfigDao;

    @Autowired
    private DsFormTableSettingDao dsFormTableSettingDao;

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
        dbBasicConfigDO.setUrl(urlSuffix);

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
                dbInforamtionDTO.setLabel(dbBasicConfigDO.getName());
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
    public List<DbInforamtionDTO> selectDataSources(Long dsId, Integer dsKeyFilter) throws IOException, SQLException {

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
                 * step4 拿到所有schema所有的表和视图,还有所有的data source key，提高性能
                 * *******************************************************************
                 */
                //所有schema所有的表和视图
                List<TableAndViewInfoDTO> tableAndViewInfoDTOS = mysqlDataSourceDao.selectAllTableAndView(dsId);
                //获取所有的data source key
                List<DsKeyInfoDTO> allDataSourceKey = dsKeyBasicConfigDao.getAllDataSourceKey();

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
                     * step5 拿到表和视图的data source key
                     * *******************************************************************
                     */

                    getDsKeyOfTableAndView(dsId, dsKeyFilter, schema, tableAndViews, allDataSourceKey);

                    //加入 table and view
                    schema.setChildren(tableAndViews);
                }

                //如果选了过滤ds key则过滤掉没有tableAndView 为空的schema和ds
                if (dsKeyFilter == 1 || dsKeyFilter == 2) {
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


    private void getDsKeyOfTableAndView(Long dsId, Integer dsKeyFilter, DbInforamtionDTO schema, List<DbInforamtionDTO> tableAndViews, List<DsKeyInfoDTO> allDataSourceKey) throws IOException, SQLException {
        Iterator<DbInforamtionDTO> tableAndViewsIterator = tableAndViews.iterator();
        while (tableAndViewsIterator.hasNext()) {
            DbInforamtionDTO tableAndView = tableAndViewsIterator.next();
            //从内存中拿数据筛选
            List<DsKeyInfoDTO> dsKeyInfoDTOList = allDataSourceKey.stream()
                    .filter(e -> e.getFkDbId().equals(dsId)
                            && e.getSchema().equals(schema.getLabel())
                            && e.getTableName().equals(tableAndView.getLabel()))
                    .collect(Collectors.toList());
            List<DbInforamtionDTO> dataSourceKeys = new LinkedList<>();
            //封装数据
            dsKeyInfoDTOList.forEach(e -> {
                DbInforamtionDTO dbInforamtionDTO = new DbInforamtionDTO();
                dbInforamtionDTO.setLevel(e.getLevel());
                dbInforamtionDTO.setLabel(e.getLabel());
                dbInforamtionDTO.setId(e.getId());
                dataSourceKeys.add(dbInforamtionDTO);
            });
            //List<DbInforamtionDTO> dataSourceKeys = dsKeyBasicConfigDao.getDataSourceKey(dsId, schema.getLabel(), tableAndView.getLabel());
            switch (dsKeyFilter) {
                case 1:
                    if (CollectionUtils.isEmpty(dataSourceKeys)) {
                        tableAndViewsIterator.remove();
                    }
                    tableAndView.setChildren(dataSourceKeys);
                    break;
                case 2:
                    if (!CollectionUtils.isEmpty(dataSourceKeys)) {
                        tableAndViewsIterator.remove();
                    }
                    break;
                default:
                    if (CollectionUtils.isEmpty(dataSourceKeys)) {
                        continue;
                    }
                    tableAndView.setChildren(dataSourceKeys);
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
        dbBasicConfigDO.setUrl(urlSuffix);

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
    public DbInforamtionDTO addDataSourceKey(DsKeyBasicConfigDTO dsKeyBasicConfigDTO) throws Exception {
        DsKeyBasicConfigDO dsKeyBasicConfigByDsKey = dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dsKeyBasicConfigDTO.getDsKey());
        //新加的ds key 是否已经存在
        if (dsKeyBasicConfigByDsKey != null) {
            throw new IllegalParameterException("data source key already exists!");
        }
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dsKeyBasicConfigDTO.getFkDbId());
        switch (dbBasicConfigDO.getType()) {
            case Constants.DATABASE_TYPE_MYSQL:
                return mysqlDataSourceComponent.addDataSourceKeyProcess(dsKeyBasicConfigDTO);
            case Constants.DATABASE_TYPE_POSTGRESQL:
                return null;
            default:
                return mysqlDataSourceComponent.addDataSourceKeyProcess(dsKeyBasicConfigDTO);
        }
    }


    @Override
    public List<Map<String, Object>> selectAllDsFormTableSettingByDsKey(String dsKey) throws Exception {
        List<Map<String, Object>> allDsFormTableSettingByDsKey = dsFormTableSettingDao.selectAllDsFormTableSettingByDsKey(dsKey);
        DsKeyBasicConfigDO dsKeyBasicConfigDO = dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dsKey);
        if (dsKeyBasicConfigDO == null) {
            throw new ResourceNotFoundException("ds config is not found!");
        }
        //获取data source key真实的table字段
        List<TableColumnsDetailDTO> tableColumnsDetailDTOList = mysqlDataSourceDao.selectTableColumnsDetail(dsKeyBasicConfigDO.getFkDbId(),
                dsKeyBasicConfigDO.getSchema(),
                dsKeyBasicConfigDO.getTableName());

        List<String> colunmNames = tableColumnsDetailDTOList.stream()
                .map(TableColumnsDetailDTO::getColumnName)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(colunmNames)) {
            throw new ResourceNotFoundException("table is not exist");
        }

        allDsFormTableSettingByDsKey.forEach(e -> {
            if (!colunmNames.contains(e.get("db_field_name"))) {
                e.put("column_is_exist", 0);
            } else {
                e.put("column_is_exist", 1);
            }
        });
        return allDsFormTableSettingByDsKey;
    }


    @Override
    public boolean updateDataSourceKey(String dsKey, String newDsKey, String description) throws IOException, SQLException {
        boolean updateResult = false;
        int updateRows = dsKeyBasicConfigDao.updateDataSourceKey(dsKey, newDsKey, description);
        if (updateRows > 0) {
            updateRows = dsFormTableSettingDao.updateDataSourceKey(dsKey, newDsKey);
            if (updateRows > 0) {
                updateResult = true;
            }
        }
        return updateResult;
    }

    @Override
    public boolean deleteDataSourceKey(String dsKey) throws IOException, SQLException {
        boolean updateResult = false;
        int updateRows = dsKeyBasicConfigDao.deleteDataSourceKey(dsKey);
        if (updateRows > 0) {
            updateRows = dsFormTableSettingDao.deleteDataSourceKey(dsKey);
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
    public Boolean saveDsFormTableSetting(List<DsFormTableSettingDO> dsFormTableSettingDOSForUpdate,List<DsFormTableSettingDO> dsFormTableSettingDOSForAdd) throws Exception{
        //更新操作
        if(!CollectionUtils.isEmpty(dsFormTableSettingDOSForUpdate)){
            for(DsFormTableSettingDO dsFormTableSettingDO:dsFormTableSettingDOSForUpdate){
                Integer updateResult = dsFormTableSettingDao.updateDsFormTableSetting(dsFormTableSettingDO);
                if(updateResult<=0){
                    return false;
                }
            }
        }

        //添加操作
        if(!CollectionUtils.isEmpty(dsFormTableSettingDOSForAdd)){
            for(DsFormTableSettingDO dsFormTableSettingDO:dsFormTableSettingDOSForAdd){
                Integer add = dsFormTableSettingDao.add(dsFormTableSettingDO);
                if(add<=0){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public List<Map<String, Object>> refreshDsFormTableSetting(String dsKey) throws Exception {
        DsKeyBasicConfigDO dsKeyBasicConfigDO = dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dsKey);
        if (dsKeyBasicConfigDO == null) {
            throw new ResourceNotFoundException("data source key config is not found!");
        }
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dsKeyBasicConfigDO.getFkDbId());
        switch (dbBasicConfigDO.getType()) {
            case Constants.DATABASE_TYPE_MYSQL:
                return mysqlDataSourceComponent.refreshDsFormTableSettingProcess(dsKey, dsKeyBasicConfigDO);
            case Constants.DATABASE_TYPE_POSTGRESQL:
                return null;
            default:
                return mysqlDataSourceComponent.refreshDsFormTableSettingProcess(dsKey, dsKeyBasicConfigDO);
        }
    }


    @Override
    public DsKeyBasicConfigDO getDsKeyBasicInfo(String dsKey) throws Exception {
        return this.dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dsKey);
    }


}
