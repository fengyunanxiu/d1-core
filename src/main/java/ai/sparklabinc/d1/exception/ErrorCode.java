package ai.sparklabinc.d1.exception;


import org.springframework.http.HttpStatus;

/**
 * Represents API error code. Each API should implement this interface to
 * provide an error code for each error case.
 *
 * @implNote Enum implementations are good fit for this scenario.
 *
 * @author Ali Dehghani
 */
public interface ErrorCode {

    String ERROR_CODE_FOR_UNKNOWN_ERROR = "unknown";

    /**
     * Represents the error code.
     *
     * @return The resource based error code
     */
    String code();

    /**
     * The corresponding HTTP status for the given error code
     *
     * @return Corresponding HTTP status code, e.g. 400 Bad Request for a validation
     * error code
     */
    HttpStatus httpStatus();

    enum UnknownErrorCode implements ErrorCode {

        INSTANCE;

        @Override
        public String code() {
            return ERROR_CODE_FOR_UNKNOWN_ERROR;
        }

        @Override
        public HttpStatus httpStatus() {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Every Api should have errorcode self
     * @author zxw
     */
    enum CommonApiErrorCode implements ErrorCode {

        BAD_REQUEST("bad requset", HttpStatus.BAD_REQUEST);

        private String code;
        private HttpStatus httpStatus;

        CommonApiErrorCode(String code, HttpStatus httpStatus) {
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

}
