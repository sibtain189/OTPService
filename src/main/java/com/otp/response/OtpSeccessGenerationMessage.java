package com.otp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
public class OtpSeccessGenerationMessage{

	private String statusCode;
	private Long traceID;
	private String otp;
	private String message;
	private Long id;
	
}
