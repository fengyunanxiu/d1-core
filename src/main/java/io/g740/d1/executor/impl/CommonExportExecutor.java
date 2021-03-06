package io.g740.d1.executor.impl;

import io.g740.d1.entity.DfFormTableSettingDO;
import io.g740.d1.exception.custom.ResourceNotFoundException;
import io.g740.d1.executor.ExportExecutor;
import io.g740.d1.poi.CommonExcelWriter;
import io.g740.d1.poi.RowUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

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
 * @function:
 * @author:   dengam
 * @date:    2019/7/18 17:24
 * @param:
 * @return:
 */
public class CommonExportExecutor implements ExportExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExportExecutor.class);


    @Override
    public File exportExcel(DataSource dataSource, String querySql, List<Object> paramList, List<DfFormTableSettingDO> queryTableSettings, Path path) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        File file = null;
        try {
            /**
             * 按照顺序
             */
            List<String> fieldKeyList = queryTableSettings.stream()
                    .sorted(Comparator.comparing(DfFormTableSettingDO::getExportFieldSequence))
                    .map(DfFormTableSettingDO::getDbFieldName).collect(Collectors.toList());
            //表头
            List<String> fieldAliasLabelList = queryTableSettings.stream()
                    .sorted(Comparator.comparing(DfFormTableSettingDO::getExportFieldSequence))
                    .map(DfFormTableSettingDO::getViewFieldLabel).collect(Collectors.toList());

            //设置默认宽度
            for (DfFormTableSettingDO e:queryTableSettings) {
                if(e.getExportFieldWidth()==null||e.getExportFieldWidth()<=0){
                    e.setExportFieldWidth(15);
                }
            }
            //表格列宽度
            List<Integer> fieldAliasLabelWidth = queryTableSettings.stream()
                    .sorted(Comparator.comparing(DfFormTableSettingDO::getExportFieldSequence))
                    .map(DfFormTableSettingDO::getExportFieldWidth).collect(Collectors.toList());

            String filePath = path.toString();
            file = new File(filePath);
            connection = dataSource.getConnection();
//            connection.setNetworkTimeout(Executors.newFixedThreadPool(1), 3600000);
            preparedStatement = connection.prepareStatement(querySql);
            //绑定参数
            if(!CollectionUtils.isEmpty(paramList)){
                bindParameters(preparedStatement,paramList.toArray());
            }
            preparedStatement.setQueryTimeout(3600);
            resultSet = preparedStatement.executeQuery();
            List<Map<String, String>> cacheRowMapList = new ArrayList<>();
            if(resultSet==null){
                throw new ResourceNotFoundException("can not find any data for export");
            }
            while(resultSet.next()){
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (String fieldKey : fieldKeyList) {
                    try {
                        String value = resultSet.getString(fieldKey);
                        rowMap.put(fieldKey, value);
                    }catch (Exception e){
                        rowMap.put(fieldKey,null);
                    }
                }
                cacheRowMapList.add(rowMap);
            }

            if (cacheRowMapList.size() > 0) {
                // 写入Excel
                write2Excel(cacheRowMapList, queryTableSettings, file, fieldAliasLabelList, fieldAliasLabelWidth);
                cacheRowMapList.clear();
            }
        } catch (Exception e) {
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

    /**
     * 绑定参数方法
     *
     * @param stmt
     * @param params
     * @throws SQLException
     */
    private void bindParameters(PreparedStatement stmt, Object... params) throws SQLException {
        //绑定参数
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }


    private void write2Excel(List<Map<String, String>> cacheRowMapList, List<DfFormTableSettingDO> queryTableSettings,
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
