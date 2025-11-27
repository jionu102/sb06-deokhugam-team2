package com.codeit.sb06deokhugamteam2.common.exception.exceptions;

import com.codeit.sb06deokhugamteam2.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class AWSException extends BasicException {

    public AWSException(ErrorCode errorCode, Map<String, Object> details, HttpStatus httpStatus) {
        super(errorCode, details, httpStatus);
    }
}
