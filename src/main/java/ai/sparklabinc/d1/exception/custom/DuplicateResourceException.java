package ai.sparklabinc.d1.exception.custom;

import ai.sparklabinc.d1.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/6 11:42
 * @description :
 */
public class DuplicateResourceException extends ServiceException {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateResourceException.class);

    public DuplicateResourceException(String message) {
        super(message);
    }
}
