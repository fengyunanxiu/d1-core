package ai.sparklabinc.service.impl;

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
import com.jcraft.jsch.Session;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    public boolean addDataSources(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws IOException, SQLException {
        DbBasicConfigDO dbBasicConfigDO = new DbBasicConfigDO();
        BeanUtils.copyProperties(dbBasicConfigDTO, dbBasicConfigDO);

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
            Integer add = dbSecurityConfigDao.add(dbSecurityConfigDO);
            if (add > 0) {
                return true;
            }

        }
        return false;

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

        if(dsId != null){
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
    public boolean addDataSourceKey(DsKeyBasicConfigDTO dsKeyBasicConfigDTO) throws Exception {
        DsKeyBasicConfigDO dsKeyBasicConfigByDsKey = dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dsKeyBasicConfigDTO.getDsKey());
        //新加的ds key 是否已经存在
        if (dsKeyBasicConfigByDsKey != null) {
            throw new IllegalParameterException("data source key already exists!");
        }

        boolean addResult = false;
        DsKeyBasicConfigDO dsKeyBasicConfigDO = new DsKeyBasicConfigDO();
        BeanUtils.copyProperties(dsKeyBasicConfigDTO, dsKeyBasicConfigDO);
        int addRow = dsKeyBasicConfigDao.addDataSourceKey(dsKeyBasicConfigDO);
        if (addRow > 0) {
            addResult = true;
            //添加data source key form table setting 配置信息
            List<TableColumnsDetailDTO> tableColumnsDetailDTOList = mysqlDataSourceDao.selectTableColumnsDetail(dsKeyBasicConfigDO.getFkDbId(),
                    dsKeyBasicConfigDO.getSchema(),
                    dsKeyBasicConfigDO.getTableName());
            if (CollectionUtils.isEmpty(tableColumnsDetailDTOList)) {
                return addResult;
            }

            DsFormTableSettingDO dsFormTableSettingDO = null;
            for (TableColumnsDetailDTO tableColumnsDetailDTO : tableColumnsDetailDTOList) {
                dsFormTableSettingDO = new DsFormTableSettingDO();

                dsFormTableSettingDO.setDsKey(dsKeyBasicConfigDO.getDsKey());
                dsFormTableSettingDO.setDbFieldName(tableColumnsDetailDTO.getColumnName());
                dsFormTableSettingDO.setDbFieldType(tableColumnsDetailDTO.getDataType());

                String columnName = tableColumnsDetailDTO.getColumnName();
                dsFormTableSettingDO.setViewFieldLabel(getLabelName(columnName));
                dsFormTableSettingDO.setDbFieldComment(tableColumnsDetailDTO.getColumnComment());
                dsFormTableSettingDO.setFormFieldVisible(true);
                dsFormTableSettingDO.setFormFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                dsFormTableSettingDO.setFormFieldQueryType(FormTableSettingConstants.FormType.TEXT.toString());

                dsFormTableSettingDO.setFormFieldIsExactly(true);
                //dsFormTableSettingDO.setFormFieldChildrenDbFieldName();
                //dsFormTableSettingDO.setFormFieldDicDomainName();
                dsFormTableSettingDO.setFormFieldUseDic(false);
                //dsFormTableSettingDO.getFormFieldDefaultValStratege();

                dsFormTableSettingDO.setTableFieldVisible(true);
                dsFormTableSettingDO.setTableFieldOrderBy(FormTableSettingConstants.OrderBy.NONE.toString());
                dsFormTableSettingDO.setTableFieldQueryRequired(true);
                dsFormTableSettingDO.setTableFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                dsFormTableSettingDO.setTableFieldColumnWidth(100);

                dsFormTableSettingDO.setExportFieldVisible(true);
                dsFormTableSettingDO.setExportFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                dsFormTableSettingDO.setExportFieldWidth(20);
                //dsFormTableSettingDO.setTableParentLabel();
                dsFormTableSettingDO.setFormFieldUseDefaultVal(true);

                dsFormTableSettingDO.setColumnIsExist(true);

                dsFormTableSettingDao.add(dsFormTableSettingDO);
            }
        }

        return addResult;
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
        if(CollectionUtils.isEmpty(colunmNames)){
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
    public boolean updateDsFormTableSetting(DsFormTableSettingDO dsFormTableSettingDO) throws IOException, SQLException {
        Integer updateResult = dsFormTableSettingDao.updateDsFormTableSetting(dsFormTableSettingDO);
        return updateResult > 0 ? true : false;
    }

    @Override
    public List<Map<String, Object>> RefreshDsFormTableSetting(String dsKey) throws Exception {
        DsKeyBasicConfigDO dsKeyBasicConfigDO = dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dsKey);
        if (dsKeyBasicConfigDO == null) {
            throw new ResourceNotFoundException("data source key config is not found!");
        }

        //获dsKey FormTableSetting的信息
        List<DsFormTableSettingDO> allDsFormTableSettingByDsKey = dsFormTableSettingDao.getAllDsFormTableSettingByDsKey(dsKey);

        //从ddl语句中获取table columns setting的配置信息
        List<TableColumnsDetailDTO> tableColumnsDetailDTOList = mysqlDataSourceDao.selectTableColumnsDetail(dsKeyBasicConfigDO.getFkDbId(),
                dsKeyBasicConfigDO.getSchema(),
                dsKeyBasicConfigDO.getTableName());
        if (CollectionUtils.isEmpty(tableColumnsDetailDTOList)) {
            throw new ResourceNotFoundException("table or view probably was removed！");
        }

        //真实表中不存在的字段设置为不存在
        for(DsFormTableSettingDO dsFormTableSettingDO:allDsFormTableSettingByDsKey){
            List<String> collect = tableColumnsDetailDTOList.stream()
                    .filter(e -> e.getColumnName().equalsIgnoreCase(dsFormTableSettingDO.getDbFieldName()))
                    .map(TableColumnsDetailDTO::getColumnName)
                    .collect(Collectors.toList());
            if(CollectionUtils.isEmpty(collect)){
                dsFormTableSettingDO.setColumnIsExist(false);
                dsFormTableSettingDao.updateDsFormTableSetting(dsFormTableSettingDO);
            }
        }

        for (TableColumnsDetailDTO tableColumnsDetailDTO : tableColumnsDetailDTOList) {
            boolean columnIsExist = false;
            for (DsFormTableSettingDO dsFormTableSettingDO : allDsFormTableSettingByDsKey) {
                if (tableColumnsDetailDTO.getColumnName().equalsIgnoreCase(dsFormTableSettingDO.getDbFieldName())) {
                    columnIsExist = true;
                    //如果存在，更新最新的值
                    dsFormTableSettingDO.setDbFieldType(tableColumnsDetailDTO.getDataType());
                    dsFormTableSettingDO.setColumnIsExist(true);
                    //dsFormTableSettingDO.setDbFieldComment(tableColumnsDetailDTO.getColumnComment());
                    dsFormTableSettingDao.updateDsFormTableSetting(dsFormTableSettingDO);
                }
            }
            //如果不存在，则加入配置
            if (!columnIsExist) {
                DsFormTableSettingDO dsFormTableSettingDO = new DsFormTableSettingDO();

                dsFormTableSettingDO.setDsKey(dsKeyBasicConfigDO.getDsKey());
                dsFormTableSettingDO.setDbFieldName(tableColumnsDetailDTO.getColumnName());
                dsFormTableSettingDO.setDbFieldType(tableColumnsDetailDTO.getDataType());

                String columnName = tableColumnsDetailDTO.getColumnName();
                dsFormTableSettingDO.setViewFieldLabel(getLabelName(columnName));
                dsFormTableSettingDO.setDbFieldComment(tableColumnsDetailDTO.getColumnComment());
                dsFormTableSettingDO.setFormFieldVisible(true);
                dsFormTableSettingDO.setFormFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                dsFormTableSettingDO.setFormFieldQueryType(FormTableSettingConstants.FormType.TEXT.toString());

                dsFormTableSettingDO.setFormFieldIsExactly(true);
                //dsFormTableSettingDO.setFormFieldChildrenDbFieldName();
                //dsFormTableSettingDO.setFormFieldDicDomainName();
                dsFormTableSettingDO.setFormFieldUseDic(false);
                //dsFormTableSettingDO.getFormFieldDefaultValStratege();

                dsFormTableSettingDO.setTableFieldVisible(true);
                dsFormTableSettingDO.setTableFieldOrderBy(FormTableSettingConstants.OrderBy.NONE.toString());
                dsFormTableSettingDO.setTableFieldQueryRequired(true);
                dsFormTableSettingDO.setTableFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                dsFormTableSettingDO.setTableFieldColumnWidth(100);

                dsFormTableSettingDO.setExportFieldVisible(true);
                dsFormTableSettingDO.setExportFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                dsFormTableSettingDO.setExportFieldWidth(20);
                //dsFormTableSettingDO.setTableParentLabel();
                dsFormTableSettingDO.setFormFieldUseDefaultVal(true);

                dsFormTableSettingDO.setColumnIsExist(true);

                dsFormTableSettingDao.add(dsFormTableSettingDO);
            }
        }

        return dsFormTableSettingDao.selectAllDsFormTableSettingByDsKey(dsKey);
    }

    @Override
    public DsKeyBasicConfigDO getDsKeyBasicInfo(String dsKey) throws Exception {
        return this.dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dsKey);
    }


    /**
     * 获取labelName
     *
     * @param columnName
     * @return
     */
    private String getLabelName(String columnName) {
        StringBuilder labelNameSb = new StringBuilder();
        String[] words = columnName.split("_");
        for (String word : words) {
            String firstCharUpperString = getFirstCharUpperString(word);
            labelNameSb.append(firstCharUpperString);
            labelNameSb.append(" ");
        }
        labelNameSb.deleteCharAt(labelNameSb.length() - 1);
        return labelNameSb.toString();
    }


    /**
     * 字符串首字符大写
     *
     * @param str
     * @return
     */
    private String getFirstCharUpperString(String str) {
        String string = "";
        char[] chars = str.toCharArray();
        if (chars[0] >= 'a' && chars[0] <= 'z') {
            chars[0] = (char) (chars[0] - 32);
        }
        string = new String(chars);
        return string;
    }


}
