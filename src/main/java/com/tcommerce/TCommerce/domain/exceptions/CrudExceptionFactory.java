package com.tcommerce.TCommerce.domain.exceptions;


import org.springframework.http.HttpStatus;
import java.util.function.Function;

public class CrudExceptionFactory {

    public static Function<String, CrudException> notFound(String entityName) {
        return (identifier) -> new CrudException(
                "NOT_FOUND",
                CrudErrorMessages.NOT_FOUND.format(entityName, identifier),
                HttpStatus.NOT_FOUND
        );
    }

    public static CrudException notFound(Class<?> entityClass, Object identifier) {
        String entityName = entityClass.getSimpleName();
        return new CrudException(
                "NOT_FOUND",
                CrudErrorMessages.NOT_FOUND.format(entityName, identifier),
                HttpStatus.NOT_FOUND
        );
    }

    public static CrudException alreadyExists(Class<?> entityClass, String field, Object value) {
        String entityName = entityClass.getSimpleName();
        return new CrudException(
                "ALREADY_EXISTS",
                CrudErrorMessages.ALREADY_EXISTS.format(entityName, field, value),
                HttpStatus.CONFLICT
        );
    }

    public static CrudException invalidData(Class<?> entityClass) {
        String entityName = entityClass.getSimpleName();
        return new CrudException(
                "INVALID_DATA",
                CrudErrorMessages.INVALID_DATA.format(entityName),
                HttpStatus.BAD_REQUEST
        );
    }

    public static CrudException duplicateKey(Class<?> entityClass, String keyName, Object keyValue) {
        String entityName = entityClass.getSimpleName();
        return new CrudException(
                "DUPLICATE_KEY",
                CrudErrorMessages.DUPLICATE_KEY.format(entityName, keyName, keyValue),
                HttpStatus.CONFLICT
        );
    }

    public static CrudException create(String errorCode, String message, HttpStatus httpStatus) {
        return new CrudException(errorCode, message, httpStatus) {};
    }

    public static CrudException create(String errorCode, CrudErrorMessages errorMessage,
                                       Object[] args, HttpStatus httpStatus) {
        return new CrudException(errorCode, errorMessage.format(args), httpStatus) {};
    }

    public static CrudException custom(String entityName, String customMessage, HttpStatus httpStatus) {
        return new CrudException(
                "CUSTOM_ERROR",
                String.format("[%s] %s", entityName, customMessage),
                httpStatus
        ) {};
    }
}