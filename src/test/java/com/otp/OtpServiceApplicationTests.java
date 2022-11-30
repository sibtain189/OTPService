package com.otp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.otp.dto.UserDto;
import com.otp.service.IOTPService;

import net.bytebuddy.ByteBuddy;

@SpringBootTest
class OtpServiceApplicationTests {

	@Autowired
	private IOTPService iotpService;

	String res = "";

//	for(byte b: screts) {
//		res = res+b;
//	}

	@Test
	public void generationOTP() {

		byte[] screts = ("sib@gmial.com" + LocalDateTime.now()).getBytes();
		
		for (byte b : screts) {
			res = res + b;
		}

		String getOTP = iotpService.generateTOTP256(res, "30", "6");
		assertEquals(6, getOTP.length());
	}

}
