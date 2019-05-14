package com.chestermere.lake.temperature.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.configuration2.Configuration;

import com.chestermere.lake.temperature.Server;
import com.google.common.flogger.FluentLogger;

public class Encryption {

	private final Configuration configuration;
	private final FluentLogger logger;
	private final String algorithm;

	public Encryption(Server instance) {
		this.logger = instance.getLogger();
		this.configuration = instance.getConfiguration();
		this.algorithm = configuration.getString("cipherAlgorithm", "AES/CBC/PKCS5Padding");
	}

	public final byte[] hash() {
		try {
			byte[] base64 = Base64.getEncoder().encode(configuration.getString("password").getBytes(StandardCharsets.UTF_8));
			return MessageDigest.getInstance(configuration.getString("passwordAlgorithm", "SHA-256")).digest(base64);
		} catch (NoSuchAlgorithmException e) {
			logger.atSevere()
					.withCause(e)
					.log("The algorithm '%s' does not exist for your system. Please use a different algorithm.", algorithm);
			return null;
		}
	}

	public byte[] encrypt(String keyString, String algorithm, byte[] packet) {
		try {
			byte[] serializedKey = keyString.getBytes(Charset.forName("UTF-8"));
			if (serializedKey.length != 16) {
				logger.atSevere().log("The cipher key length is invalid. The length needs to be 16 but was: %s", serializedKey.length);
				return null;
			}
			SecretKeySpec key = new SecretKeySpec(serializedKey, "AES");
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
			return Base64.getEncoder().encode(cipher.doFinal(packet));
		} catch (NoSuchAlgorithmException e) {
			logger.atSevere()
					.withCause(e)
					.log("The algorithm '%s' does not exist for your system. Please use a different algorithm.", algorithm);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			logger.atSevere()
					.withCause(e)
					.log("There was an error encrypting.");
		}
		return null;
	}

	public Object decrypt(String keyString, String algorithm, byte[] input) {
		try {
			byte[] serializedKey = keyString.getBytes(Charset.forName("UTF-8"));
			if (serializedKey.length != 16) {
				logger.atSevere().log("The cipher key length is invalid. The length needs to be 16 but was: %s", serializedKey.length);
				return null;
			}
			SecretKeySpec key = new SecretKeySpec(serializedKey, "AES");
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
			byte[] decoded = Base64.getDecoder().decode((byte[]) input);
			return deserialize(cipher.doFinal(decoded));
		} catch (NoSuchAlgorithmException e) {
			logger.atSevere()
					.withCause(e)
					.log("The algorithm '%s' does not exist for your system. Please use a different algorithm.", algorithm);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			logger.atSevere()
					.withCause(e)
					.log("There was an error decrypting.");
		}
		return null;
	}

	public byte[] serialize(Object object) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(out);
			outputStream.writeObject(object);
			return out.toByteArray();
		} catch (IOException e) {
			logger.atSevere()
					.withCause(e)
					.log("There was an error while serializing.");
		}
		return null;
	}

	public Object deserialize(byte[] input) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(input);
			ObjectInputStream inputStream = new ObjectInputStream(in);
			return inputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			logger.atSevere()
					.withCause(e)
					.log("There was an error while deserializing.");
		}
		return null;
	}

}
