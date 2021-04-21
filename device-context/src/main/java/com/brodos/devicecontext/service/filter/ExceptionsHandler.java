/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.service.filter;

import com.brodos.devicecontext.ErrorCodes;
import com.brodos.devicecontext.service.assembler.ResponseAssembler;
import com.brodos.devicecontext.service.exception.DeviceContextException;
import java.util.HashMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author padhaval
 */
@ControllerAdvice
@RestController
public class ExceptionsHandler {

    @Autowired
    ResponseAssembler responseAssembler;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<HashMap<Object, Object>> handleAllExceptions(HttpMessageNotReadableException ex, WebRequest request) {        
        HashMap<Object, Object> map = new HashMap<>();
        map.put("message", ExceptionUtils.getRootCauseMessage(ex));
        return new ResponseEntity<>(map, HttpStatus.valueOf(ErrorCodes.REQUEST_BODY_MISSING.getCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<HashMap<Object, Object>> handleAllExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("message", ex.getBindingResult().getAllErrors().stream().findFirst().get().getDefaultMessage());
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JDBCConnectionException.class)
    @ResponseBody
    public ResponseEntity<HashMap<Object, Object>> handleAllExceptions(JDBCConnectionException ex, WebRequest request) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("message", ErrorCodes.DB_CONNECTION_ERROR.getMessage());
        return new ResponseEntity<>(map, HttpStatus.valueOf(ErrorCodes.DB_CONNECTION_ERROR.getCode()));
    }    @ExceptionHandler(DeviceContextException.class)
    @ResponseBody
    public ResponseEntity<HashMap<Object, Object>> handleAllExceptions(DeviceContextException ex, WebRequest request) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("message", ex.getMessage());
        return new ResponseEntity<>(map, HttpStatus.valueOf(ex.getCode()));
    }
}
