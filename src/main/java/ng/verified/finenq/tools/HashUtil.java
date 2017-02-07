package ng.verified.finenq.tools;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ng.verified.finenq.jbeans.ApplicationBean;

@Stateless
public class HashUtil {
	
	@Inject
	private ApplicationBean appBean ;
	
	/**
	 * All API parameters with the exception of the merchantId variable should be encrypted with TripleDES Algorithm using the API key value.
	 * This value should be protected and should not be sent as part of the pay-load
	 * 
	 * @param unencryptedString
	 * @return encrypted string
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String harden(String unencryptedString) 
			throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digestOfPassword = md.digest(appBean.getFw_key().getBytes("UTF-8"));
		byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

		for (int j = 0, k = 16; j < 8;) {
			keyBytes[k++] = keyBytes[j++];
		}

		SecretKey secretKey = new SecretKeySpec(keyBytes, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		byte[] plainTextBytes = unencryptedString.getBytes("utf-8");
		byte[] buf = cipher.doFinal(plainTextBytes);
		byte[] base64Bytes = Base64.getEncoder().encode(buf);
		String base64EncryptedString = new String(base64Bytes);

		return base64EncryptedString;
	}
	
	/**
	 * Should be used to soften encrypted API response parameters when required.
	 *  
	 * @param encryptedString
	 * @return plain text string
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String soften(String encryptedString) 
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		if(encryptedString == null)
			return "";
		
		byte[] message = Base64.getDecoder().decode(encryptedString.getBytes("utf-8"));

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digestOfPassword = md.digest(appBean.getFw_key().getBytes("UTF-8"));
		byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		
		for (int j = 0, k = 16; j < 8;) {
			keyBytes[k++] = keyBytes[j++];
		}
		
		SecretKey secretKey = new SecretKeySpec(keyBytes, "DESede");

		Cipher decipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		decipher.init(Cipher.DECRYPT_MODE, secretKey);

		byte[] plainText = decipher.doFinal(message);

		return new String(plainText, "UTF-8");

	}

}
