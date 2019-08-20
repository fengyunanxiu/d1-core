package io.g740.d1.exception.custom;

import io.g740.d1.exception.ServiceException;

public class ResourceNotFoundException extends ServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 394958817197733611L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

}
