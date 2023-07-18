package com.vti.threadsmeta.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "statusCode", "timestamp", "path", "message", "details" })
public class ErrorResponse {

    @JsonProperty("status_code")
    private int statusCode;
    private Date timestamp;
    private String path;
    private String message;
    private String info;
}
