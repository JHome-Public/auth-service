package com.jhome.auth.exception;

import com.jhome.auth.response.ApiResponseCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ApiResponseCode apiResponseCode;

    public CustomException(ApiResponseCode apiResponseCode) {
        super(apiResponseCode.getMessage());
        this.apiResponseCode = apiResponseCode;
    }

}
