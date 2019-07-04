package ai.sparklabinc.exception;

import ai.sparklabinc.exception.custom.IllegalParameterException;
import ai.sparklabinc.exception.custom.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Service抛出的异常
 * 
 * @author bbottong
 *
 */
public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2319474663401790191L;

	public ServiceException(String message) {
		super(message);
	}

	public enum ServiceErrorCode implements ErrorCode {

		ILLEGAL_PARAMETER_CODE("10170", HttpStatus.BAD_REQUEST),
		NOT_FOUND("10150", HttpStatus.NOT_FOUND);


		private String code;
		private HttpStatus httpStatus;

		ServiceErrorCode(String code, HttpStatus httpStatus) {
			this.code = code;
			this.httpStatus = httpStatus;
		}

		@Override
		public String code() {
			return code;
		}

		@Override
		public HttpStatus httpStatus() {
			return httpStatus;
		}
	}

	@Component
	static class ResourceNotFoundExceptionToErrorCode implements ExceptionToErrorCode {
		@Override
		public boolean canHandle(Exception exception) {
			return exception instanceof ResourceNotFoundException;
		}

		@Override
		public ErrorCode toErrorCode(Exception exception) {
			return ServiceErrorCode.NOT_FOUND;
		}
	}

	@Component
	static class IllegalParameterExceptionToErrorCode implements ExceptionToErrorCode {
		@Override
		public boolean canHandle(Exception exception) {
			return exception instanceof IllegalParameterException;
		}

		@Override
		public ErrorCode toErrorCode(Exception exception) {
			return ServiceErrorCode.ILLEGAL_PARAMETER_CODE;
		}
	}


}
