package com.gamgoon.commons;

import lombok.Data;

import java.util.List;

/**
 * Created by gamgoon on 2015. 9. 17..
 */
@Data
public class ErrorResponse {
    private String message;
    private String code;
    private List<FieldError> errors;

    // TODO
    public static class FieldError {
        private String field;
        private String value;
        private String reason;
    }
}
