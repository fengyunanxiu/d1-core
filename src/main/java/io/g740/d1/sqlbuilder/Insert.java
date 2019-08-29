package io.g740.d1.sqlbuilder;

import io.g740.d1.util.DateUtils;
import io.g740.d1.util.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/29 11:00
 * @description :
 */
public class Insert {

    private static final Logger LOGGER = LoggerFactory.getLogger(Insert.class);

    public SQL single(List<BeanParser.ColumnNode> columnNodeList, String tableName) {
        if (columnNodeList == null || columnNodeList.isEmpty()) {
            return null;
        }
        List<String> fieldNameList = new ArrayList<>();
        List<Object> fieldValueList = new ArrayList<>();
        List<String> fieldValuePlaceholderList = new ArrayList<>();
        for (int i = 0; i < columnNodeList.size(); i++) {
            BeanParser.ColumnNode columnNode = columnNodeList.get(i);
            boolean id = columnNode.isId();
            if (id) {
                GenerateType generateType = columnNode.getGenerateType();
                switch (generateType) {
                    case AUTO:
                        // 自增主键，insert 排除掉该字段
                        continue;
                    case UUID:
                        // 主键设置为uuid
                        columnNode.setColumnValue(UUIDUtils.compress());
                        break;
                    default:
                        break;
                }
            }
            fieldNameList.add(columnNode.getColumnName());
            Object fieldValue = columnNode.getColumnValue();
            fieldValueList.add(fieldValue);
            fieldValuePlaceholderList.add("?");
        }
        StringBuilder sql = new StringBuilder(" insert into ")
                .append(tableName)
                .append(" ( ")
                .append(String.join(",", fieldNameList))
                .append(" ) ")
                .append(" values ( ")
                .append(String.join(",", fieldValuePlaceholderList))
                .append(" ) ");

        return new SQL(sql.toString(), fieldNameList, fieldValueList);
    }

}
