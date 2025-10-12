package car.rental.core.common.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        LOG.error("Exception caught: " + exception.getClass().getName(), exception);

        ErrorResponse errorResponse;
        Response.Status status;

        // Check for database constraint violations in the cause chain first
        Throwable constraintViolation = findConstraintViolationInChain(exception);
        if (constraintViolation instanceof org.hibernate.exception.ConstraintViolationException) {
            status = Response.Status.CONFLICT;
            errorResponse = buildDatabaseConstraintErrorResponse((org.hibernate.exception.ConstraintViolationException) constraintViolation);
            return Response.status(status).entity(errorResponse).build();
        }

        if (exception instanceof ResourceNotFoundException) {
            status = Response.Status.NOT_FOUND;
            errorResponse = buildErrorResponse(status, exception.getMessage());
        } else if (exception instanceof car.rental.core.common.exception.BusinessException) {
            status = Response.Status.CONFLICT;
            errorResponse = buildErrorResponse(status, exception.getMessage());
        } else if (exception instanceof car.rental.core.common.exception.BadRequestException) {
            status = Response.Status.BAD_REQUEST;
            errorResponse = buildErrorResponse(status, exception.getMessage());
        } else if (exception instanceof BadRequestException) {
            status = Response.Status.BAD_REQUEST;
            errorResponse = buildErrorResponse(status, "Bad request: " + exception.getMessage());
        } else if (exception instanceof NotFoundException) {
            status = Response.Status.NOT_FOUND;
            errorResponse = buildErrorResponse(status, "Resource not found");
        } else if (exception instanceof ConstraintViolationException) {
            status = Response.Status.BAD_REQUEST;
            errorResponse = buildValidationErrorResponse((ConstraintViolationException) exception);
        } else if (exception instanceof jakarta.validation.ValidationException) {
            status = Response.Status.BAD_REQUEST;
            errorResponse = buildErrorResponse(status, "Validation failed: " + exception.getMessage());
        } else if (exception instanceof IllegalArgumentException) {
            status = Response.Status.BAD_REQUEST;
            errorResponse = buildErrorResponse(status, exception.getMessage());
        } else if (exception instanceof IllegalStateException) {
            status = Response.Status.CONFLICT;
            errorResponse = buildErrorResponse(status, exception.getMessage());
        } else {
            status = Response.Status.INTERNAL_SERVER_ERROR;
            errorResponse = buildErrorResponse(status, "An unexpected error occurred. Please contact support.");
        }

        return Response.status(status)
                .entity(errorResponse)
                .build();
    }

    private ErrorResponse buildErrorResponse(Response.Status status, String message) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.getStatusCode())
                .error(status.getReasonPhrase())
                .message(message)
                .path(uriInfo != null ? uriInfo.getPath() : null)
                .build();
    }

    private ErrorResponse buildValidationErrorResponse(ConstraintViolationException exception) {
        List<ErrorResponse.ValidationError> validationErrors = exception.getConstraintViolations()
                .stream()
                .map(violation -> ErrorResponse.ValidationError.builder()
                        .field(getFieldName(violation))
                        .message(violation.getMessage())
                        .build())
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(Response.Status.BAD_REQUEST.getStatusCode())
                .error(Response.Status.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(uriInfo != null ? uriInfo.getPath() : null)
                .validationErrors(validationErrors)
                .build();
    }

    private ErrorResponse buildDatabaseConstraintErrorResponse(org.hibernate.exception.ConstraintViolationException exception) {
        String message = "A database constraint was violated";
        String sqlMessage = exception.getSQLException() != null ? exception.getSQLException().getMessage() : "";
        String constraintName = exception.getConstraintName();
        String sql = exception.getSQL();

        // Extract duplicate key value if present
        String duplicateValue = extractDuplicateKeyValue(sqlMessage);

        // Parse common constraint violation messages
        if (sqlMessage.contains("UNIQUE KEY constraint") || sqlMessage.contains("unique constraint")) {
            // Try to determine which field based on constraint name, SQL statement, or duplicate value
            String fieldName = determineConstraintField(constraintName, sql, sqlMessage, duplicateValue);

            if ("username".equalsIgnoreCase(fieldName)) {
                message = duplicateValue != null
                        ? String.format("Username '%s' already exists. Please choose a different username.", duplicateValue)
                        : "Username already exists. Please choose a different username.";
            } else if ("email".equalsIgnoreCase(fieldName)) {
                message = duplicateValue != null
                        ? String.format("Email '%s' already exists. Please use a different email address.", duplicateValue)
                        : "Email already exists. Please use a different email address.";
            } else {
                message = duplicateValue != null
                        ? String.format("A record with value '%s' already exists. Please use different values.", duplicateValue)
                        : "A record with this value already exists. Please use different values.";
            }
        } else if (sqlMessage.contains("FOREIGN KEY constraint") || sqlMessage.contains("foreign key")) {
            message = "Cannot perform operation due to related records. Please check dependencies.";
        } else if (sqlMessage.contains("PRIMARY KEY constraint") || sqlMessage.contains("primary key")) {
            message = duplicateValue != null
                    ? String.format("A record with identifier '%s' already exists.", duplicateValue)
                    : "A record with this identifier already exists.";
        } else if (sqlMessage.contains("CHECK constraint")) {
            message = "The provided value does not meet the required constraints.";
        } else if (sqlMessage.contains("NOT NULL constraint") || sqlMessage.contains("cannot be null")) {
            message = "A required field is missing. Please provide all mandatory information.";
        }

        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(Response.Status.CONFLICT.getStatusCode())
                .error(Response.Status.CONFLICT.getReasonPhrase())
                .message(message)
                .path(uriInfo != null ? uriInfo.getPath() : null)
                .build();
    }

    private String determineConstraintField(String constraintName, String sql, String sqlMessage, String duplicateValue) {
        // Check constraint name
        if (constraintName != null) {
            String lowerConstraint = constraintName.toLowerCase();
            if (lowerConstraint.contains("username")) {
                return "username";
            }
            if (lowerConstraint.contains("email")) {
                return "email";
            }
        }

        // Check SQL message
        if (sqlMessage != null) {
            String lowerMessage = sqlMessage.toLowerCase();
            if (lowerMessage.contains("username")) {
                return "username";
            }
            if (lowerMessage.contains("email")) {
                return "email";
            }
        }

        // Check duplicate value format (email pattern)
        if (duplicateValue != null && duplicateValue.contains("@")) {
            return "email";
        }

        // Check SQL statement to see which table and potentially which field
        if (sql != null) {
            String lowerSql = sql.toLowerCase();
            if (lowerSql.contains("username")) {
                return "username";
            }
            if (lowerSql.contains("email")) {
                return "email";
            }
        }

        return null;
    }

    private String extractDuplicateKeyValue(String sqlMessage) {
        // Extract value from pattern: "The duplicate key value is (value)."
        if (sqlMessage.contains("duplicate key value is")) {
            int startIdx = sqlMessage.indexOf("(");
            int endIdx = sqlMessage.indexOf(")", startIdx);
            if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
                return sqlMessage.substring(startIdx + 1, endIdx).trim();
            }
        }
        return null;
    }

    private String getFieldName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        if (propertyPath.contains(".")) {
            return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
        }
        return propertyPath;
    }

    private Throwable findConstraintViolationInChain(Throwable exception) {
        // Check the exception itself first
        if (exception instanceof org.hibernate.exception.ConstraintViolationException) {
            return exception;
        }

        // Then check the cause chain
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
                return cause;
            }
            cause = cause.getCause();
        }
        return null;
    }
}
