package org.geekpower.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterValidator {

	private static Logger logger = LoggerFactory.getLogger(ParameterValidator.class);

	public static Tuple.Pair<Integer, String> onException(Exception exp) {
		if (exp instanceof BaseException) {
			BaseException bexp = (BaseException) exp;
			String message = bexp.getMessage();
			logger.error("ecoce={} reason={}", bexp.getErrorCode(), message, exp);

			return Tuple.makePair(bexp.getErrorCode(), message);
		} else {
			logger.error("", exp);
			return Tuple.makePair(BaseError.UNKNOWN_ERROR.getCode(), BaseError.UNKNOWN_ERROR.getDescription());
		}
	}

}
