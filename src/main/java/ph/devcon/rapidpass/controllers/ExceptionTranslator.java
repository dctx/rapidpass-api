/*
 * Copyright (c) 2020.  DevConnect Philippines, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package ph.devcon.rapidpass.controllers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ph.devcon.rapidpass.exceptions.AccountLockedException;
import ph.devcon.rapidpass.services.RegistryService;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
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

    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Map<String, String> forbiddenError(Throwable e) {
        log.warn("Authentication Error!", e);
        return ImmutableMap.of("message", "You are not allowed to access resource.");
    }

    @ExceptionHandler({AccountLockedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Map<String, String> lockedOut(AccountLockedException e) {
        log.warn("Account was locked out", e);
        return ImmutableMap.of("message", e.getMessage());
    }

    /**
     * Converts errors related to exceptions related to user input into 400s
     *
     * @param ex a user input error
     * @return response body with errors
     */
    @ExceptionHandler({
            ConstraintViolationException.class,
            IllegalArgumentException.class,
            InvalidFormatException.class,
            RegistryService.UpdateAccessPassException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> illegalArgsError(Throwable ex) {
        log.warn("Request Error! ", ex);
        return ImmutableMap.of("message", ex.getMessage());
    }

    /**
     * Generic error catcher so that we don't print stack traces back to client.
     *
     * @param ex generic error
     * @return respoinse body with errors
     */
    @ExceptionHandler({Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, String> everythingElse(Throwable ex) {
        log.warn("Server error! ", ex);
        return ImmutableMap.of("message", "Something went wrong! Please contact application owners.");
    }
}
