package ai.sparklabinc.dao.impl;

import ai.sparklabinc.dao.DsFormTableSettingDao;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.entity.DsFormTableSettingDO;
import ai.sparklabinc.util.DateUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : Kingzer
 * @date : 2019-07-03 07:46
 * @description :
 */
@Repository
public class DsFormTableSettingDaoImpl implements DsFormTableSettingDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DsFormTableSettingDaoImpl.class);
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    public List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKey(String dataSourceKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,0L));
        String querySql = "select * from ds_form_table_setting where ds_key = ? ";
        LOGGER.info("querySql:{}", querySql);
        List<DsFormTableSettingDO>  dsFormTableSettingDOList = queryRunner.query(querySql, new ResultSetHandler<List<DsFormTableSettingDO>>() {
            @Override
            public List<DsFormTableSettingDO> handle(ResultSet resultSet) throws SQLException {
                List<DsFormTableSettingDO>  dsFormTableSettingDOS = new ArrayList<>();
                DsFormTableSettingDO dsFormTableSettingDO = null;
                while (resultSet.next()) {
                    Long id =  resultSet.getLong("id");
                    String gmtCreateStr =  resultSet.getString("gmt_create");
                    String gmtModifiedStr = resultSet.getString("gmt_modified");
                    String dsKey = resultSet.getString("ds_key");
                    String dbFieldName = resultSet.getString("db_field_name");
                    String dbFieldType = resultSet.getString("db_field_type");
                    String viewFieldLabel = resultSet.getString("view_field_label");
                    String dbFieldComment = resultSet.getString("db_field_comment");
                    Boolean formFieldVisible = resultSet.getBoolean("form_field_visible");
                    Integer formFieldSequence = resultSet.getInt("form_field_sequence");
                    String formFieldQueryType = resultSet.getString("form_field_query_type");
                    Boolean formFieldIsExactly = resultSet.getBoolean("form_field_is_exactly");
                    String formFieldChildrenDbFieldName = resultSet.getString("form_field_children_db_field_name");
                    String formFieldDicDomainName = resultSet.getString("form_field_dic_domain_name");
                    Boolean formFieldUseDic = resultSet.getBoolean("form_field_use_dic");
                    String formFieldDefalutValStratege = resultSet.getString("form_field_defalut_val_stratege");
                    Boolean tableFieldVisible = resultSet.getBoolean("table_field_visible");
                    String tableFiedldOrderBy = resultSet.getString("table_field_order_by");
                    Boolean tableFieldQueryRequired = resultSet.getBoolean("table_field_query_required");
                    Integer tableFieldSequence = resultSet.getInt("table_field_sequence");
                    Integer tableFieldColumnWidth= resultSet.getInt("table_field_column_width");
                    Boolean exportFieldVisible= resultSet.getBoolean("export_field_visible");
                    Integer exportFieldSequence = resultSet.getInt("export_field_sequence");
                    Integer exportFieldWidth= resultSet.getInt("export_field_width");
                    String tableParentLabel = resultSet.getString("table_parent_label");

                    Boolean formFieldUseDefaultVal = resultSet.getBoolean("form_field_use_default_val");
                    String formFieldManMadeDefaultVal = resultSet.getString("form_field_man_made_default_val");
                    String formFieldDefaultValSql = resultSet.getString("form_field_default_val_sql");
                    Boolean columIsExist = resultSet.getBoolean("column_is_exist");

                    dsFormTableSettingDO = new DsFormTableSettingDO();
                    dsFormTableSettingDO.setId(id);
                    dsFormTableSettingDO.setGmtCreate(gmtCreateStr);
                    dsFormTableSettingDO.setGmtModified(gmtModifiedStr);
                    dsFormTableSettingDO.setDsKey(dsKey);
                    dsFormTableSettingDO.setDbFieldName(dbFieldName);
                    dsFormTableSettingDO.setDbFieldType(dbFieldType);
                    dsFormTableSettingDO.setViewFieldLabel(viewFieldLabel);
                    dsFormTableSettingDO.setDbFieldComment(dbFieldComment);
                    dsFormTableSettingDO.setFormFieldVisible(formFieldVisible);
                    dsFormTableSettingDO.setFormFieldSequence(formFieldSequence);
                    dsFormTableSettingDO.setFormFieldQueryType(formFieldQueryType);
                    dsFormTableSettingDO.setFormFieldIsExactly(formFieldIsExactly);
                    dsFormTableSettingDO.setFormFieldChildrenDbFieldName(formFieldChildrenDbFieldName);
                    dsFormTableSettingDO.setFormFieldDicDomainName(formFieldDicDomainName);
                    dsFormTableSettingDO.setFormFieldUseDic(formFieldUseDic);
                    dsFormTableSettingDO.setFormFieldDefaultValStratege(formFieldDefalutValStratege);
                    dsFormTableSettingDO.setTableFieldVisible(tableFieldVisible);
                    dsFormTableSettingDO.setTableFieldOrderBy(tableFiedldOrderBy);
                    dsFormTableSettingDO.setTableFieldQueryRequired(tableFieldQueryRequired);
                    dsFormTableSettingDO.setTableFieldSequence(tableFieldSequence);
                    dsFormTableSettingDO.setTableFieldColumnWidth(tableFieldColumnWidth);
                    dsFormTableSettingDO.setExportFieldVisible(exportFieldVisible);
                    dsFormTableSettingDO.setExportFieldSequence(exportFieldSequence);
                    dsFormTableSettingDO.setExportFieldWidth(exportFieldWidth);
                    dsFormTableSettingDO.setTableParentLabel(tableParentLabel);
                    dsFormTableSettingDO.setFormFieldUseDefaultVal(formFieldUseDefaultVal);
                    dsFormTableSettingDO.setFormFieldManMadeDefaultVal(formFieldManMadeDefaultVal);
                    dsFormTableSettingDO.setFormFieldDefaultValSql(formFieldDefaultValSql);
                    dsFormTableSettingDO.setColumnIsExist(columIsExist);

                    dsFormTableSettingDOS.add(dsFormTableSettingDO);
                }
                return dsFormTableSettingDOS;
            }
        },dataSourceKey);
        return dsFormTableSettingDOList;
    }


    @Override
    public Integer add(DsFormTableSettingDO dsFormTableSettingDO) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,null));
        String sql ="insert into ds_form_table_setting(gmt_create, gmt_modified, ds_key, db_field_name, db_field_type," +
                " view_field_label, db_field_comment, form_field_visible, form_field_sequence, form_field_query_type," +
                " form_field_is_exactly, form_field_children_db_field_name, form_field_dic_domain_name, form_field_use_dic, form_field_defalut_val_stratege," +
                " table_field_visible, table_field_order_by, table_field_query_required, table_field_sequence, table_field_column_width," +
                " export_field_visible, export_field_sequence, export_field_width,table_parent_label,form_field_use_default_val," +
                " form_field_man_made_default_val,form_field_default_val_sql,column_is_exist)" +
                " values (?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?, ?)";
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectParams={now, now,
                dsFormTableSettingDO.getDsKey(),
                dsFormTableSettingDO.getDbFieldName(),
                dsFormTableSettingDO.getDbFieldType(),

                dsFormTableSettingDO.getViewFieldLabel(),
                dsFormTableSettingDO.getDbFieldComment(),
                dsFormTableSettingDO.getFormFieldVisible()?1:0,
                dsFormTableSettingDO.getFormFieldSequence(),
                dsFormTableSettingDO.getFormFieldQueryType(),

                dsFormTableSettingDO.getFormFieldIsExactly()?1:0,
                dsFormTableSettingDO.getFormFieldChildrenDbFieldName(),
                dsFormTableSettingDO.getFormFieldDicDomainName(),
                dsFormTableSettingDO.getFormFieldUseDic()?1:0,
                dsFormTableSettingDO.getFormFieldDefaultValStratege(),

                dsFormTableSettingDO.getTableFieldVisible()?1:0,
                dsFormTableSettingDO.getTableFieldOrderBy(),
                dsFormTableSettingDO.getTableFieldQueryRequired()?1:0,
                dsFormTableSettingDO.getTableFieldSequence(),
                dsFormTableSettingDO.getTableFieldColumnWidth(),

                dsFormTableSettingDO.getExportFieldVisible()?1:0,
                dsFormTableSettingDO.getExportFieldSequence(),
                dsFormTableSettingDO.getExportFieldWidth(),
                dsFormTableSettingDO.getTableParentLabel(),
                dsFormTableSettingDO.getFormFieldUseDefaultVal()?1:0,

                dsFormTableSettingDO.getFormFieldManMadeDefaultVal(),
                dsFormTableSettingDO.getFormFieldDefaultValSql(),
                dsFormTableSettingDO.getColumnIsExist()?1:0
                };
        LOGGER.info("insert sql:{}",sql);
        int result = queryRunner.update(sql, objectParams);
        return  result;
    }

    @Override
    public List<Map<String, Object>> selectAllDsFormTableSettingByDsKey(String dataSourceKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null));
        String querySql = "select * from ds_form_table_setting where ds_key = ? ";
        LOGGER.info("querySql:{}", querySql);
        List<Map<String, Object>> result = queryRunner.query(querySql, new MapListHandler(),dataSourceKey);
        return result;
    }


    @Override
    public Integer updateDataSourceKey(String dataSourceKey,String newDataSourceKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null));
        String querySql = "update ds_form_table_setting set gmt_modified = ?, ds_key = ?" +
                          "where ds_key = ?";
        LOGGER.info("querySql:{}", querySql);
        String now = DateUtils.ofLongStr(new java.util.Date());
        int update = queryRunner.update(querySql, now, newDataSourceKey, dataSourceKey);
        return update;
    }

    @Override
    public Integer deleteDataSourceKey(String dsKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE, null));
        String sql="delete from ds_form_table_setting where ds_key = ?";
        int update = queryRunner.update(sql, dsKey);
        return update;
    }


    @Override
    public List<DsFormTableSettingDO> getAllDsFormTableSettingByDsKeyForExport(String dataSourceKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,null));
        String querySql = "" +
                "select id as  id," +
                "  gmt_create as  gmtCreate," +
                "  gmt_modified as  gmtModified ," +
                "  ds_key as  dsKey ," +
                "  db_field_name as  dbFieldName ," +
                "  db_field_type as  dbFieldType ," +
                "  view_field_label as  viewFieldLabel ," +
                "  db_field_comment as  dbFieldComment ," +
                "  form_field_visible as  formFieldVisible," +
                "  form_field_sequence as  formFieldSequence ," +
                "  form_field_query_type as  formFieldQueryType," +
                "  form_field_is_exactly as  formFieldIsExactly ," +
                "  form_field_children_db_field_name as  formFieldChildrenDbFieldName ," +
                "  form_field_dic_domain_name as  formFieldDicDomainName ," +
                "  form_field_use_dic as  formFieldUseDic ," +
                "  form_field_defalut_val_stratege as  formFieldDefalutValStratege ," +
                "  table_field_visible as  tableFieldVisible," +
                "  table_field_order_by as  tableFieldOrderBy ," +
                "  table_field_query_required as  tableFieldQueryRequired," +
                "  table_field_sequence as  tableFieldSequence," +
                "  table_field_column_width as  tableFieldColumnWidth," +
                "  export_field_visible as  exportFieldVisible," +
                "  export_field_sequence as  exportFieldSequence," +
                "  export_field_width as  exportFieldWidth," +
                "  table_parent_label as  tableParentLabel," +
                "  form_field_use_default_val as  formFieldUseDefaultVal," +
                "  form_field_man_made_default_val as  formFieldManMadeDefaultVal," +
                "  form_field_default_val_sql as formFieldDefaultValSql ," +
                "  column_is_exist as columIsExist" +
                "from ds_form_table_setting where ds_key = ? and exportFieldVisible = ?";
        LOGGER.info("querySql:{}", querySql);
        List<DsFormTableSettingDO> dsFormTableSettingDOList = queryRunner.query(querySql, new BeanListHandler<>(DsFormTableSettingDO.class), dataSourceKey, 1);
        return dsFormTableSettingDOList;
    }


    @Override
    public Integer updateDsFormTableSetting(DsFormTableSettingDO dsFormTableSettingDO) throws SQLException, IOException {
        int result=0;
        QueryRunner queryRunner = new QueryRunner(dataSourceFactory.builder(Constants.DATABASE_TYPE_SQLITE,null));
        String updateSql = "update ds_form_table_setting " +
                "set gmt_modified = ?," +
                "   ds_key = ?," +
                "   db_field_name = ?," +
                "   db_field_type = ?," +
                "   view_field_label = ?," +
                "   db_field_comment = ?," +
                "   form_field_visible = ?," +
                "   form_field_sequence = ?," +
                "   form_field_query_type = ?," +
                "   form_field_is_exactly = ?," +
                "   form_field_children_db_field_name = ?," +
                "   form_field_dic_domain_name = ?," +
                "   form_field_use_dic = ?," +
                "   form_field_defalut_val_stratege = ?," +
                "   table_field_visible = ?," +
                "   table_field_order_by = ?," +
                "   table_field_query_required = ?," +
                "   table_field_sequence = ?," +
                "   table_field_column_width = ?," +
                "   export_field_visible = ?," +
                "   export_field_sequence = ?," +
                "   export_field_width = ?," +
                "   table_parent_label = ?," +
                "   form_field_use_default_val = ?," +
                "   form_field_man_made_default_val = ?," +
                "   form_field_default_val_sql = ?," +
                "   column_is_exist = ?" +
                " where id = ?";
        LOGGER.info("updateSql:{}", updateSql);
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectParams={now,
                dsFormTableSettingDO.getDsKey(),
                dsFormTableSettingDO.getDbFieldName(),
                dsFormTableSettingDO.getDbFieldType(),
                dsFormTableSettingDO.getViewFieldLabel(),

                dsFormTableSettingDO.getDbFieldComment(),
                dsFormTableSettingDO.getFormFieldVisible()?1:0,
                dsFormTableSettingDO.getFormFieldSequence(),
                dsFormTableSettingDO.getFormFieldQueryType(),
                dsFormTableSettingDO.getFormFieldIsExactly()?1:0,

                dsFormTableSettingDO.getFormFieldChildrenDbFieldName(),
                dsFormTableSettingDO.getFormFieldDicDomainName(),
                dsFormTableSettingDO.getFormFieldUseDic()?1:0,
                dsFormTableSettingDO.getFormFieldDefaultValStratege(),
                dsFormTableSettingDO.getTableFieldVisible()?1:0,

                dsFormTableSettingDO.getTableFieldOrderBy(),
                dsFormTableSettingDO.getTableFieldQueryRequired()?1:0,
                dsFormTableSettingDO.getTableFieldSequence(),
                dsFormTableSettingDO.getTableFieldColumnWidth(),
                dsFormTableSettingDO.getExportFieldVisible()?1:0,

                dsFormTableSettingDO.getExportFieldSequence(),
                dsFormTableSettingDO.getExportFieldWidth(),
                dsFormTableSettingDO.getTableParentLabel(),
                dsFormTableSettingDO.getFormFieldUseDefaultVal()?1:0,

                dsFormTableSettingDO.getFormFieldManMadeDefaultVal(),
                dsFormTableSettingDO.getFormFieldDefaultValSql(),
                dsFormTableSettingDO.getColumnIsExist()?1:0,
                dsFormTableSettingDO.getId()
        };
        result = queryRunner.update(updateSql, objectParams);
        return result;
    }





}
