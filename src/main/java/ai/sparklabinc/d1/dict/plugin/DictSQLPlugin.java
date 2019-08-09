package ai.sparklabinc.d1.dict.plugin;

import ai.sparklabinc.d1.dict.dao.DictPluginConfigurationRepository;
import ai.sparklabinc.d1.dict.dao.DictRepository;
import ai.sparklabinc.d1.dict.entity.DictDO;
import ai.sparklabinc.d1.dict.entity.DictPluginConfigurationDO;
import ai.sparklabinc.d1.engine.SQLEngine;
import ai.sparklabinc.d1.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/9 15:28
 * @description :
 */
@Component
public class DictSQLPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictSQLPlugin.class);

    @Resource(name = "DictPluginConfigurationRepository")
    private DictPluginConfigurationRepository dictPluginConfigurationRepository;

    @Autowired
    private SQLEngine sqlEngine;

    @Autowired
    private TaskScheduler taskScheduler;

    @Resource(name = "DictRepository")
    private DictRepository dictRepository;

    public ScheduledFuture<?> run(DictPluginConfigurationDO dictPluginConfigurationDO) {
        String fieldDomain = dictPluginConfigurationDO.getFieldDomain();
        String fieldItem = dictPluginConfigurationDO.getFieldItem();
        String fieldType = dictPluginConfigurationDO.getFieldType();
        String fieldParam = dictPluginConfigurationDO.getFieldParam();
        String fieldCron = dictPluginConfigurationDO.getFieldCron();
        if (StringUtils.isNullOrEmpty(fieldDomain)
                ||StringUtils.isNullOrEmpty(fieldItem)
                || StringUtils.isNullOrEmpty(fieldType)
                || StringUtils.isNullOrEmpty(fieldParam)
                || StringUtils.isNullOrEmpty(fieldCron)
                || !"SQL".equals(fieldType)) {
            LOGGER.info("error dict sql plugin msg");
            return null;
        }
        CronTrigger cronTrigger = new CronTrigger(fieldCron);
        return this.taskScheduler.schedule(() -> {
            try {
                LOGGER.info("begin to process dict sql plugin task");
                process(dictPluginConfigurationDO);
                LOGGER.info("end to process dict sql plugin task");
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }, cronTrigger);
    }

    private void process(DictPluginConfigurationDO dictPluginConfigurationDO) throws Exception {
        String fieldDomain = dictPluginConfigurationDO.getFieldDomain();
        String fieldItem = dictPluginConfigurationDO.getFieldItem();
        if (StringUtils.isNullOrEmpty(fieldDomain)
                || StringUtils.isNullOrEmpty(fieldItem)) {
            return;
        }
        // 每次读取最新的任务信息
        dictPluginConfigurationDO = this.dictPluginConfigurationRepository.findByDomainAndItem(fieldDomain, fieldItem);
        String fieldParam = dictPluginConfigurationDO.getFieldParam();
        if (StringUtils.isNullOrEmpty(fieldParam)) {
            return;
        }
        JSONObject paramJSONObject = JSON.parseObject(fieldParam);
        String jdbcUrl = paramJSONObject.getString("jdbc_url");
        String username = paramJSONObject.getString("username");
        String password = paramJSONObject.getString("password");
        String sql = paramJSONObject.getString("sql");
        if (StringUtils.isNullOrEmpty(sql)) {
            return;
        }
        List<Map<String, String>> result = this.sqlEngine.execute(jdbcUrl, username, password, sql);
        if (result == null || result.isEmpty()) {
            return;
        }
        // 更新到字典中
        List<DictDO> resultList = result.stream().map((tmp) -> {
            String value = tmp.get("value");
            String label = tmp.get("label");
            String sequence = tmp.get("sequence");
            String enable = tmp.get("enable");
            String parentId = tmp.get("parent_id");
            DictDO dictDO = new DictDO();
            dictDO.setFieldDomain(fieldDomain);
            dictDO.setFieldItem(fieldItem);
            dictDO.setFieldValue(value);
            dictDO.setFieldLabel(label);
            dictDO.setFieldSequence(sequence);
            dictDO.setFieldEnable(enable);
            dictDO.setFieldParentId(parentId);
            return dictDO;
        }).collect(Collectors.toList());
        this.dictRepository.updateValueByDomainAndItem(resultList);
    }


}
