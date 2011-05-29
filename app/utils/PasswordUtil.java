package utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;

import exceptions.ServerException;

/**
 * Static methods shared by the various services that need to create/check passwords.
 */
public class PasswordUtil {
	
	private static final String HASH_ALGORITHM = "MD5";
	private static final String ENCODING = "UTF-8";
	
	/**
	 * Converts a plain text password into a hashed form.
	 * 
	 * @param password The password to encrypt
	 * @param salt A salt to use -- null if you don't want to use a salt.
	 * 
	 * @return The hashed password.
	 * 
	 * @throws ServerException When encoding fails due to NoSuchAlgorithmException or UnsupportedEncodingException 
	 */
	@Nonnull
	public static String hashPassword(@Nonnull String password, String salt) throws ServerException {
		try {
			final MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
	        final StringBuffer  hashTextBuffer = new StringBuffer();
	        String              rawByteHexCharacter;
	        
	        if (salt != null) {
	        	messageDigest.update(salt.getBytes(ENCODING));
	        }
	        messageDigest.update(password.getBytes(ENCODING));
	        
	        // Encode as Base16 (Hex)
	        final byte[] rawBytes = messageDigest.digest();
	        
	        for (int rawByteIndex = 0; rawByteIndex < rawBytes.length; rawByteIndex++)
	        {
	            rawByteHexCharacter = Integer.toHexString(rawBytes[rawByteIndex]).toUpperCase();

	            if  (rawByteHexCharacter.length() < 2)
	            {
	                hashTextBuffer.append('0');
	                hashTextBuffer.append(rawByteHexCharacter);
	            }
	            else 
	            {
	                //  Get the last two hex characters and ignore the starting characters.
	                hashTextBuffer.append(rawByteHexCharacter.substring(rawByteHexCharacter.length() - 2 ));
	            }
	        }   
		    return hashTextBuffer.toString();
		// FIXME Why the catch and re-throw? Do we need a custom Exception type?
		} catch (NoSuchAlgorithmException e) {
			throw new ServerException("No such algorithm (" + HASH_ALGORITHM + ") for encoding passwords", e);
		} catch (UnsupportedEncodingException e) {
			throw new ServerException("Unsupported character encoding (" + ENCODING + ") for encoding passwords", e);
		} 
	}
}

