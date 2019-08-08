package ai.sparklabinc.d1.service.impl;

import ai.sparklabinc.d1.component.MysqlDataSourceComponent;
import ai.sparklabinc.d1.dao.*;
import ai.sparklabinc.d1.datasource.Constants;
import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DfKeyBasicConfigDTO;
import ai.sparklabinc.d1.dto.TableColumnsDetailDTO;
import ai.sparklabinc.d1.entity.DbBasicConfigDO;
import ai.sparklabinc.d1.entity.DfKeyBasicConfigDO;
import ai.sparklabinc.d1.entity.DsFormTableSettingDO;
import ai.sparklabinc.d1.entity.DsKeyBasicConfigDO;
import ai.sparklabinc.d1.exception.custom.IllegalParameterException;
import ai.sparklabinc.d1.exception.custom.ResourceNotFoundException;
import ai.sparklabinc.d1.service.DataFacetKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
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

    @Resource(name = "DbBasicConfigDao")
    private DbBasicConfigDao dbBasicConfigDao;

    @Resource(name = "DataSourceDao")
    private DataSourceDao dataSourceDao;

    @Resource(name = "DsKeyBasicConfigDao")
    private DsKeyBasicConfigDao dsKeyBasicConfigDao;

    @Resource(name = "DsFormTableSettingDao")
    private DsFormTableSettingDao dsFormTableSettingDao;

    @Autowired
    private MysqlDataSourceComponent mysqlDataSourceComponent;

    @Override
    public DbInforamtionDTO addDataSourceKey(DfKeyBasicConfigDTO dfKeyBasicConfigDTO) throws Exception {
        DsKeyBasicConfigDO dsKeyBasicConfigByDsKey = dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dsKeyBasicConfigDTO.getDsKey());
        //新加的ds key 是否已经存在
        if (dsKeyBasicConfigByDsKey != null) {
            throw new IllegalParameterException("data source key already exists!");
        }
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dfKeyBasicConfigDTO.getFkDbId());
        switch (dbBasicConfigDO.getDbType()) {
            case Constants.DATABASE_TYPE_MYSQL:
                return mysqlDataSourceComponent.addDataSourceKeyProcess(dfKeyBasicConfigDTO);
            case Constants.DATABASE_TYPE_POSTGRESQL:
                return null;
            default:
                return mysqlDataSourceComponent.addDataSourceKeyProcess(dfKeyBasicConfigDTO);
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
        List<TableColumnsDetailDTO> tableColumnsDetailDTOList = dataSourceDao.selectTableColumnsDetail(dsKeyBasicConfigDO.getFkDbId(),
                dsKeyBasicConfigDO.getSchemaName(),
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
    public Boolean saveDsFormTableSetting(List<DsFormTableSettingDO> dsFormTableSettingDOSForUpdate, List<DsFormTableSettingDO> dsFormTableSettingDOSForAdd) throws Exception{
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
    public List<Map<String, Object>> refreshDsFormTableSetting(String dfKey) throws Exception {
        DfKeyBasicConfigDO dfKeyBasicConfigDO = dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dfKey);
        if (dsKeyBasicConfigDO == null) {
            throw new ResourceNotFoundException("data source key config is not found!");
        }
        DbBasicConfigDO dbBasicConfigDO = dbBasicConfigDao.findById(dsKeyBasicConfigDO.getFkDbId());
        switch (dbBasicConfigDO.getDbType()) {
            case Constants.DATABASE_TYPE_MYSQL:
                return mysqlDataSourceComponent.refreshDsFormTableSettingProcess(dfKey, dsKeyBasicConfigDO);
            case Constants.DATABASE_TYPE_POSTGRESQL:
                return null;
            default:
                return mysqlDataSourceComponent.refreshDsFormTableSettingProcess(dfKey, dsKeyBasicConfigDO);
        }
    }


    @Override
    public DsKeyBasicConfigDO getDsKeyBasicInfo(String dsKey) throws Exception {
        return this.dsKeyBasicConfigDao.getDsKeyBasicConfigByDsKey(dsKey);
    }


}
