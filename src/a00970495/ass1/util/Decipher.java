package a00970495.ass1.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import a00970495.ass1.dataBase.DbConstants;

// EncipherDecipher.java

public class Decipher {

	private static Logger LOG = LogManager.getLogger();

	// salt for password-based encryption-decryption algorithm
	private static final byte[] salt = { (byte) 0xf5, (byte) 0x33, (byte) 0x01, (byte) 0x2a, (byte) 0xb2, (byte) 0xcc, (byte) 0xe4, (byte) 0x7f };

	// iteration count
	private static int iterationCount = 100;

	// obtain contents from file and decrypt
	@SuppressWarnings({ "unchecked", "unused" })
	public static InputStream readFromFileAndDecrypt(String password) throws InvalidKeySpecException {

		// used to rebuild byte list
		@SuppressWarnings("rawtypes")
		Vector fileBytes = new Vector();

		// create secret key
		Cipher cipher = null;

		try {
			// create password based encryption key object
			PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());

			// obtain instance for secret key factory
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");

			// generate secret key for encryption
			SecretKey secretKey = keyFactory.generateSecret(keySpec);

			// specifies parameters used with password based encryption
			PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterationCount);

			// obtain cipher instance reference.
			cipher = Cipher.getInstance("PBEWithMD5AndDES");

			// initialize cipher in decrypt mode
			cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
		}

		// handle NoSuchAlgorithmException
		catch (NoSuchAlgorithmException exception) {
			exception.printStackTrace();
			System.exit(1);
		}

		// handle InvalidKeySpecException
		catch (InvalidKeySpecException exception) {
			exception.printStackTrace();
			System.exit(1);
		}

		// handle InvalidKeyException
		catch (InvalidKeyException exception) {
			exception.printStackTrace();
			System.exit(1);
		}

		// handle NoSuchPaddingException
		catch (NoSuchPaddingException exception) {
			exception.printStackTrace();
			System.exit(1);
		}

		// handle InvalidAlgorithmParameterException
		catch (InvalidAlgorithmParameterException exception) {
			exception.printStackTrace();
			System.exit(1);
		}

		// read and decrypt contents from file
		try {
			System.out.println("here i am");
			// check path
			File file1 = new File(".");
			String Flist = "";
			for (String fileNames : file1.list()) {
				System.out.println(fileNames);
				Flist = Flist + ", " + fileNames;
			}

			//
			// ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			// InputStream input = classLoader.getResourceAsStream("src/a00970495.ass1.util/dbh.properties");

			// InputStream input = getServletContext().getResourceAsStream("/WEB-INF/foo.properties");
			InputStream fileInputStream = Decipher.class.getResourceAsStream(DbConstants.PROPERTIES_FILENAME);
			System.out.println(fileInputStream.toString());
			// FileInputStream fileInputStream = new FileInputStream(file);

			CipherInputStream in = new CipherInputStream(fileInputStream, cipher);

			// read bytes from stream.
			byte contents = (byte) in.read();

			int count = 0;
			while (contents != -1) {
				count++;
				fileBytes.add(new Byte(contents));
				contents = (byte) in.read();
			}
			in.close();

		}

		// handle IOException
		catch (IOException exception) {
			exception.printStackTrace();
			// System.exit(1);
		}

		// create byte array from contents in Vector fileBytes
		byte[] decryptedText = new byte[fileBytes.size()];

		for (int i = 0; i < fileBytes.size(); i++) {
			decryptedText[i] = ((Byte) fileBytes.elementAt(i)).byteValue();
		}

		// update Editor Pane contents.
		LOG.debug(new String(decryptedText));
		InputStream input = new ByteArrayInputStream(decryptedText);
		return input;
	}

}
