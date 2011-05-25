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
	
	//Type of hashing function to use from encrypting passwords.
	private static final String MD5 = "MD5";
	//Type of character encoding for encrypting passwords.
	private static final String UTF8 = "UTF-8";	
	//secret for encoding the password with
	private static final String HASH_SALT = "GAMES GAMES GAMES";
	
	/**
	 * Converts a plain text password into it's encrypted form.
	 * 
	 * @param password The password to encrypt.
	 * 
	 * @return The encrypted password.
	 * 
	 * @throws Exception When encoding fails due to NoSuchAlgorithmException or UnsupportedEncodingException 
	 */
	@Nonnull
	public static String encryptPassword(@Nonnull String password) throws ServerException {
		try {
			final MessageDigest messageDigest = MessageDigest.getInstance(MD5);
	        final StringBuffer  hashTextBuffer = new StringBuffer();
	        String              rawByteHexCharacter;
	        
            messageDigest.update(HASH_SALT.getBytes(UTF8));	        
	        messageDigest.update(password.getBytes(UTF8));
	        
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
			throw new ServerException("No such algorithm ("+MD5+") for encoding passwords", e);
		} catch (UnsupportedEncodingException e) {
			throw new ServerException("Unsupported character encoding ("+UTF8+") for encoding passwords", e);
		} 
	}
}
