package com.otp.serviceimpl;

import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.cache.LoadingCache;
import com.otp.exception.OtpException;
import com.otp.response.OtpValidationSuccessMessage;


@Service
public class TOTPServiceImpl implements IOTPService {

	private static final int[] DIGITS_POWER = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };
	private static LocalDateTime duration = null;
	private long ids = 0;
	private int counter = 0;
	private String result = null;

	private TOTPServiceImpl() {
	}

	public String generateTOTP256(String key, String time, String returnDigits) {
		return generateTOTP(key, time, returnDigits, "HmacSHA256");
	}

	public String generateTOTP(String key, String time, String returnDigits, String crypto) {
		int codeDigits = Integer.decode(returnDigits).intValue();

		while (time.length() < 16)
			time = "0".concat(time);
		byte[] msg = hexStr2Bytes(time);
		byte[] k = hexStr2Bytes(key);
		byte[] hash = hmacSHA(crypto, k, msg);
		int offset = hash[hash.length - 1] & 0xf;
		int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16)
				| ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
		int otp = binary % DIGITS_POWER[codeDigits];
		result = Integer.toString(otp);
		duration = LocalDateTime.now().plusSeconds(30);
		while (result.length() < codeDigits) {
			result = "0".concat(result);
		}
		return result;
	}

	public byte[] hmacSHA(String crypto, byte[] keyBytes, byte[] text) {
		try {
			Mac hmac;
			hmac = Mac.getInstance(crypto);
			SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
			hmac.init(macKey);
			return hmac.doFinal(text);
		} catch (GeneralSecurityException gse) {
			throw new UndeclaredThrowableException(gse);
		}
	}

	public byte[] hexStr2Bytes(String hex) {
		byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
		byte[] ret = new byte[bArray.length - 1];
		for (int i = 0; i < ret.length; i++)
			ret[i] = bArray[i + 1];
		return ret;
	}

	@Override
	public ResponseEntity<?> validateOtp(String otp) {

		if(!otp.equals(result)) {
			return new ResponseEntity<>("OTP not correct...", HttpStatus.GATEWAY_TIMEOUT);
		}
		
	else if (LocalDateTime.now().compareTo(duration) < 0) {
			if (otp.equals(result)) {
				OtpValidationSuccessMessage res = OtpValidationSuccessMessage.builder().id(ids++)
						.traceid(Instant.now().toEpochMilli()).message("Otp Validate Successfully").statuscode(200l)
						.build();
				return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
			} else {
				throw new OtpException("Otp Not Validate");
			}

		}
		
		else {

			return new ResponseEntity<>("otp expire please resend...", HttpStatus.GATEWAY_TIMEOUT);
		}

	}

	@Override
	public String resendOtp(String key, String time, String returnDigits) {
		if (counter == -1) {
			return "you crossed maximum no. of limits please try again after 24 hr";
		} else if (counter < 5) {
			counter++;
			return generateTOTP(key, time, returnDigits, "HmacSHA256");
		}

		else {
			counter = -1;
			return "you crossed maximum no. of limits please try again after 24 hr";
		}

	}
}
