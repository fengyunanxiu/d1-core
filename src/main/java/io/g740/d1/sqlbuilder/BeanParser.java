package io.g740.d1.sqlbuilder;

import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.engine.SQLEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/28 17:51
 * @description :
 */
public class BeanParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanParser.class);

    public static <T> List<ColumnNode> buildNode(Class<T> clazz, T t) throws Exception {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<ColumnNode> columnNodeList = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Column[] annotationArray = declaredField.getDeclaredAnnotationsByType(Column.class);
            if (annotationArray == null || annotationArray.length == 0) {
                continue;
            }
            ColumnNode columnNode = new ColumnNode();
            Column annotation = annotationArray[0];
            Type.Java javaType = annotation.javaType();
            Type.SQL sqlType = annotation.sqlType();
            String columnName = annotation.value();
            Id idAnnotation = declaredField.getDeclaredAnnotation(Id.class);
            if (idAnnotation != null) {
                GenerateType generateType = idAnnotation.generateType();
                columnNode.setId(true);
                columnNode.setGenerateType(generateType);
            }
            // 转换类型
            Object columnValue = declaredField.get(t);
            if (columnValue != null) {
                columnValue = Type.typeTransform(javaType, columnValue);
            }
            columnNode.setColumnName(columnName);
            columnNode.setColumnValue(columnValue);
            columnNode.setSqlType(sqlType);
            columnNode.setJavaType(javaType);
            columnNodeList.add(columnNode);
        }
        return columnNodeList;
    }

    public static <T> String parseTableName(Class<T> clazz) throws Exception {
        Table[] tableAnnotationArray = clazz.getDeclaredAnnotationsByType(Table.class);
        if (tableAnnotationArray == null || tableAnnotationArray.length == 0) {
            throw new Exception("must have table annotation");
        }
        Table table = tableAnnotationArray[0];
        String tableName = table.value();
        return tableName;
    }

    public static <T> String getIdFieldName(Class<T> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> idFieldList = Arrays.stream(declaredFields).filter((field) -> field.getAnnotation(Id.class) != null).collect(Collectors.toList());
        if (idFieldList != null && !idFieldList.isEmpty()) {
            return idFieldList.get(0).getName();
        }
        return null;
    }

    public static <T> List<ColumnNode> buildNode(Class<T> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<ColumnNode> columnNodeList = new ArrayList<>();
        for (int i = 0; i < declaredFields.length; i++) {
            Field declaredField = declaredFields[i];
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            if (columnAnnotation == null) {
                continue;
            }
            Id idAnnotation = declaredField.getAnnotation(Id.class);
            ColumnNode columnNode = new ColumnNode();
            if (idAnnotation != null) {
                columnNode.setId(true);
                columnNode.setGenerateType(idAnnotation.generateType());
            } else {
                columnNode.setId(false);
            }
            columnNode.setColumnName(columnAnnotation.value());
            columnNode.setJavaType(columnAnnotation.javaType());
            columnNode.setSqlType(columnAnnotation.sqlType());
            columnNode.setFieldName(declaredField.getName());
            columnNodeList.add(columnNode);
        }
        return columnNodeList;
    }

    public static class ColumnNode {
        private boolean id;
        private GenerateType generateType;
        private String fieldName;
        private String columnName;
        private Object columnValue;
        private Type.SQL sqlType;
        private Type.Java javaType;

        public boolean isId() {
            return id;
        }

        public void setId(boolean id) {
            this.id = id;
        }

        public GenerateType getGenerateType() {
            return generateType;
        }

        public void setGenerateType(GenerateType generateType) {
            this.generateType = generateType;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public Object getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(Object columnValue) {
            this.columnValue = columnValue;
        }

        public Type.SQL getSqlType() {
            return sqlType;
        }

        public void setSqlType(Type.SQL sqlType) {
            this.sqlType = sqlType;
        }

        public Type.Java getJavaType() {
            return javaType;
        }

        public void setJavaType(Type.Java javaType) {
            this.javaType = javaType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
    }

    public static void main(String[] args) throws Exception {
        BeanParser beanParser = new BeanParser();
        DictDO dictDO = new DictDO();
        dictDO.setFieldId("123");
        dictDO.setFieldDomain("test domain");
        dictDO.setFieldItem("test item");

        SQLEngine sqlEngine = new SQLEngine();
    }

}
