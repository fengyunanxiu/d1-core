package io.g740.d1.dao.impl.postgres;

import io.g740.d1.dao.DataDaoType;
import io.g740.d1.dao.DfFormTableSettingDao;
import io.g740.d1.dao.impl.AbstractDfFormTableSettingDao;
import io.g740.d1.datasource.DataSourceFactory;
import io.g740.d1.entity.DfFormTableSettingDO;
import io.g740.d1.util.DateUtils;
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
@Repository("PostgresqlDfFormTableSettingDaoImpl")
public class PostgresqlDfFormTableSettingDaoImpl extends AbstractDfFormTableSettingDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresqlDfFormTableSettingDaoImpl.class);
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Resource(name="D1BasicDataSource")
    private DataSource d1BasicDataSource;

    @Override
    public DataSource d1BasicDataSource() {
        return this.d1BasicDataSource;
    }

    @Override
    public DataDaoType getDataDaoType() {
        return DataDaoType.POSTGRESQL;
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
                    String formFieldChildrenDbFieldName = resultSet.getString("form_field_child_field_name");
                    String formFieldDictDomainName = resultSet.getString("form_field_dict_domain_name");
                    String formFieldDefalutValStrategy = resultSet.getString("form_field_def_val_strategy");
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
                    dfFormTableSettingDO.setFormFieldChildFieldName(formFieldChildrenDbFieldName);
                    dfFormTableSettingDO.setFormFieldDictDomainName(formFieldDictDomainName);
                    dfFormTableSettingDO.setFormFieldDefValStrategy(formFieldDefalutValStrategy);
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
                    dfFormTableSettingDO.setColumnIsExist(columIsExist);

                    dfFormTableSettingDOS.add(dfFormTableSettingDO);
                }
                return dfFormTableSettingDOS;
            }
        },dataFacetKey);
        return dfFormTableSettingDOList;
    }

    @Override
    public Integer batchAdd(List<DfFormTableSettingDO> dfFormTableSettingDOS) throws IOException, SQLException {
        long startTime=System.currentTimeMillis();
        QueryRunner queryRunner = new QueryRunner(d1BasicDataSource);
        String sql ="insert into df_form_table_setting(gmt_create, gmt_modified, df_key, db_field_name, db_field_type," +
                " view_field_label, db_field_comment, form_field_visible, form_field_sequence, form_field_query_type," +
                " form_field_child_field_name, form_field_dict_domain_name, form_field_dict_item,form_field_def_val_strategy," +
                " table_field_visible, table_field_order_by, table_field_query_required, table_field_sequence, table_field_column_width," +
                " export_field_visible, export_field_sequence, export_field_width,table_parent_label,form_field_use_default_val," +
                " form_field_default_val,column_is_exist)" +
                " values (?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?, ?, ?, ?," +
                "  ?, ?)";
        String now = DateUtils.ofLongStr(new java.util.Date());
        Object[][] paramArray = new Object[dfFormTableSettingDOS.size()][];
        for(int i=0,size=dfFormTableSettingDOS.size();i<size;i++){
            DfFormTableSettingDO dfFormTableSettingDO = dfFormTableSettingDOS.get(i);
            paramArray[i][0]=now;
            paramArray[i][1]=now;
            paramArray[i][2]=dfFormTableSettingDO.getDfKey();
            paramArray[i][3]=dfFormTableSettingDO.getDbFieldName();
            paramArray[i][4]=dfFormTableSettingDO.getDbFieldType();

            paramArray[i][6]=dfFormTableSettingDO.getViewFieldLabel();
            paramArray[i][7]=dfFormTableSettingDO.getDbFieldComment();
            paramArray[i][8]=dfFormTableSettingDO.getFormFieldVisible()?1:0;
            paramArray[i][9]=dfFormTableSettingDO.getFormFieldSequence();
            paramArray[i][10]=dfFormTableSettingDO.getFormFieldQueryType();

            paramArray[i][11]=dfFormTableSettingDO.getFormFieldChildFieldName();
            paramArray[i][12]=dfFormTableSettingDO.getFormFieldDictDomainName();
            paramArray[i][13]=dfFormTableSettingDO.getFormFieldDictItem();
            paramArray[i][14]=dfFormTableSettingDO.getFormFieldDefValStrategy();

            paramArray[i][15]=dfFormTableSettingDO.getTableFieldVisible()?1:0;
            paramArray[i][16]=dfFormTableSettingDO.getTableFieldOrderBy();
            paramArray[i][17]=dfFormTableSettingDO.getTableFieldQueryRequired()?1:0;
            paramArray[i][18]=dfFormTableSettingDO.getTableFieldSequence();
            paramArray[i][19]=dfFormTableSettingDO.getTableFieldColumnWidth();

            paramArray[i][20]=dfFormTableSettingDO.getExportFieldVisible()?1:0;
            paramArray[i][21]=dfFormTableSettingDO.getExportFieldSequence();
            paramArray[i][22]=dfFormTableSettingDO.getExportFieldWidth();
            paramArray[i][23]=dfFormTableSettingDO.getTableParentLabel();
            paramArray[i][24]=dfFormTableSettingDO.getFormFieldUseDefaultVal()?1:0;

            paramArray[i][25]=dfFormTableSettingDO.getFormFieldDefaultVal();
            paramArray[i][26]=dfFormTableSettingDO.getColumnIsExist()?1:0;
        }

        LOGGER.info("insert sql:{}",sql);
        int result = queryRunner.batch(sql, paramArray).length;
        LOGGER.info("insert spent time：{}",System.currentTimeMillis()-startTime);
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
                "  form_field_dict_domain_name as  formFieldDicDomainName ," +
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
                "   form_field_child_field_name = ?," +
                "   form_field_dict_domain_name = ?," +
                "   form_field_def_val_strategy = ?," +
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

                dfFormTableSettingDO.getFormFieldChildFieldName(),
                dfFormTableSettingDO.getFormFieldDictDomainName(),
                dfFormTableSettingDO.getFormFieldDefValStrategy(),
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
                dfFormTableSettingDO.getColumnIsExist()?1:0,
                dfFormTableSettingDO.getId()
        };
        result = queryRunner.update(updateSql, objectParams);
        return result;
    }



}
