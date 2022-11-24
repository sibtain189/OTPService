package com.otp.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OtpValidationSuccessMessage {

	
	private long id;
	private long statuscode;
	private long traceid;
	private String message;
	
	
}
