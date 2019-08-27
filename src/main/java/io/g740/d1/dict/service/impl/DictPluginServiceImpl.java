package io.g740.d1.dict.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.g740.d1.dict.dao.DictPluginConfigurationRepository;
import io.g740.d1.dict.dao.DictRepository;
import io.g740.d1.dict.dto.DictPluginDTO;
import io.g740.d1.dict.entity.DictDO;
import io.g740.d1.dict.entity.DictPluginConfigurationDO;
import io.g740.d1.dict.entity.DictPluginType;
import io.g740.d1.dict.service.DictPluginService;
import io.g740.d1.engine.SQLEngine;
import io.g740.d1.exception.custom.IllegalParameterException;
import io.g740.d1.util.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/26 11:07
 * @description :
 */
@Service
public class DictPluginServiceImpl implements DictPluginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictPluginServiceImpl.class);


    @Resource(name = "DictPluginConfigurationRepository")
    private DictPluginConfigurationRepository dictPluginConfigurationRepository;

    @Resource(name = "DictRepository")
    private DictRepository dictRepository;

    @Autowired
    private SQLEngine sqlEngine;


    /**
     * 查询字典插件配置信息
     *
     * @param domain
     * @param item
     * @return
     * @throws SQLException
     */
    @Override
    public DictPluginDTO query(String domain, String item) throws SQLException {
        DictPluginConfigurationDO existDictPluginConfiguration = this.dictPluginConfigurationRepository.findByDomainAndItem(domain, item);
        DictPluginDTO result = new DictPluginDTO();
        if (existDictPluginConfiguration != null) {
            result.setCron(existDictPluginConfiguration.getFieldCron());
            result.setDomain(existDictPluginConfiguration.getFieldDomain());
            result.setEnable(existDictPluginConfiguration.getFieldEnable());
            result.setItem(existDictPluginConfiguration.getFieldItem());
            result.setType(existDictPluginConfiguration.getFieldType());
            result.setId(existDictPluginConfiguration.getFieldId());
            String fieldParam = existDictPluginConfiguration.getFieldParam();
            JSONObject jsonObject = JSON.parseObject(fieldParam);
            result.setParamJdbcUrl(jsonObject.getString("jdbc_url"));
            result.setParamPassword(jsonObject.getString("password"));
            result.setParamUsername(jsonObject.getString("username"));
            result.setParamSQL(jsonObject.getString("sql"));
        }else {
            return  null;
        }
        return result;
    }


    /**
     * 添加或者修改插件配置信息
     *
     * @param dictPluginDTO
     * @return
     * @throws IllegalParameterException
     */
    @Override
    public void allocateSQLPluginByDomainAndItem(DictPluginDTO dictPluginDTO) throws Exception {
        String domain = dictPluginDTO.getDomain();
        String item = dictPluginDTO.getItem();
        Boolean enable = dictPluginDTO.getEnable();
        String cron = dictPluginDTO.getCron();
        String type = dictPluginDTO.getType();
        String paramJdbcUrl = dictPluginDTO.getParamJdbcUrl();
        String paramPassword = dictPluginDTO.getParamPassword();
        String paramSQL = dictPluginDTO.getParamSQL();
        String paramUsername = dictPluginDTO.getParamUsername();
        // 校验Type
        DictPluginType dictPluginType = DictPluginType.valueOf(type.toUpperCase());
        if (!DictPluginType.SQL.equals(dictPluginType)) {
            throw new IllegalParameterException("plugin type " + dictPluginType + " not support");
        }
        //检查必要的插件参数信息
        if (StringUtils.isNullOrEmpty(paramJdbcUrl)
                || StringUtils.isNullOrEmpty(paramPassword)
                || StringUtils.isNullOrEmpty(paramSQL)
                || StringUtils.isNullOrEmpty(paramUsername)
                || StringUtils.isNullOrEmpty(cron)) {
            throw new IllegalParameterException("jdbcUrl, username, password, sql, cron must not be null");
        }
        // 检查字典是否有对应的domain，item
        List<DictDO> dictDOList = this.dictRepository.findByDomainAndItem(domain, item);
        if (dictDOList == null || dictDOList.isEmpty()) {
            throw new IllegalParameterException("domain " + domain + " and item " + item + " not found in dictionary ");
        }

        //TODO 执行SQL测试
        List<Map<String, String>> executeSQLTest = executeSQLTest(dictPluginDTO);
        boolean allMatch = executeSQLTest.stream().allMatch(rowMap -> rowMap.get("value") != null && rowMap.get("sequence") != null && rowMap.get("label") != null);
        if (!allMatch) {
            throw new IllegalParameterException("sql " + dictPluginDTO.getParamSQL() + " execute failed; sql must return value, sequence, label");
        }
        if (enable == null) {
            enable = Boolean.TRUE;
        }
        DictPluginConfigurationDO dictPluginConfigurationDO = new DictPluginConfigurationDO();
        dictPluginConfigurationDO.setFieldCron(cron);
        dictPluginConfigurationDO.setFieldDomain(domain);
        dictPluginConfigurationDO.setFieldEnable(enable);
        dictPluginConfigurationDO.setFieldItem(item);
        dictPluginConfigurationDO.setFieldType(type);
        JSONObject sqlParam = new JSONObject();
        sqlParam.put("jdbc_url", paramJdbcUrl);
        sqlParam.put("username", paramUsername);
        sqlParam.put("password", paramPassword);
        sqlParam.put("sql", paramSQL);
        dictPluginConfigurationDO.setFieldParam(sqlParam.toJSONString());
        // 先查询是否存在对应的domain，item
        DictPluginConfigurationDO existConfiguration = this.dictPluginConfigurationRepository.findByDomainAndItem(domain, item);
        if (existConfiguration == null) {
            // 创建新的插件配置
            this.dictPluginConfigurationRepository.create(dictPluginConfigurationDO);
        } else {
            // 更新插件配置信息
            dictPluginConfigurationDO.setFieldId(existConfiguration.getFieldId());
            this.dictPluginConfigurationRepository.update(dictPluginConfigurationDO);
        }
    }

    /**
     * 执行测试字典插件配置SQL
     *
     * @param dictPluginDTO
     * @return
     */
    @Override
    public List<Map<String, String>> executeSQLTest(DictPluginDTO dictPluginDTO) {
        String paramJdbcUrl = dictPluginDTO.getParamJdbcUrl();
        String paramUsername = dictPluginDTO.getParamUsername();
        String paramPassword = dictPluginDTO.getParamPassword();
        String paramSQL = dictPluginDTO.getParamSQL();
        List<Map<String, String>> executeResult = this.sqlEngine.execute(paramJdbcUrl, paramUsername, paramPassword, paramSQL);
        List<Map<String, String>> collect = executeResult.stream().limit(5).collect(Collectors.toList());
//        collect.stream().allMatch(rowMap -> rowMap.get("value") != null && rowMap.get("sequence") != null && rowMap.get("label") != null);
        return collect;
    }

}
