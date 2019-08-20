package io.g740.d1.exception;

import io.g740.d1.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;
import java.util.Objects;


/**
 * Exception handler that catches all exceptions thrown by the REST layer and
 * convert them to the appropriate {@linkplain ErrorResponse}s with a suitable
 * HTTP status code.
 *
 * @author Ali Dehghani
 * @see ErrorCode
 * @see ErrorCodes
 * @see ErrorResponse
 */
@ControllerAdvice
public class ApiExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

	private static final String NO_MESSAGE_AVAILABLE = "No message available";
	/**
	 * Factory to convert the given {@linkplain Exception} to an instance of
	 * {@linkplain ErrorCode}
	 */
	private final ErrorCodes errorCodes;

	/**
	 * Responsible for finding the appropriate error message(s) based on the given
	 * {@linkplain ErrorCode} and {@linkplain Locale}
	 */
	private final MessageSource messageSource;

	/**
	 * Construct a valid instance of the exception handler
	 *
	 * @throws NullPointerException
	 *             If either of required parameters were {@code null}
	 */
	ApiExceptionHandler(ErrorCodes errorCodes, MessageSource messageSource) {
		Objects.requireNonNull(errorCodes);
		Objects.requireNonNull(messageSource);

		this.errorCodes = errorCodes;
		this.messageSource = messageSource;
	}

	/**
	 * Catches all non-validation exceptions and tries to convert them to
	 * appropriate HTTP Error responses
	 * <p>
	 * <p>
	 * First using the {@linkplain #errorCodes} will find the corresponding
	 * {@linkplain ErrorCode} for the given {@code exception}. Then based on the
	 * resolved {@linkplain Locale}, a suitable instance of
	 * {@linkplain ErrorResponse} with appropriate and localized message will return
	 * to the client. {@linkplain ErrorCode} itself determines the HTTP status of
	 * the response.
	 *
	 * @param e
	 *            The exception to convert
	 * @param locale
	 *            The locale that usually resolved by {@code Accept-Language}
	 *            header. This locale will determine the language of the returned
	 *            error message.
	 * @return An appropriate HTTP Error Response with suitable status code and
	 *         error messages
	 */
	@ExceptionHandler(Exception.class)
	ResponseEntity<ErrorResponse> handleException(Exception e, Locale locale) {
		LOGGER.error(e.getMessage(), e);

		ErrorCode errorCode = errorCodes.of(e);

		ErrorResponse errorResponse = ErrorResponse.of(errorCode.httpStatus(),
				toApiError(errorCode, locale, e.getMessage()));

		return ResponseEntity.status(errorCode.httpStatus()).body(errorResponse);
	}

	private ErrorResponse.ApiError toApiError(ErrorCode errorCode, Locale locale, String messageInException) {
		String message;
		try {
			message = messageSource.getMessage(errorCode.code(), new Object[] { messageInException }, locale);
		} catch (NoSuchMessageException e) {
			if (StringUtils.isNotNullNorEmpty(messageInException)) {
				message = messageInException;
			} else {
				message = NO_MESSAGE_AVAILABLE;
			}
		}
		return new ErrorResponse.ApiError(errorCode.code(), message);
	}

}
