package ai.sparklabinc.poi;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

/**
 * @author: zhangxw
 * @date: 2018/2/26 14:21
 */
public class CommonExcelWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExcelWriter.class);

    /**
     * 创建Excel
     *
     * @param outputStream
     * @param headers
     * @param sheetName
     * @param converter
     */
    public static void wirteCommonData(OutputStream outputStream,
                                       String[] headers,
                                       String sheetName,
                                       RowUnitConverter converter) {
        /**
         * 当数据量达到1000时，写入磁盘
         */
        Workbook workbook = new SXSSFWorkbook(1000);
        CellStyle cellStyle = applyContentRowStyle(workbook);
        // 写入数据
        Iterable<RowUnit> cellUnitIterable = converter.convert();
        if (cellUnitIterable == null) {
            return;
        }
        Iterator<RowUnit> iterator = cellUnitIterable.iterator();

        int count = 0;
        Sheet sheet = null;
        while (iterator.hasNext()) {
            RowUnit rowUnit = iterator.next();
            int pageIndex = count / 65535;
            int rowSize = count % 65535;

            /*超过65535个的时候, 创建新的Sheet*/
            if (rowSize == 0 || sheet == null) {
                sheet = createSheet(workbook, sheetName, pageIndex + 1, headers);
            }
            if (sheet != null) {
                Row row = sheet.createRow(rowUnit.getRowIndex());
                List<String> cellValues = rowUnit.getCellValues();
                if (cellValues == null) {
                    return;
                }
                for (int i = 0; i < cellValues.size(); i++) {
                    fillCellToRow(row, i, cellValues.get(i), cellStyle);
                }
            }
            count++;
        }
        try {
            if (count > 0) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            LOGGER.error("write excel failed", e);
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                LOGGER.error("OutputStream Close Failed");
            }
        }
    }

    public static void appendCommonData(OutputStream outputStream,
                                         String[] headers,
                                         String sheetName,
                                         RowUnitConverter converter) {

        Iterable<RowUnit> cellUnitIterable = converter.convert();
        if (cellUnitIterable == null) {
            return;
        }
        Workbook workbook = new SXSSFWorkbook(1000);
        CellStyle cellStyle = applyContentRowStyle(workbook);
        int numberOfSheets = workbook.getNumberOfSheets();
        Sheet sheet = null;
        if (numberOfSheets <= 0) {
            sheet = createSheet(workbook, sheetName, 0, headers);
        }
        int activeSheetIndex = workbook.getActiveSheetIndex();
        sheet = workbook.getSheetAt(activeSheetIndex);
        // 写入数据
        Iterator<RowUnit> iterator = cellUnitIterable.iterator();
        if (sheet == null) {
            sheet = createSheet(workbook, sheetName, activeSheetIndex, headers);
        }

        // 当前sheet的行数
        int currentRowNum = sheet.getLastRowNum();
        // 当前处理的总行数
        while (iterator.hasNext()) {
            RowUnit rowUnit = iterator.next();
            /*超过100万个的时候, 创建新的Sheet*/
            if (currentRowNum >= 1000000) {
                activeSheetIndex = activeSheetIndex + 1;
                sheet = createSheet(workbook, sheetName, activeSheetIndex, headers);
                // count从头计算
                currentRowNum = 0;
            }
            if (sheet != null) {
                Row row = null;
                if (headers == null || headers.length <=0) {
                    row = sheet.createRow(rowUnit.getRowIndex());
                } else  {
                    row = sheet.createRow(rowUnit.getRowIndex() + 1);
                }
                List<String> cellValues = rowUnit.getCellValues();
                if (cellValues == null) {
                    return;
                }
                for (int i = 0; i < cellValues.size(); i++) {
                    fillCellToRow(row, i, cellValues.get(i), cellStyle);
                }
            }
            currentRowNum ++;
        }

        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            LOGGER.error("write excel failed", e);
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                LOGGER.error("OutputStream Close Failed");
            }
        }
    }

    private static Sheet createSheet(Workbook workbook,
                                     String sheetName,
                                     Integer index,
                                     String[] headers) {
        if (sheetName == null || sheetName.isEmpty()) {
            sheetName = "Sheet";
        }
        Sheet sheet = workbook.createSheet(sheetName + index);

        sheet.setDefaultColumnWidth(20);
        // 写入第一行
        if (headers != null && headers.length > 0) {
            Row row = sheet.createRow(0);
            CellStyle cellStyle = applyTitleRowStyle(workbook);
            for (int i = 0; i < headers.length; i++) {
                fillCellToRow(row, i, headers[i], cellStyle);
            }
        }
        workbook.setActiveSheet(index);
        return sheet;
    }

    private static void fillCellToRow(Row row, int column, String value, CellStyle cellStyle) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
    }

    private static CellStyle applyTitleRowStyle(Workbook workbook) {
        // 生成一个样式
        CellStyle titleRowStyle = workbook.createCellStyle();

        // 设置这些样式
        titleRowStyle.setFillForegroundColor((short) 40);
        titleRowStyle.setFillPattern((short) 1);
        titleRowStyle.setBorderBottom((short) 1);
        titleRowStyle.setBorderLeft((short) 1);
        titleRowStyle.setBorderRight((short) 1);
        titleRowStyle.setBorderTop((short) 1);
        titleRowStyle.setAlignment(CellStyle.ALIGN_CENTER);//水平居中
        titleRowStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);//垂直居中
        // 生成一个字体
        Font titleRowFont = workbook.createFont();
        titleRowFont.setColor((short) 20);
        titleRowFont.setFontHeightInPoints((short) 12);
        titleRowFont.setBoldweight((short) 700);
        // 把字体应用到当前的样式
        titleRowStyle.setFont(titleRowFont);

        return titleRowStyle;
    }

    private static CellStyle applyContentRowStyle(Workbook workbook) {
        // 生成并设置另一个样式
        CellStyle contentRowStyle = workbook.createCellStyle();
        // HSSFColor
        contentRowStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        contentRowStyle.setFillPattern((short) 1);
        contentRowStyle.setBorderBottom((short) 1);
        contentRowStyle.setBorderLeft((short) 1);
        contentRowStyle.setBorderRight((short) 1);
        contentRowStyle.setBorderTop((short) 1);
        contentRowStyle.setAlignment((short) 2);
        contentRowStyle.setVerticalAlignment((short) 1);
        // 生成另一个字体
        Font contentRowFont = workbook.createFont();
        contentRowFont.setFontHeightInPoints((short) 10);
        // 把字体应用到当前的样式
        contentRowStyle.setFont(contentRowFont);

        return contentRowStyle;
    }
}
