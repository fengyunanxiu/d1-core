package ai.sparklabinc.exception.custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author :  zxiuwu
 * @date : 2019-03-21 16:24
 */
public class PropertyNotFoundException extends Exception {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyNotFoundException.class);

    public PropertyNotFoundException(String msg) {
        super(msg);
    }
    public PropertyNotFoundException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
