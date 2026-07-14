package com.manuel.vigicontrol.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponse {
    private int status;
    private String message;
    private Object data;
}