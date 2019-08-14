package ai.sparklabinc.d1.component;

import ai.sparklabinc.d1.constant.DsConstants;
import ai.sparklabinc.d1.constant.FormTableSettingConstants;
import ai.sparklabinc.d1.dao.DataSourceDao;
import ai.sparklabinc.d1.dao.DbSecurityConfigDao;
import ai.sparklabinc.d1.dao.DfKeyBasicConfigDao;
import ai.sparklabinc.d1.dao.DfFormTableSettingDao;
import ai.sparklabinc.d1.datasource.Constants;
import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DfKeyBasicConfigDTO;
import ai.sparklabinc.d1.dto.TableColumnsDetailDTO;
import ai.sparklabinc.d1.entity.DfFormTableSettingDO;
import ai.sparklabinc.d1.entity.DfKeyBasicConfigDO;
import ai.sparklabinc.d1.exception.custom.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @function:
 * @author: DAM
 * @date: 2019/7/23 10:22
 * @description:
 * @version: V1.0
 */
@Component
public class MysqlDataSourceComponent {


    @Autowired
    @Qualifier("DfKeyBasicConfigDao")
    private DfKeyBasicConfigDao dfKeyBasicConfigDao;


    @Resource(name="DataSourceDao")
    private DataSourceDao dataSourceDao;

    @Resource(name = "DfFormTableSettingDao")
    private DfFormTableSettingDao dfFormTableSettingDao;

    public DbInforamtionDTO addDataFacetKeyProcess(DfKeyBasicConfigDTO dfKeyBasicConfigDTO) throws Exception {
        DfKeyBasicConfigDO dfKeyBasicConfigDO = new DfKeyBasicConfigDO();
        BeanUtils.copyProperties(dfKeyBasicConfigDTO, dfKeyBasicConfigDO);
        Long dsId = dfKeyBasicConfigDao.addDataFacetKeyAndReturnId(dfKeyBasicConfigDO);

        DbInforamtionDTO dbInforamtionDTO = new DbInforamtionDTO();
        dbInforamtionDTO.setId(dsId);
        dbInforamtionDTO.setLevel(4);
        dbInforamtionDTO.setLabel(dfKeyBasicConfigDTO.getDfKey());

        if (dsId != null) {

            //添加data facet key form table setting 配置信息
            List<TableColumnsDetailDTO> tableColumnsDetailDTOList = dataSourceDao.selectTableColumnsDetail(dfKeyBasicConfigDO.getFkDbId(),
                    dfKeyBasicConfigDO.getSchemaName(),
                    dfKeyBasicConfigDO.getTableName());
            if (CollectionUtils.isEmpty(tableColumnsDetailDTOList)) {
                return dbInforamtionDTO;
            }

            DfFormTableSettingDO dfFormTableSettingDO = null;
            List<DfFormTableSettingDO> dfFormTableSettingDOSAdd=new LinkedList<>();
            for (TableColumnsDetailDTO tableColumnsDetailDTO : tableColumnsDetailDTOList) {
                dfFormTableSettingDO = new DfFormTableSettingDO();

                dfFormTableSettingDO.setDfKey(dfKeyBasicConfigDO.getDfKey());
                dfFormTableSettingDO.setDbFieldName(tableColumnsDetailDTO.getColumnName());
                dfFormTableSettingDO.setDbFieldType(tableColumnsDetailDTO.getDataType());

                String columnName = tableColumnsDetailDTO.getColumnName();
                dfFormTableSettingDO.setViewFieldLabel(getLabelName(columnName));
                dfFormTableSettingDO.setDbFieldComment(tableColumnsDetailDTO.getColumnComment());
                dfFormTableSettingDO.setFormFieldVisible(true);
                dfFormTableSettingDO.setFormFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                dfFormTableSettingDO.setFormFieldQueryType(DsConstants.FormFieldQueryTypeEnum.EXACT_MATCHING_TEXT.getVal());

                //dfFormTableSettingDO.setFormFieldChildrenDbFieldName();
                //dfFormTableSettingDO.setFormFieldDictDomainName();
                //dfFormTableSettingDO.getFormFieldDefaultValStrategy();

                dfFormTableSettingDO.setTableFieldVisible(true);
                dfFormTableSettingDO.setTableFieldOrderBy(FormTableSettingConstants.OrderBy.NONE.toString());
                dfFormTableSettingDO.setTableFieldQueryRequired(true);
                dfFormTableSettingDO.setTableFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                //计算表格列宽
                if (tableColumnsDetailDTO.getCharacterMaximumLength() != null &&
                        tableColumnsDetailDTO.getCharacterMaximumLength() > 0) {
                    //因数
                    int factor = 10;
                    int length = tableColumnsDetailDTO.getColumnName().length();
                    Long columnLength = tableColumnsDetailDTO.getCharacterMaximumLength() * factor;
                    //表头和列内容长度比较，以大的为长度
                    columnLength = columnLength >= length ? columnLength : length;
                    if (columnLength > 350) {
                        dfFormTableSettingDO.setTableFieldColumnWidth(350);
                    } else {
                        dfFormTableSettingDO.setTableFieldColumnWidth(columnLength.intValue());
                    }
                } else if (tableColumnsDetailDTO.getDataType().equalsIgnoreCase("datetime")) {
                    dfFormTableSettingDO.setTableFieldColumnWidth(150);
                } else {
                    dfFormTableSettingDO.setTableFieldColumnWidth(100);
                }

                dfFormTableSettingDO.setExportFieldVisible(true);
                dfFormTableSettingDO.setExportFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                //计算导出列宽
                if (tableColumnsDetailDTO.getCharacterMaximumLength() != null &&
                        tableColumnsDetailDTO.getCharacterMaximumLength() > 0) {
                    int length = tableColumnsDetailDTO.getColumnName().length();
                    Long columnLength = tableColumnsDetailDTO.getCharacterMaximumLength();
                    //表头和列内容长度比较，以大的为长度
                    columnLength = columnLength >= length ? columnLength : length;
                    if (columnLength > 100) {
                        dfFormTableSettingDO.setExportFieldWidth(100);
                    } else {
                        dfFormTableSettingDO.setExportFieldWidth(columnLength.intValue());
                    }
                } else {
                    dfFormTableSettingDO.setExportFieldWidth(20);
                }
                //dfFormTableSettingDO.setTableParentLabel();
                dfFormTableSettingDO.setFormFieldUseDefaultVal(true);

                dfFormTableSettingDO.setColumnIsExist(true);

                dfFormTableSettingDOSAdd.add(dfFormTableSettingDO);
            }
            //入库
            if(!CollectionUtils.isEmpty(dfFormTableSettingDOSAdd)){
                dfFormTableSettingDao.batchAdd(dfFormTableSettingDOSAdd);
            }

        }
        return dbInforamtionDTO;
    }


    public List<Map<String, Object>> refreshDfFormTableSettingProcess(String dfKey, DfKeyBasicConfigDO dfKeyBasicConfigDO) throws Exception {
        //获dfKey FormTableSetting的信息
        List<DfFormTableSettingDO> allDfFormTableSettingByDfKey = dfFormTableSettingDao.getAllDfFormTableSettingByDfKey(dfKey);

        //从ddl语句中获取table columns setting的配置信息
        List<TableColumnsDetailDTO> tableColumnsDetailDTOList = dataSourceDao.selectTableColumnsDetail(dfKeyBasicConfigDO.getFkDbId(),
                dfKeyBasicConfigDO.getSchemaName(),
                dfKeyBasicConfigDO.getTableName());
        if (CollectionUtils.isEmpty(tableColumnsDetailDTOList)) {
            throw new ResourceNotFoundException("table or view probably was removed！");
        }

        //真实表中不存在的字段设置为不存在
        for (DfFormTableSettingDO dfFormTableSettingDO : allDfFormTableSettingByDfKey) {
            List<String> collect = tableColumnsDetailDTOList.stream()
                    .filter(e -> e.getColumnName().equalsIgnoreCase(dfFormTableSettingDO.getDbFieldName()))
                    .map(TableColumnsDetailDTO::getColumnName)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                dfFormTableSettingDO.setColumnIsExist(false);
                dfFormTableSettingDao.updateDfFormTableSetting(dfFormTableSettingDO);
            }
        }

        for (TableColumnsDetailDTO tableColumnsDetailDTO : tableColumnsDetailDTOList) {
            boolean columnIsExist = false;
            for (DfFormTableSettingDO dfFormTableSettingDO : allDfFormTableSettingByDfKey) {
                if (tableColumnsDetailDTO.getColumnName().equalsIgnoreCase(dfFormTableSettingDO.getDbFieldName())) {
                    columnIsExist = true;
                    //如果存在，更新最新的值
                    dfFormTableSettingDO.setDbFieldType(tableColumnsDetailDTO.getDataType());
                    dfFormTableSettingDO.setColumnIsExist(true);
                    //dfFormTableSettingDO.setDbFieldComment(tableColumnsDetailDTO.getColumnComment());
                    dfFormTableSettingDao.updateDfFormTableSetting(dfFormTableSettingDO);
                }
            }
            //如果不存在，则加入配置
            List<DfFormTableSettingDO> dfFormTableSettingDOSAdd=new LinkedList<>();
            if (!columnIsExist) {
                DfFormTableSettingDO dfFormTableSettingDO = new DfFormTableSettingDO();

                dfFormTableSettingDO.setDfKey(dfKeyBasicConfigDO.getDfKey());
                dfFormTableSettingDO.setDbFieldName(tableColumnsDetailDTO.getColumnName());
                dfFormTableSettingDO.setDbFieldType(tableColumnsDetailDTO.getDataType());

                String columnName = tableColumnsDetailDTO.getColumnName();
                dfFormTableSettingDO.setViewFieldLabel(getLabelName(columnName));
                dfFormTableSettingDO.setDbFieldComment(tableColumnsDetailDTO.getColumnComment());
                dfFormTableSettingDO.setFormFieldVisible(true);
                dfFormTableSettingDO.setFormFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                dfFormTableSettingDO.setFormFieldQueryType(FormTableSettingConstants.FormType.TEXT.toString());

                //dfFormTableSettingDO.setFormFieldChildrenDbFieldName();
                //dfFormTableSettingDO.setFormFieldDictDomainName();
                //dfFormTableSettingDO.getFormFieldDefaultValStrategy();

                dfFormTableSettingDO.setTableFieldVisible(true);
                dfFormTableSettingDO.setTableFieldOrderBy(FormTableSettingConstants.OrderBy.NONE.toString());
                dfFormTableSettingDO.setTableFieldQueryRequired(true);
                dfFormTableSettingDO.setTableFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                //计算表格列宽
                if (tableColumnsDetailDTO.getCharacterMaximumLength() != null &&
                        tableColumnsDetailDTO.getCharacterMaximumLength() > 0) {
                    //因数
                    int factor = 10;
                    int length = tableColumnsDetailDTO.getColumnName().length();
                    Long columnLength = tableColumnsDetailDTO.getCharacterMaximumLength() * factor;
                    //表头和列内容长度比较，以大的为长度
                    columnLength = columnLength >= length ? columnLength : length;
                    if (columnLength > 350) {
                        dfFormTableSettingDO.setTableFieldColumnWidth(350);
                    } else {
                        dfFormTableSettingDO.setTableFieldColumnWidth(columnLength.intValue());
                    }
                } else if (tableColumnsDetailDTO.getDataType().equalsIgnoreCase("datetime")) {
                    dfFormTableSettingDO.setTableFieldColumnWidth(150);
                } else {
                    dfFormTableSettingDO.setTableFieldColumnWidth(100);
                }

                dfFormTableSettingDO.setExportFieldVisible(true);
                dfFormTableSettingDO.setExportFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                //计算导出列宽
                if (tableColumnsDetailDTO.getCharacterMaximumLength() != null &&
                        tableColumnsDetailDTO.getCharacterMaximumLength() > 0) {
                    int length = tableColumnsDetailDTO.getColumnName().length();
                    Long columnLength = tableColumnsDetailDTO.getCharacterMaximumLength();
                    //表头和列内容长度比较，以大的为长度
                    columnLength = columnLength >= length ? columnLength : length;
                    if (columnLength > 100) {
                        dfFormTableSettingDO.setExportFieldWidth(100);
                    } else {
                        dfFormTableSettingDO.setExportFieldWidth(columnLength.intValue());
                    }
                } else {
                    dfFormTableSettingDO.setExportFieldWidth(20);
                }
                //dfFormTableSettingDO.setTableParentLabel();
                dfFormTableSettingDO.setFormFieldUseDefaultVal(true);

                dfFormTableSettingDO.setColumnIsExist(true);

                dfFormTableSettingDOSAdd.add(dfFormTableSettingDO);
            }
            //入库
            if(!CollectionUtils.isEmpty(dfFormTableSettingDOSAdd)){
                dfFormTableSettingDao.batchAdd(dfFormTableSettingDOSAdd);
            }
        }
        return dfFormTableSettingDao.selectAllDfFormTableSettingByDfKey(dfKey);
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
