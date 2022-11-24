package com.otp.controllers;

import java.time.Instant;
import java.time.LocalTime;

import javax.validation.Valid;

import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.otp.dto.OtpDto;
import com.otp.dto.UserDto;
import com.otp.exception.OtpException;
import com.otp.response.OtpSeccessGenerationMessage;
import com.otp.response.OtpValidationSuccessMessage;
import com.otp.response.ResendSuccessMessage;
import com.otp.serviceimpl.TOTPServiceImpl;
import lombok.extern.slf4j.Slf4j;

@RestController

@RequestMapping("/v1.0/otp")
public class OTPController {

	@Autowired
	TOTPServiceImpl tOTPServiceImpl;
	
	private String result = "";
	
	Long ids = (long) 0;

	
	
	@PostMapping("/generateOTP")
	public ResponseEntity<OtpSeccessGenerationMessage> generateOTP(@Valid @RequestBody UserDto userdto) {
		byte[] sec = (userdto.getEmail() + LocalTime.now()).getBytes();
		for (byte b : sec) {
			result = result.concat("" + b);
		}
		try {
			String otp = this.tOTPServiceImpl.generateTOTP256(result, "30", "6");
			OtpSeccessGenerationMessage res = OtpSeccessGenerationMessage.builder().id(ids++).traceID(Instant.now().toEpochMilli())
					.message("otp generated successfully").otp(otp).statusCode("200").build();
			return new ResponseEntity<>(res, HttpStatus.OK);
		} catch (Exception ex) {
			throw new OtpException("otp not generated");
		}

	}
	
	
	

	@PostMapping("/validateOTPs")
	public ResponseEntity<?> validateOTP(@RequestBody OtpDto otpdto) {
		return this.tOTPServiceImpl.validateOtp("" + otpdto.getOtp());

	}
	
	

	@PostMapping("/resendOTP")
	public ResponseEntity<?> resendOtp() {

		String msg = this.tOTPServiceImpl.resendOtp(result, "30", "6");
		if (msg.length() == 6) {
			ResendSuccessMessage res = ResendSuccessMessage.builder().successcode("200")
					.message("OTP resend successfully").build();
			return new ResponseEntity<>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(msg, HttpStatus.OK);
		}
	}

}
