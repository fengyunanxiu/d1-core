package ai.sparklabinc.d1.service.impl;

import ai.sparklabinc.d1.component.MysqlDataSourceComponent;
import ai.sparklabinc.d1.dao.DataSourceDao;
import ai.sparklabinc.d1.dao.DbBasicConfigDao;
import ai.sparklabinc.d1.dao.DfFormTableSettingDao;
import ai.sparklabinc.d1.dao.DfKeyBasicConfigDao;
import ai.sparklabinc.d1.datasource.Constants;
import ai.sparklabinc.d1.dto.DbInformationDTO;
import ai.sparklabinc.d1.dto.DfKeyBasicConfigDTO;
import ai.sparklabinc.d1.dto.TableColumnsDetailDTO;
import ai.sparklabinc.d1.entity.DbBasicConfigDO;
import ai.sparklabinc.d1.entity.DfFormTableSettingDO;
import ai.sparklabinc.d1.entity.DfKeyBasicConfigDO;
import ai.sparklabinc.d1.exception.custom.IllegalParameterException;
import ai.sparklabinc.d1.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.d1.service.DataFacetKeyService;
import ai.sparklabinc.d1.service.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.rmi.ServerException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @function:
 * @author: DAM
 * @date: 2019/8/8 9:45
 * @description:
 * @version: V1.0
 */
@Service
public class DataFacetKeyServiceImpl implements DataFacetKeyService {

    private final static Logger LOGGER=LoggerFactory.getLogger(DataFacetKeyServiceImpl.class);

    @Resource(name = "DbBasicConfigDao")
    private DbBasicConfigDao dbBasicConfigDao;

    @Resource(name = "DataSourceDao")
    private DataSourceDao dataSourceDao;

    @Resource(name = "DfKeyBasicConfigDao")
    private DfKeyBasicConfigDao dfKeyBasicConfigDao;

    @Resource(name = "DfFormTableSettingDao")
    private DfFormTableSettingDao dfFormTableSettingDao;

    @Autowired
    private MysqlDataSourceComponent mysqlDataSourceComponent;

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public DbInformationDTO addDataFacetKey(DfKeyBasicConfigDTO dfKeyBasicConfigDTO) throws Exception {
        long start=System.currentTimeMillis();
        //是否能够连接上真实的数据库
        dataSourceService.Connection2DataSource(dfKeyBasicConfigDTO.getFkDbId());

        DfKeyBasicConfigDO dfKeyBasicConfigByDfKey = dfKeyBasicConfigDao.getDfKeyBasicConfigByDfKey(dfKeyBasicConfigDTO.getDfKey());
        //新加的df key 是否已经存在
        if (dfKeyBasicConfigByDfKey != null) {
            throw new IllegalParameterException("data facet key already exists!");
        }
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dfKeyBasicConfigDTO.getFkDbId());
        LOGGER.info("valid spend time：{}",System.currentTimeMillis()-start);
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
        List<TableColumnsDetailDTO> tableColumnsDetailDTOList = dataSourceDao.selectTableColumnsDetail(dfKeyBasicConfigDO.getFkDbId(),
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
    public void updateDataFacetKey(String dfKey, String newDfKey, String description) throws Exception {
        boolean updateResult = false;
        DfKeyBasicConfigDO dfKeyBasicConfigByDfKey = dfKeyBasicConfigDao.getDfKeyBasicConfigByDfKey(newDfKey);
        //新加的df key 是否已经存在
        if (dfKeyBasicConfigByDfKey != null) {
            throw new IllegalParameterException("data facet key already exists!");
        }
        int updateRows = dfKeyBasicConfigDao.updateDataFacetKey(dfKey, newDfKey, description);
        if (updateRows > 0) {
            updateRows = dfFormTableSettingDao.updateDataFacetKey(dfKey, newDfKey);
            if (updateRows > 0) {
                updateResult = true;
            }
        }
        if(!updateResult){
          throw new ServerException("update data facet key is failed") ;
        }

    }

    @Override
    public void deleteDataFacetKey(String dfKey) throws IOException, SQLException {
        boolean updateResult = false;
        int updateRows = dfKeyBasicConfigDao.deleteDataFacetKey(dfKey);
        if (updateRows > 0) {
            updateRows = dfFormTableSettingDao.deleteDataFacetKey(dfKey);
            if (updateRows > 0) {
                updateResult = true;
            }
        }
        if(!updateResult){
            throw new ServerException("delete data facet key is failed") ;
        }
    }

    @Override
    public void saveDfFormTableSetting(List<DfFormTableSettingDO> dfFormTableSettingDOSForUpdate, List<DfFormTableSettingDO> dfFormTableSettingDOSForAdd) throws Exception {
        //更新操作
        if (!CollectionUtils.isEmpty(dfFormTableSettingDOSForUpdate)) {
            for (DfFormTableSettingDO dfFormTableSettingDO : dfFormTableSettingDOSForUpdate) {
                Integer updateResult = dfFormTableSettingDao.updateDfFormTableSetting(dfFormTableSettingDO);
                if (updateResult <= 0) {
                    throw new ServerException("save form table setting is failed") ;
                }
            }
        }
        //添加操作
        if (!CollectionUtils.isEmpty(dfFormTableSettingDOSForAdd)) {
            Integer add = dfFormTableSettingDao.batchAdd(dfFormTableSettingDOSForAdd);
            if (add <= 0) {
                throw new ServerException("save form table setting is failed") ;
            }
        }
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

    /**
     * 写入默认值
     *
     * @param dfKey
     * @param fieldKey
     * @param jsonValue
     * @throws Exception
     */
    @Override
    public void updateDefaultValueByDfKeyAndFieldKey(String dfKey, String fieldKey, String jsonValue) throws Exception {
        this.dfFormTableSettingDao.updateDefaultValueByDfKeyAndFieldName(dfKey, fieldKey, jsonValue);
    }

    /**
     * 写入字典domain和item
     *
     * @param dfKey
     * @param fieldName
     * @param domain
     * @param item
     * @throws Exception
     */
    @Override
    public void updateDomainAndItemByDfKeyAndFieldName(String dfKey, String fieldName, String domain, String item) throws Exception {
        this.dfFormTableSettingDao.updateDomainAndItemByDfKeyAndFieldName(dfKey, fieldName, domain, item);
    }
}
