package com.otp.response;

import java.time.LocalDateTime;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
public class ErrorMessage {

	
	private int errorcode;
	private LocalDateTime timestamp;
	private String statuscode;
	private String error;
	private long traceid;
	private Object trace;
	private String path;
	
	
}
