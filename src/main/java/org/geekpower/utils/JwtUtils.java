package org.geekpower.utils;

import java.util.Date;

import org.geekpower.common.dto.UserDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtils {

	private static String JWT_KEY_ID = "id";
	private static String JWT_KEY_USER_NAME = "username";

	/**
	 * 私钥加密token
	 *
	 * @param userInfo      载荷中的数据
	 * @param privateKey    私钥字节数组
	 * @param expireMinutes 过期时间，单位秒
	 * @return
	 * @throws Exception
	 */
	public static String generateToken(UserDTO userInfo, int expireMinutes) {
		return Jwts.builder().claim(JWT_KEY_ID, userInfo.getUserId()).claim(JWT_KEY_USER_NAME, userInfo.getUserName())
				.setExpiration(new Date(System.currentTimeMillis() + expireMinutes * 1000 * 60))
				.signWith(SignatureAlgorithm.RS256, RsaUtils.getPrivateKey()).compact();
	}

	/**
	 * 获取token中的用户信息
	 *
	 * @param token     用户请求中的令牌
	 * @param publicKey 公钥
	 * @return 用户信息
	 * @throws Exception
	 */
	public static UserDTO getInfoFromToken(String token) {
		Jws<Claims> claimsJws = Jwts.parser().setSigningKey(RsaUtils.getPublicKey()).parseClaimsJws(token);
		Claims body = claimsJws.getBody();
		return new UserDTO(ObjectUtils.toInt(body.get(JWT_KEY_ID)), ObjectUtils.toString(body.get(JWT_KEY_USER_NAME)));
	}

}