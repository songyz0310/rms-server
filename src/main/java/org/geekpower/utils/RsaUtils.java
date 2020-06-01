package org.geekpower.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RsaUtils {

	private static Logger logger = LoggerFactory.getLogger(RsaUtils.class);

	private static PublicKey PUBLIC_KEY = null;
	private static PrivateKey PRIVATE_KEY = null;
	static {
		try (InputStream publicIs = JwtUtils.class.getClassLoader().getResourceAsStream("rsa.pub");
				InputStream privateIs = JwtUtils.class.getClassLoader().getResourceAsStream("rqa.pri")) {
			ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
			ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int rc = 0;
			while ((rc = publicIs.read(buff, 0, 100)) > 0) {
				byteArrayOutputStream1.write(buff, 0, rc);
			}

			X509EncodedKeySpec x509 = new X509EncodedKeySpec(byteArrayOutputStream1.toByteArray());
			KeyFactory factory = KeyFactory.getInstance("RSA");
			PUBLIC_KEY = factory.generatePublic(x509);

			rc = 0;
			while ((rc = privateIs.read(buff, 0, 100)) > 0) {
				byteArrayOutputStream2.write(buff, 0, rc);
			}

			PKCS8EncodedKeySpec pks8 = new PKCS8EncodedKeySpec(byteArrayOutputStream2.toByteArray());

			PRIVATE_KEY = factory.generatePrivate(pks8);

		} catch (Exception e) {
			logger.error("初始化公钥私钥异常:", e);
		}
	}

	// 获取公钥
	public static PublicKey getPublicKey() {
		return PUBLIC_KEY;
	}

	// 获取私钥
	public static PrivateKey getPrivateKey() {
		return PRIVATE_KEY;
	}

}