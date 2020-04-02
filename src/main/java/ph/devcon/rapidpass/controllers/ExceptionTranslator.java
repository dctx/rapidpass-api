package ph.devcon.rapidpass.controllers;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ph.devcon.rapidpass.services.RegistryService;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 *
 * @author jonasespelita@gmail.com
 */
@ControllerAdvice
@Slf4j
public class ExceptionTranslator {

    /**
     * Converts validation errors to JSON.
     *
     * @param ex validation exception
     * @return response body with errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> processValidationError(MethodArgumentNotValidException ex) {
        //noinspection ConstantConditions
        return ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> illegalArgsError(IllegalArgumentException ex) {
        log.warn("Request Error! ", ex);
        return ImmutableMap.of("message", ex.getMessage());
    }

    @ExceptionHandler(RegistryService.UpdateAccessPassException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> updateAcessPassError(RegistryService.UpdateAccessPassException ex) {
        return ImmutableMap.of("message", ex.getMessage());
    }


}
