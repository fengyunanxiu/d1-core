package ai.sparklabinc.executor.impl;

import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.executor.ExportExecutor;
import ai.sparklabinc.poi.CommonExcelWriter;
import ai.sparklabinc.poi.RowUnit;
import ai.sparklabinc.vo.DsKeyQueryTableSettingVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author :  zxiuwu
 * @date : 2019-03-21 15:32
 */
public class CommonExportExecutor implements ExportExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExportExecutor.class);


    @Override
    public File exportExcel(DataSource dataSource, String querySql, List<DsFormTableSettingDO> queryTableSettings, Path path) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        File file = null;
        try {
            /**
             * 按照顺序
             */
            List<String> fieldKeyList = queryTableSettings.stream()
                    .sorted(Comparator.comparing(DsFormTableSettingDO::getExportFieldSequence))
                    .map(DsFormTableSettingDO::getDbFieldName).collect(Collectors.toList());
            //表头
            List<String> fieldAliasLabelList = queryTableSettings.stream()
                    .sorted(Comparator.comparing(DsFormTableSettingDO::getExportFieldSequence))
                    .map(DsFormTableSettingDO::getViewFieldLabel).collect(Collectors.toList());

            //设置默认宽度
            queryTableSettings.forEach(e->{
                if(e.getExportFieldWidth()==null&&e.getExportFieldWidth()<=0){
                    e.setExportFieldWidth(15);
                }
            });
            //表格列宽度
            List<Integer> fieldAliasLabelWidth = queryTableSettings.stream()
                    .sorted(Comparator.comparing(DsFormTableSettingDO::getExportFieldSequence))
                    .map(DsFormTableSettingDO::getExportFieldWidth).collect(Collectors.toList());

            String filePath = path.toString();
            file = new File(filePath);
            connection = dataSource.getConnection();
            connection.setNetworkTimeout(Executors.newFixedThreadPool(1), 3600000);
            preparedStatement = connection.prepareStatement(querySql);
            preparedStatement.setQueryTimeout(3600);
            resultSet = preparedStatement.executeQuery();
            List<Map<String, String>> cacheRowMapList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (String fieldKey : fieldKeyList) {
                    String value = resultSet.getString(fieldKey);
                    rowMap.put(fieldKey, value);
                }
                cacheRowMapList.add(rowMap);
            }
            if (cacheRowMapList.size() > 0) {
                // 写入Excel
                write2Excel(cacheRowMapList, queryTableSettings, file, fieldAliasLabelList, fieldAliasLabelWidth);
                cacheRowMapList.clear();
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    LOGGER.error("", e);
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    LOGGER.error("", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.error("", e);
                }
            }
        }
        return file;
    }


    private void write2Excel(List<Map<String, String>> cacheRowMapList, List<DsFormTableSettingDO> queryTableSettings,
                             File file, List<String> fieldAliasLabelList, List<Integer> fieldAliasLabelWidth) {
        if (queryTableSettings != null && cacheRowMapList != null && !cacheRowMapList.isEmpty()) {

            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                CommonExcelWriter.appendCommonData(outputStream,
                        fieldAliasLabelList.toArray(new String[0]),
                        fieldAliasLabelWidth.toArray(new Integer[0]),
                        null, () -> {
                            List<RowUnit> rowUnits = new ArrayList<>();
                            for (int i = 0; i < cacheRowMapList.size(); i++) {
                                Map<String, String> rowMap = cacheRowMapList.get(i);
                                RowUnit rowUnit = new RowUnit();
                                List<String> values = new ArrayList<>(rowMap.values());
                                rowUnit.setRowIndex(i);
                                rowUnit.setCellValues(values);
                                rowUnits.add(rowUnit);
                            }
                            return rowUnits;
                        });
            } catch (FileNotFoundException e) {
                LOGGER.error("", e);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }
    }

}
