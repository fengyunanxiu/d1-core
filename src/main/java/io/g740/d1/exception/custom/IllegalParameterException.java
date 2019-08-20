package io.g740.d1.exception.custom;

import io.g740.d1.exception.ServiceException;

public class IllegalParameterException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -791209396726926333L;

	public IllegalParameterException(String message) {
		super(message);
	}

}
