package ai.sparklabinc.d1.dao.impl.mysql;

import ai.sparklabinc.d1.dao.DataDaoType;
import ai.sparklabinc.d1.dao.DfFormTableSettingDao;
import ai.sparklabinc.d1.datasource.DataSourceFactory;
import ai.sparklabinc.d1.entity.DfFormTableSettingDO;
import ai.sparklabinc.d1.util.DateUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
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
@Repository("MysqlDfFormTableSettingDaoImpl")
public class MysqlDfFormTableSettingDaoImpl implements DfFormTableSettingDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDfFormTableSettingDaoImpl.class);
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Resource(name="D1BasicDataSource")
    private DataSource d1BasicDataSource;

    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.MYSQL;
    }

    @Override
    public List<DfFormTableSettingDO> getAllDfFormTableSettingByDfKey(String dataFacetKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String querySql = "select * from df_form_table_setting where df_key = ? ";
        LOGGER.info("querySql:{}", querySql);
        List<DfFormTableSettingDO> dfFormTableSettingDOList = queryRunner.query(querySql, new ResultSetHandler<List<DfFormTableSettingDO>>() {
            @Override
            public List<DfFormTableSettingDO> handle(ResultSet resultSet) throws SQLException {
                List<DfFormTableSettingDO> dfFormTableSettingDOS = new ArrayList<>();
                DfFormTableSettingDO dfFormTableSettingDO = null;
                while (resultSet.next()) {
                    Long id =  resultSet.getLong("id");
                    String gmtCreateStr =  resultSet.getString("gmt_create");
                    String gmtModifiedStr = resultSet.getString("gmt_modified");
                    String dfKey = resultSet.getString("df_key");
                    String dbFieldName = resultSet.getString("db_field_name");
                    String dbFieldType = resultSet.getString("db_field_type");
                    String viewFieldLabel = resultSet.getString("view_field_label");
                    String dbFieldComment = resultSet.getString("db_field_comment");
                    Boolean formFieldVisible = resultSet.getBoolean("form_field_visible");
                    Integer formFieldSequence = resultSet.getInt("form_field_sequence");
                    String formFieldQueryType = resultSet.getString("form_field_query_type");
                    Boolean formFieldIsExactly = resultSet.getBoolean("form_field_is_exactly");
                    String formFieldChildrenDbFieldName = resultSet.getString("form_field_child_field_name");
                    String formFieldDicDomainName = resultSet.getString("form_field_dic_domain_name");
                    Boolean formFieldUseDic = resultSet.getBoolean("form_field_use_dic");
                    String formFieldDefalutValStratege = resultSet.getString("form_field_def_val_stratege");
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
                    String formFieldManMadeDefaultVal = resultSet.getString("form_field_default_val");
                    String formFieldDefaultValSql = resultSet.getString("form_field_default_val_sql");
                    Boolean columIsExist = resultSet.getBoolean("column_is_exist");

                    dfFormTableSettingDO = new DfFormTableSettingDO();
                    dfFormTableSettingDO.setId(id);
                    dfFormTableSettingDO.setGmtCreate(gmtCreateStr);
                    dfFormTableSettingDO.setGmtModified(gmtModifiedStr);
                    dfFormTableSettingDO.setDfKey(dfKey);
                    dfFormTableSettingDO.setDbFieldName(dbFieldName);
                    dfFormTableSettingDO.setDbFieldType(dbFieldType);
                    dfFormTableSettingDO.setViewFieldLabel(viewFieldLabel);
                    dfFormTableSettingDO.setDbFieldComment(dbFieldComment);
                    dfFormTableSettingDO.setFormFieldVisible(formFieldVisible);
                    dfFormTableSettingDO.setFormFieldSequence(formFieldSequence);
                    dfFormTableSettingDO.setFormFieldQueryType(formFieldQueryType);
                    dfFormTableSettingDO.setFormFieldIsExactly(formFieldIsExactly);
                    dfFormTableSettingDO.setFormFieldChildFieldName(formFieldChildrenDbFieldName);
                    dfFormTableSettingDO.setFormFieldDicDomainName(formFieldDicDomainName);
                    dfFormTableSettingDO.setFormFieldUseDic(formFieldUseDic);
                    dfFormTableSettingDO.setFormFieldDefValStratege(formFieldDefalutValStratege);
                    dfFormTableSettingDO.setTableFieldVisible(tableFieldVisible);
                    dfFormTableSettingDO.setTableFieldOrderBy(tableFiedldOrderBy);
                    dfFormTableSettingDO.setTableFieldQueryRequired(tableFieldQueryRequired);
                    dfFormTableSettingDO.setTableFieldSequence(tableFieldSequence);
                    dfFormTableSettingDO.setTableFieldColumnWidth(tableFieldColumnWidth);
                    dfFormTableSettingDO.setExportFieldVisible(exportFieldVisible);
                    dfFormTableSettingDO.setExportFieldSequence(exportFieldSequence);
                    dfFormTableSettingDO.setExportFieldWidth(exportFieldWidth);
                    dfFormTableSettingDO.setTableParentLabel(tableParentLabel);
                    dfFormTableSettingDO.setFormFieldUseDefaultVal(formFieldUseDefaultVal);
                    dfFormTableSettingDO.setFormFieldDefaultVal(formFieldManMadeDefaultVal);
                    dfFormTableSettingDO.setFormFieldDefaultValSql(formFieldDefaultValSql);
                    dfFormTableSettingDO.setColumnIsExist(columIsExist);

                    dfFormTableSettingDOS.add(dfFormTableSettingDO);
                }
                return dfFormTableSettingDOS;
            }
        },dataFacetKey);
        return dfFormTableSettingDOList;
    }

    @Override
    public Integer add(DfFormTableSettingDO dfFormTableSettingDO) throws IOException, SQLException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql ="insert into df_form_table_setting(gmt_create, gmt_modified, df_key, db_field_name, db_field_type," +
                " view_field_label, db_field_comment, form_field_visible, form_field_sequence, form_field_query_type," +
                " form_field_is_exactly, form_field_child_field_name, form_field_dic_domain_name, form_field_use_dic, form_field_def_val_stratege," +
                " table_field_visible, table_field_order_by, table_field_query_required, table_field_sequence, table_field_column_width," +
                " export_field_visible, export_field_sequence, export_field_width,table_parent_label,form_field_use_default_val," +
                " form_field_default_val,form_field_default_val_sql,column_is_exist)" +
                " values (?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?, ?)";
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectParams={now, now,
                dfFormTableSettingDO.getDfKey(),
                dfFormTableSettingDO.getDbFieldName(),
                dfFormTableSettingDO.getDbFieldType(),

                dfFormTableSettingDO.getViewFieldLabel(),
                dfFormTableSettingDO.getDbFieldComment(),
                dfFormTableSettingDO.getFormFieldVisible()?1:0,
                dfFormTableSettingDO.getFormFieldSequence(),
                dfFormTableSettingDO.getFormFieldQueryType(),

                dfFormTableSettingDO.getFormFieldIsExactly()?1:0,
                dfFormTableSettingDO.getFormFieldChildFieldName(),
                dfFormTableSettingDO.getFormFieldDicDomainName(),
                dfFormTableSettingDO.getFormFieldUseDic()?1:0,
                dfFormTableSettingDO.getFormFieldDefValStratege(),

                dfFormTableSettingDO.getTableFieldVisible()?1:0,
                dfFormTableSettingDO.getTableFieldOrderBy(),
                dfFormTableSettingDO.getTableFieldQueryRequired()?1:0,
                dfFormTableSettingDO.getTableFieldSequence(),
                dfFormTableSettingDO.getTableFieldColumnWidth(),

                dfFormTableSettingDO.getExportFieldVisible()?1:0,
                dfFormTableSettingDO.getExportFieldSequence(),
                dfFormTableSettingDO.getExportFieldWidth(),
                dfFormTableSettingDO.getTableParentLabel(),
                dfFormTableSettingDO.getFormFieldUseDefaultVal()?1:0,

                dfFormTableSettingDO.getFormFieldDefaultVal(),
                dfFormTableSettingDO.getFormFieldDefaultValSql(),
                dfFormTableSettingDO.getColumnIsExist()?1:0
                };
        LOGGER.info("insert sql:{}",sql);
        int result = queryRunner.update(sql, objectParams);
        return  result;
    }

    @Override
    public List<Map<String, Object>> selectAllDfFormTableSettingByDfKey(String dataFacetKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String querySql = "select * from df_form_table_setting where df_key = ? ";
        LOGGER.info("querySql:{}", querySql);
        List<Map<String, Object>> result = queryRunner.query(querySql, new MapListHandler(),dataFacetKey);
        return result;
    }

    @Override
    public Integer updateDataFacetKey(String dataFacetKey,String newDataFacetKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String querySql = " update df_form_table_setting set gmt_modified = ?, df_key = ? " +
                          " where df_key = ?";
        LOGGER.info("querySql:{}", querySql);
        String now = DateUtils.ofLongStr(new java.util.Date());
        int update = queryRunner.update(querySql, now, newDataFacetKey, dataFacetKey);
        return update;
    }

    @Override
    public Integer deleteDataFacetKey(String dfKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql="delete from df_form_table_setting where df_key = ?";
        int update = queryRunner.update(sql, dfKey);
        return update;
    }

    @Override
    public List<DfFormTableSettingDO> getAllDfFormTableSettingByDfKeyForExport(String dataFacetKey) throws SQLException, IOException {
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String querySql = "" +
                "select id as  id," +
                "  gmt_create as  gmtCreate," +
                "  gmt_modified as  gmtModified ," +
                "  df_key as  dfKey ," +
                "  db_field_name as  dbFieldName ," +
                "  db_field_type as  dbFieldType ," +
                "  view_field_label as  viewFieldLabel ," +
                "  db_field_comment as  dbFieldComment ," +
                "  form_field_visible as  formFieldVisible," +
                "  form_field_sequence as  formFieldSequence ," +
                "  form_field_query_type as  formFieldQueryType," +
                "  form_field_is_exactly as  formFieldIsExactly ," +
                "  form_field_child_field_name as  formFieldChildrenDbFieldName ," +
                "  form_field_dic_domain_name as  formFieldDicDomainName ," +
                "  form_field_use_dic as  formFieldUseDic ," +
                "  form_field_def_val_stratege as  formFieldDefalutValStratege ," +
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
                "  form_field_default_val as  formFieldManMadeDefaultVal," +
                "  form_field_default_val_sql as formFieldDefaultValSql ," +
                "  column_is_exist as columIsExist " +
                " from df_form_table_setting where df_key = ? and export_field_visible = ?";
        LOGGER.info("querySql:{}", querySql);
        List<DfFormTableSettingDO> dfFormTableSettingDOList = queryRunner.query(querySql, new BeanListHandler<>(DfFormTableSettingDO.class), dataFacetKey, 1);
        return dfFormTableSettingDOList;
    }

    @Override
    public Integer updateDfFormTableSetting(DfFormTableSettingDO dfFormTableSettingDO) throws SQLException, IOException {
        int result=0;
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String updateSql = "update df_form_table_setting " +
                "set gmt_modified = ?," +
                "   df_key = ?," +
                "   db_field_name = ?," +
                "   db_field_type = ?," +
                "   view_field_label = ?," +
                "   db_field_comment = ?," +
                "   form_field_visible = ?," +
                "   form_field_sequence = ?," +
                "   form_field_query_type = ?," +
                "   form_field_is_exactly = ?," +
                "   form_field_child_field_name = ?," +
                "   form_field_dic_domain_name = ?," +
                "   form_field_use_dic = ?," +
                "   form_field_def_val_stratege = ?," +
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
                "   form_field_default_val = ?," +
                "   form_field_default_val_sql = ?," +
                "   column_is_exist = ?" +
                " where id = ?";
        LOGGER.info("updateSql:{}", updateSql);
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[] objectParams={now,
                dfFormTableSettingDO.getDfKey(),
                dfFormTableSettingDO.getDbFieldName(),
                dfFormTableSettingDO.getDbFieldType(),
                dfFormTableSettingDO.getViewFieldLabel(),

                dfFormTableSettingDO.getDbFieldComment(),
                dfFormTableSettingDO.getFormFieldVisible()?1:0,
                dfFormTableSettingDO.getFormFieldSequence(),
                dfFormTableSettingDO.getFormFieldQueryType(),
                dfFormTableSettingDO.getFormFieldIsExactly()?1:0,

                dfFormTableSettingDO.getFormFieldChildFieldName(),
                dfFormTableSettingDO.getFormFieldDicDomainName(),
                dfFormTableSettingDO.getFormFieldUseDic()?1:0,
                dfFormTableSettingDO.getFormFieldDefValStratege(),
                dfFormTableSettingDO.getTableFieldVisible()?1:0,

                dfFormTableSettingDO.getTableFieldOrderBy(),
                dfFormTableSettingDO.getTableFieldQueryRequired()?1:0,
                dfFormTableSettingDO.getTableFieldSequence(),
                dfFormTableSettingDO.getTableFieldColumnWidth(),
                dfFormTableSettingDO.getExportFieldVisible()?1:0,

                dfFormTableSettingDO.getExportFieldSequence(),
                dfFormTableSettingDO.getExportFieldWidth(),
                dfFormTableSettingDO.getTableParentLabel(),
                dfFormTableSettingDO.getFormFieldUseDefaultVal()?1:0,

                dfFormTableSettingDO.getFormFieldDefaultVal(),
                dfFormTableSettingDO.getFormFieldDefaultValSql(),
                dfFormTableSettingDO.getColumnIsExist()?1:0,
                dfFormTableSettingDO.getId()
        };
        result = queryRunner.update(updateSql, objectParams);
        return result;
    }


}
