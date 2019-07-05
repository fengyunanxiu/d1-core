package ai.sparklabinc.exception.custom;

import ai.sparklabinc.exception.ServiceException;

public class IllegalParameterException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -791209396726926333L;

	public IllegalParameterException(String message) {
		super(message);
	}

}
