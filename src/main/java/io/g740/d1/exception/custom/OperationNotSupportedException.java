package io.g740.d1.exception.custom;

import io.g740.d1.exception.ServiceException;

/**
 * @author : Kingzer
 * @date : 2019-08-28 19:06
 * @description :
 */
public class OperationNotSupportedException extends ServiceException {

    public OperationNotSupportedException(String msg) {
        super(msg);
    }

}
