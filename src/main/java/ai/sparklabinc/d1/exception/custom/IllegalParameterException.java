package ai.sparklabinc.d1.exception.custom;

import ai.sparklabinc.d1.exception.ServiceException;

public class IllegalParameterException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -791209396726926333L;

	public IllegalParameterException(String message) {
		super(message);
	}

}
