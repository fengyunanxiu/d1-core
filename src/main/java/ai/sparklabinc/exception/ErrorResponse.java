package ai.sparklabinc.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

/**
 * An immutable data structure representing HTTP error response bodies. JSON
 * representation of this class would be something like the following:
 * <pre>
 *     {
 *         "status_code": 404,
 *         "reason_phrase": "Not Found",
 *         "errors": [
 *             {"code": "res-15", "message": "some error message"},
 *             {"code": "res-16", "message": "yet another message"}
 *         ]
 *     }
 * </pre>
 *
 * @author Ali Dehghani
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ErrorResponse {

    /**
     * The 4xx or 5xx status code for error cases, e.g. 404
     */
    private final int statusCode;

    /**
     * The HTTP reason phrase corresponding the {@linkplain #statusCode},
     * e.g. Not Found
     *
     * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html">
     * Status Code and Reason Phrase</a>
     */
    private final String reasonPhrase;

    /**
     * List of application-level error code and message combinations.
     * Using these errors we provide more information about the
     * actual error
     */
    private final List<ApiError> errors;


    public ErrorResponse(int statusCode, String reasonPhrase, List<ApiError> errors) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.errors = errors;
    }

    /**
     * Static factory method to create a {@linkplain ErrorResponse} with multiple
     * {@linkplain ApiError}s. The canonical use case of this factory method is when
     * we're handling validation exceptions, since we may have multiple validation
     * errors.
     */
    static ErrorResponse ofErrors(HttpStatus httpStatus, List<ApiError> errors) {
        return new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase(), errors);
    }

    /**
     * Static factory method to create a {@linkplain ErrorResponse} with a single
     * {@linkplain ApiError}. The canonical use case for this method is when we trying
     * to create {@linkplain ErrorResponse}es for regular non-validation exceptions.
     */
    static ErrorResponse of(HttpStatus httpStatus, ApiError error) {
        return ofErrors(httpStatus, Collections.singletonList(error));
    }


    /**
     * An immutable data structure representing each application-level error. JSON
     * representation of this class would be something like the following:
     * <pre>
     *     {"code": "res-12", "message": "some error"}
     * </pre>
     *
     * @author Ali Dehghani
     */
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static class ApiError {
        /**
         * The error code
         */
        private final String code;

        /**
         * Possibly localized error message
         */
        private final String message;

        ApiError(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
