package io.g740.d1.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/6 11:03
 * @description :
 */
public class CollectionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionUtils.class);

    public static <K, V> void putIfKVNotNull(Map<K, V> map, K k, V v) {
        if (k != null && v != null) {
            map.put(k, v);
        }
    }

}
