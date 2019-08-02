package ai.sparklabinc.d1.component;

import ai.sparklabinc.d1.constant.FormTableSettingConstants;
import ai.sparklabinc.d1.dao.DataSourceDao;
import ai.sparklabinc.d1.dao.DsFormTableSettingDao;
import ai.sparklabinc.d1.dao.DsKeyBasicConfigDao;
import ai.sparklabinc.d1.dto.DbInforamtionDTO;
import ai.sparklabinc.d1.dto.DsKeyBasicConfigDTO;
import ai.sparklabinc.d1.dto.TableColumnsDetailDTO;
import ai.sparklabinc.d1.entity.DsFormTableSettingDO;
import ai.sparklabinc.d1.entity.DsKeyBasicConfigDO;
import ai.sparklabinc.d1.exception.custom.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
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
 * @date: 2019/7/23 10:22
 * @description:
 * @version: V1.0
 */
@Component
public class MysqlDataSourceComponent {


    @Autowired
    @Qualifier("DsKeyBasicConfigDao")
    private DsKeyBasicConfigDao dsKeyBasicConfigDao;

    @Resource(name="DataSourceDao")
    private DataSourceDao dataSourceDao;

    @Resource(name = "DsFormTableSettingDao")
    private DsFormTableSettingDao dsFormTableSettingDao;

    public DbInforamtionDTO addDataSourceKeyProcess(DsKeyBasicConfigDTO dsKeyBasicConfigDTO) throws Exception {
        DsKeyBasicConfigDO dsKeyBasicConfigDO = new DsKeyBasicConfigDO();
        BeanUtils.copyProperties(dsKeyBasicConfigDTO, dsKeyBasicConfigDO);
        Long dsId = dsKeyBasicConfigDao.addDataSourceKeyAndReturnId(dsKeyBasicConfigDO);

        DbInforamtionDTO dbInforamtionDTO = new DbInforamtionDTO();
        dbInforamtionDTO.setId(dsId);
        dbInforamtionDTO.setLevel(4);
        dbInforamtionDTO.setLabel(dsKeyBasicConfigDTO.getDsKey());

        if (dsId != null) {

            //添加data source key form table setting 配置信息
            List<TableColumnsDetailDTO> tableColumnsDetailDTOList = dataSourceDao.selectTableColumnsDetail(dsKeyBasicConfigDO.getFkDbId(),
                    dsKeyBasicConfigDO.getSchemaName(),
                    dsKeyBasicConfigDO.getTableName());
            if (CollectionUtils.isEmpty(tableColumnsDetailDTOList)) {
                return dbInforamtionDTO;
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
                        dsFormTableSettingDO.setTableFieldColumnWidth(350);
                    } else {
                        dsFormTableSettingDO.setTableFieldColumnWidth(columnLength.intValue());
                    }
                } else if (tableColumnsDetailDTO.getDataType().equalsIgnoreCase("datetime")) {
                    dsFormTableSettingDO.setTableFieldColumnWidth(150);
                } else {
                    dsFormTableSettingDO.setTableFieldColumnWidth(100);
                }

                dsFormTableSettingDO.setExportFieldVisible(true);
                dsFormTableSettingDO.setExportFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                //计算导出列宽
                if (tableColumnsDetailDTO.getCharacterMaximumLength() != null &&
                        tableColumnsDetailDTO.getCharacterMaximumLength() > 0) {
                    int length = tableColumnsDetailDTO.getColumnName().length();
                    Long columnLength = tableColumnsDetailDTO.getCharacterMaximumLength();
                    //表头和列内容长度比较，以大的为长度
                    columnLength = columnLength >= length ? columnLength : length;
                    if (columnLength > 100) {
                        dsFormTableSettingDO.setExportFieldWidth(100);
                    } else {
                        dsFormTableSettingDO.setExportFieldWidth(columnLength.intValue());
                    }
                } else {
                    dsFormTableSettingDO.setExportFieldWidth(20);
                }
                //dsFormTableSettingDO.setTableParentLabel();
                dsFormTableSettingDO.setFormFieldUseDefaultVal(true);

                dsFormTableSettingDO.setColumnIsExist(true);

                dsFormTableSettingDao.add(dsFormTableSettingDO);
            }
        }
        return dbInforamtionDTO;
    }


    public List<Map<String, Object>> refreshDsFormTableSettingProcess(String dsKey, DsKeyBasicConfigDO dsKeyBasicConfigDO) throws SQLException, IOException, ResourceNotFoundException {
        //获dsKey FormTableSetting的信息
        List<DsFormTableSettingDO> allDsFormTableSettingByDsKey = dsFormTableSettingDao.getAllDsFormTableSettingByDsKey(dsKey);

        //从ddl语句中获取table columns setting的配置信息
        List<TableColumnsDetailDTO> tableColumnsDetailDTOList = dataSourceDao.selectTableColumnsDetail(dsKeyBasicConfigDO.getFkDbId(),
                dsKeyBasicConfigDO.getSchemaName(),
                dsKeyBasicConfigDO.getTableName());
        if (CollectionUtils.isEmpty(tableColumnsDetailDTOList)) {
            throw new ResourceNotFoundException("table or view probably was removed！");
        }

        //真实表中不存在的字段设置为不存在
        for (DsFormTableSettingDO dsFormTableSettingDO : allDsFormTableSettingByDsKey) {
            List<String> collect = tableColumnsDetailDTOList.stream()
                    .filter(e -> e.getColumnName().equalsIgnoreCase(dsFormTableSettingDO.getDbFieldName()))
                    .map(TableColumnsDetailDTO::getColumnName)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
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
                        dsFormTableSettingDO.setTableFieldColumnWidth(350);
                    } else {
                        dsFormTableSettingDO.setTableFieldColumnWidth(columnLength.intValue());
                    }
                } else if (tableColumnsDetailDTO.getDataType().equalsIgnoreCase("datetime")) {
                    dsFormTableSettingDO.setTableFieldColumnWidth(150);
                } else {
                    dsFormTableSettingDO.setTableFieldColumnWidth(100);
                }

                dsFormTableSettingDO.setExportFieldVisible(true);
                dsFormTableSettingDO.setExportFieldSequence(tableColumnsDetailDTO.getOrdinalPosition());
                //计算导出列宽
                if (tableColumnsDetailDTO.getCharacterMaximumLength() != null &&
                        tableColumnsDetailDTO.getCharacterMaximumLength() > 0) {
                    int length = tableColumnsDetailDTO.getColumnName().length();
                    Long columnLength = tableColumnsDetailDTO.getCharacterMaximumLength();
                    //表头和列内容长度比较，以大的为长度
                    columnLength = columnLength >= length ? columnLength : length;
                    if (columnLength > 100) {
                        dsFormTableSettingDO.setExportFieldWidth(100);
                    } else {
                        dsFormTableSettingDO.setExportFieldWidth(columnLength.intValue());
                    }
                } else {
                    dsFormTableSettingDO.setExportFieldWidth(20);
                }
                //dsFormTableSettingDO.setTableParentLabel();
                dsFormTableSettingDO.setFormFieldUseDefaultVal(true);

                dsFormTableSettingDO.setColumnIsExist(true);

                dsFormTableSettingDao.add(dsFormTableSettingDO);
            }
        }
        return dsFormTableSettingDao.selectAllDsFormTableSettingByDsKey(dsKey);
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
