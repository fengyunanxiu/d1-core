package io.g740.d1.service.impl;

import io.g740.d1.component.MysqlDataSourceComponent;
import io.g740.d1.constant.DsConstants;
import io.g740.d1.dao.DataSourceDao;
import io.g740.d1.dao.DbBasicConfigDao;
import io.g740.d1.dao.DfFormTableSettingDao;
import io.g740.d1.dao.DfKeyBasicConfigDao;
import io.g740.d1.datasource.Constants;
import io.g740.d1.defaults.dto.DefaultsConfigurationDTO;
import io.g740.d1.defaults.service.DefaultsConfigurationService;
import io.g740.d1.dict.entity.FormDictConfigurationDO;
import io.g740.d1.dict.service.FormDictConfigurationService;
import io.g740.d1.dto.DbInformationDTO;
import io.g740.d1.dto.DfKeyBasicConfigDTO;
import io.g740.d1.dto.TableColumnsDetailDTO;
import io.g740.d1.entity.DbBasicConfigDO;
import io.g740.d1.entity.DfFormTableSettingDO;
import io.g740.d1.entity.DfKeyBasicConfigDO;
import io.g740.d1.exception.custom.IllegalParameterException;
import io.g740.d1.exception.custom.ResourceNotFoundException;
import io.g740.d1.service.DataFacetKeyService;
import io.g740.d1.service.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.rmi.ServerException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import static io.g740.d1.entity.DfFormTableSettingDO.*;

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

    @Autowired
    private FormDictConfigurationService formDictConfigurationService;

    @Autowired
    private DefaultsConfigurationService defaultsConfigurationService;

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
        if (dfKeyBasicConfigByDfKey != null&&!dfKey.equals(newDfKey)) {
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
     * 写入默认值策略类型
     * @param dfKey
     * @param fieldKey
     * @param strategyType
     * @throws Exception
     */
    @Override
    public void updateDefaultValueStrategyType(String dfKey, String fieldKey, String strategyType) throws Exception {
        this.dfFormTableSettingDao.updateDefaultStrategyTypeByDfKeyAndFieldName(dfKey, fieldKey, strategyType);
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

    @Override
    public void saveDfFormTableSetting(List<DfFormTableSettingDO> dfFormTableSettingDOS) throws Exception {


        String dfKey = !CollectionUtils.isEmpty(dfFormTableSettingDOS)? dfFormTableSettingDOS.get(0).getDfKey():null;
        if(StringUtils.isEmpty(dfKey)){
            throw new IllegalParameterException("dfKey can not by empty");
        }
        Map<String,Map<String,Object>> oldDfFormTableSettingDOMap = this.generateMapList(dfKey);

        // 找到有改变的dictDTOS然后去改变其值（规则）;并且这里会做校验
        List<FormDictConfigurationDO> formDictConfigurationDOS = new LinkedList<>();
        List<DefaultsConfigurationDTO>  defaultsConfigurationDTOS = new LinkedList<>();
        this.validateAndGetDictAndDefaultChangeList(dfFormTableSettingDOS,oldDfFormTableSettingDOMap,formDictConfigurationDOS, defaultsConfigurationDTOS);


        List<DfFormTableSettingDO> dfFormTableSettingDOSForUpdate = dfFormTableSettingDOS.stream()
                .filter(e -> e.getId() != null && e.getId() > 0)
                .collect(Collectors.toList());

        List<DfFormTableSettingDO> dfFormTableSettingDOSForAdd = dfFormTableSettingDOS.stream()
                .filter((e) -> e.getId() == null || e.getId() <= 0)
                .collect(Collectors.toList());


        this.defaultsConfigurationService.saveBatchListForForm(defaultsConfigurationDTOS);
        this.saveDfFormTableSetting(dfFormTableSettingDOSForUpdate,dfFormTableSettingDOSForAdd);
        this.formDictConfigurationService.saveBatchList(formDictConfigurationDOS);


    }


    /**
     * 验证传值的正确性以及存储字典值和默认值
     * @param dfFormTableSettingDOS
     * @param oldDfFormTableSettingDOMap
     * @param formDictConfigurationDOS
     * @param defaultsConfigurationDTOS
     */
    private void validateAndGetDictAndDefaultChangeList(List<DfFormTableSettingDO> dfFormTableSettingDOS,Map<String,Map<String,Object>> oldDfFormTableSettingDOMap ,List<FormDictConfigurationDO> formDictConfigurationDOS, List<DefaultsConfigurationDTO> defaultsConfigurationDTOS) throws IllegalParameterException {
        for (DfFormTableSettingDO dfFormTableSettingDO : dfFormTableSettingDOS) {
            String formQueryType = dfFormTableSettingDO.getFormFieldQueryType();
            String dbFieldName = dfFormTableSettingDO.getDbFieldName();
            String dfKey = dfFormTableSettingDO.getDfKey();
            List<String> choiceTypeList = DsConstants.FormFieldQueryTypeEnum.getChoiceList();

            // 校验
            if(choiceTypeList.contains(formQueryType)){
                //下拉框类型  一定要用domain,一定不能有默认值
                if(StringUtils.isEmpty(dfFormTableSettingDO.getFormFieldDictDomainName()) || StringUtils.isEmpty(dfFormTableSettingDO.getFormFieldDictItem()) ){
                    throw new IllegalParameterException(String.format("Field Name like:%s's Element Type is %s,must set a Optional Values",dbFieldName,formQueryType));
                }
//              不在校验默认值，可以使用默认值
//                if( !StringUtils.isEmpty(dfFormTableSettingDO.getFormFieldDefaultVal()) || !StringUtils.isEmpty(dfFormTableSettingDO.getFormFieldDefValStrategy()) || dfFormTableSettingDO.getFormFieldUseDefaultVal() ){
//                    throw new IllegalParameterException(String.format("Field Name like:%s's Element Type is %s,can not use a default val",dbFieldName,formQueryType));
//                }
            }else{
                if(!StringUtils.isEmpty(dfFormTableSettingDO.getFormFieldDictDomainName()) || !StringUtils.isEmpty(dfFormTableSettingDO.getFormFieldDictItem()) ){
                    throw new IllegalParameterException(String.format("Field Name like:%s's Element Type is %s,can not set a Optional Values",dbFieldName,formQueryType));
                }
            }
            if(!StringUtils.isEmpty(dfFormTableSettingDO.getFormFieldChildFieldName()) && !DsConstants.FormFieldQueryTypeEnum.MULTIPLE_CHOICE_LIST.getVal().equals(formQueryType)){
                throw new IllegalParameterException(String.format("Field Name like:%s's Element Type is %s,can not set a Child Field Name",dbFieldName,formQueryType));
            }

            Map<String, Object> dfFormTableSettingDOMap =  oldDfFormTableSettingDOMap.get(dbFieldName);
            // 字典值(不再去根据id更新了),根据是否有fileId进行保存（现根据dictConfiguration判断有没有进行查询和保存，有的话再根据是否有id确定对应关系是否有存在原值，无就去掉，有的就加入）
            FormDictConfigurationDO dictConfiguration = dfFormTableSettingDO.getDictConfiguration();
            if(dictConfiguration != null){
                if(!StringUtils.isEmpty(dictConfiguration.getFieldId())){
                    formDictConfigurationDOS.add(dictConfiguration);
                }else{
                    // 进行过保存，如果有fileId说明以前保存过；如果没有保存过，就看有没有设置值，有值就保存
                    if(!StringUtils.isEmpty(dictConfiguration.getFieldDomain())){
                        formDictConfigurationDOS.add(dictConfiguration);
                    }
                }
            }else{
                // 如果没有进行查询和保存，这时候dictConfiguration 为空，需要验证以前是什么类型，  如果是下拉框类型，（如今是下拉框类型不需要处理因为说明没有更改，如果非下拉框类型就需要处理,表明清空了）
                String oldQueryType = Optional.ofNullable(dfFormTableSettingDOMap.get(F_FORM_FIELD_QUERY_TYPE)).map(Object::toString).orElse(null);
                if(choiceTypeList.contains(oldQueryType)){
                    if(!choiceTypeList.contains(formQueryType)){
                        FormDictConfigurationDO formDictConfigurationDO = new FormDictConfigurationDO();
                        formDictConfigurationDO.setFieldDomain(null);
                        formDictConfigurationDO.setFieldItem(null);
                        formDictConfigurationDO.setFieldFormDfKey(dfKey);
                        formDictConfigurationDO.setFieldFormFieldKey(dbFieldName);
                        formDictConfigurationDOS.add(formDictConfigurationDO);
                    }
                }
            }

            // 默认值策略  (如果save过，就直接保存（不管后续是否清空，这个时候值也会发生改变的(前端会进行处理的)）)
            DefaultsConfigurationDTO defaultsConfigurationDTO = dfFormTableSettingDO.getDefaultsConfiguration();
            if(defaultsConfigurationDTO != null){
                defaultsConfigurationDTOS.add(defaultsConfigurationDTO);
            }else{
               String oldDefaultValStrategy = Optional.ofNullable(dfFormTableSettingDOMap.get(F_FORM_FIELD_DEF_VAL_STRATEGY)).map(Object::toString).orElse(null);
               // 如果没有进行设置，以前如果有值而现在却没有表示有更改。否则就没有进行更改   (注意以前有，现在没有，肯定会经过保存的过程)
               if(!StringUtils.isEmpty(oldDefaultValStrategy) && StringUtils.isEmpty(dfFormTableSettingDO.getFormFieldDefValStrategy())){
                   DefaultsConfigurationDTO newDefaultsConfigurationDTO = new DefaultsConfigurationDTO();
                   newDefaultsConfigurationDTO.setFormDfKey(dfKey);
                   newDefaultsConfigurationDTO.setFormFieldKey(dbFieldName);
                   defaultsConfigurationDTOS.add(newDefaultsConfigurationDTO);
               }
            }
        }


    }



    /**
     * 获取数据库字段对应实体的map
     * @param dfKey
     * @return
     * @throws Exception
     */
    private Map<String,Map<String,Object>> generateMapList(String dfKey) throws Exception {
        Map<String,Map<String,Object>> dbFieldNameMap = new HashMap<>();
        List<Map<String, Object>> dbFileldResultList = this.selectAllDfFormTableSettingByDfKey(dfKey);

        for (Map<String, Object> dbFieldResult : dbFileldResultList) {
            String dbFieldName = Optional.ofNullable(dbFieldResult.get(F_DB_FIELD_NAME)).map(Object::toString).orElse(null);
            dbFieldNameMap.put(dbFieldName, dbFieldResult);
        }
        return  dbFieldNameMap;
    }



}
