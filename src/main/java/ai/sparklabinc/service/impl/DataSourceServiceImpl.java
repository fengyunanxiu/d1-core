package ai.sparklabinc.service.impl;

import ai.sparklabinc.dao.DbBasicConfigDao;
import ai.sparklabinc.dao.DbSecurityConfigDao;
import ai.sparklabinc.dao.DsKeyBasicConfigDao;
import ai.sparklabinc.dao.MysqlDataSourceDao;
import ai.sparklabinc.datasource.Constants;
import ai.sparklabinc.datasource.DataSourceFactory;
import ai.sparklabinc.dto.DbBasicConfigDTO;
import ai.sparklabinc.dto.DbInforamtionDTO;
import ai.sparklabinc.dto.DbSecurityConfigDTO;
import ai.sparklabinc.entity.DbBasicConfigDO;
import ai.sparklabinc.entity.DbSecurityConfigDO;
import ai.sparklabinc.service.DataSourceService;
import com.jcraft.jsch.Session;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Function:
 * @Author: DAM
 * @Date: 2019/7/1 20:33
 * @Description:
 * @Version: V1.0
 */

@Service
public class DataSourceServiceImpl implements DataSourceService {
    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Autowired
    private DbBasicConfigDao dbBasicConfigDao;

    @Autowired
    private DbSecurityConfigDao dbSecurityConfigDao;

    @Autowired
    private MysqlDataSourceDao mysqlDataSourceDao;

    @Autowired
    private DsKeyBasicConfigDao dsKeyBasicConfigDao;


    @Override
    public boolean Connection2DataSource(Long dsId) throws SQLException, IOException {
        Connection connection = null;
        try {
            DataSource mysql = dataSourceFactory.builder(Constants.DATABASE_TYPE_MYSQL, dsId);
            connection = mysql.getConnection();
            if(connection!=null){
                return true;
            }
        }finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("error>>>>"+e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public boolean addDataSources(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws IOException, SQLException {
        DbBasicConfigDO dbBasicConfigDO = new DbBasicConfigDO();
        BeanUtils.copyProperties(dbBasicConfigDTO,dbBasicConfigDO);
        Long dsId = dbBasicConfigDao.add(dbBasicConfigDO);
        if(dsId>0L){
            DbSecurityConfigDO dbSecurityConfigDO = new DbSecurityConfigDO();
            BeanUtils.copyProperties(dbSecurityConfigDTO,dbSecurityConfigDO);
            dbSecurityConfigDO.setId(dsId);
            Integer add = dbSecurityConfigDao.add(dbSecurityConfigDO);
            if(add>0){
                return  true;
            }

        }
        return  false;

    }

    @Override
    public boolean deleteDataSources(Long dsId) throws IOException, SQLException {
        Integer delete = dbBasicConfigDao.delete(dsId);
        Integer delete1 = dbSecurityConfigDao.delete(dsId);
        if(delete>0&&delete1>0){
            return true;
        }
        return false;
    }

    @Override
    public List<DbInforamtionDTO> selectDataSources(Long dsId) throws IOException, SQLException {
        /*********************************************************************
         * step1 拿到前端需要展示的第一层信息
         * *******************************************************************
         */
        List<DbInforamtionDTO> result = dbBasicConfigDao.selectDataSources(dsId);
        if(CollectionUtils.isEmpty(result)){
            return  null;
        }
        /*********************************************************************
         * step2 连接数据库
         * *******************************************************************
         */
        DbInforamtionDTO dbInforamtionDTO = result.get(0);
        dsId=dbInforamtionDTO.getId();

        boolean connection = Connection2DataSource(dbInforamtionDTO.getId());
        if(connection){
            /*********************************************************************
             * step3 拿到所有的数据库名称
             * *******************************************************************
             */
            List<DbInforamtionDTO> schemas = mysqlDataSourceDao.selectAllSchema(dsId);
            if(CollectionUtils.isEmpty(schemas)){
                return result;
            }
            dbInforamtionDTO.setChildren(schemas);

            for (DbInforamtionDTO schema: schemas){
                /*********************************************************************
                 * step4 拿到schema所有的表和视图
                 * *******************************************************************
                 */
                List<DbInforamtionDTO> tableAndViews = mysqlDataSourceDao.selectAllTableAndView(dsId, schema.getLabel());
                if(CollectionUtils.isEmpty(tableAndViews)){
                    continue;
                }
                schema.setChildren(tableAndViews);
                /*********************************************************************
                 * step5 拿到表和视图的data source key
                 * *******************************************************************
                 */
                for(DbInforamtionDTO tableAndView:tableAndViews){
                    List<DbInforamtionDTO> dataSourceKeys = dsKeyBasicConfigDao.getDataSourceKey(dsId, schema.getLabel(), tableAndView.getLabel());
                        if(CollectionUtils.isEmpty(dataSourceKeys)){
                            continue;
                        }
                        tableAndView.setChildren(dataSourceKeys);
                }
            };
        }
        return  result;
    }




    @Override
    public List<Map<String, Object>> selectDataSourceProperty(Long dsId) throws IOException, SQLException {
        List<Map<String, Object>> result = dbBasicConfigDao.selectDataSourceProperty(dsId);
        return  result;
    }

    @Override
    public boolean editDataSourceProperty(DbBasicConfigDTO dbBasicConfigDTO, DbSecurityConfigDTO dbSecurityConfigDTO) throws IOException, SQLException {
        boolean updateResult=false;
        DbBasicConfigDO dbBasicConfigDO = new DbBasicConfigDO();
        BeanUtils.copyProperties(dbBasicConfigDTO,dbBasicConfigDO);
        Integer dbBasicUpdate = dbBasicConfigDao.editDataSourceProperty(dbBasicConfigDO);

        DbSecurityConfigDO dbSecurityConfigDO = new DbSecurityConfigDO();
        BeanUtils.copyProperties(dbSecurityConfigDTO,dbSecurityConfigDO);
        dbSecurityConfigDO.setId(dbBasicConfigDTO.getId());
        Integer dbSecurityUpdate = dbSecurityConfigDao.editDataSourceProperty(dbSecurityConfigDO);

        if(dbBasicUpdate>0&&dbSecurityUpdate>0){
            //清除ssh，datasource
            DataSource dataSource = dataSourceFactory.dataSourceMap.get(dbBasicConfigDO.getId());
            dataSource.postDeregister();
            dataSourceFactory.dataSourceMap.remove(dbBasicConfigDO.getId());


            Session session = dataSourceFactory.sshSessionMap.get(dbBasicConfigDO.getId());
            //关闭连接
            session.disconnect();
            dataSourceFactory.sshSessionMap.remove(dbBasicConfigDO.getId());
            updateResult=true;
        }

        return updateResult;
    }


}
