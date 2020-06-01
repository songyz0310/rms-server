package org.geekpower.service.impl;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

import org.aspectj.util.FileUtil;
import org.geekpower.common.BaseError;
import org.geekpower.common.BaseException;
import org.geekpower.common.dto.UserDTO;
import org.geekpower.common.dto.UserSessionDTO;
import org.geekpower.service.IAuthorityService;
import org.geekpower.utils.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * 使用JWT鉴权
 * 
 * @author songyz
 * @createTime 2020-06-01 13:33:47
 */
@Service
public class AuthorityServiceImpl implements IAuthorityService {

    private static Logger logger = LoggerFactory.getLogger(AuthorityServiceImpl.class);

    private static final int EXP_HOUR = 1;
    private static PublicKey PUBLIC_KEY = null;
    private static PrivateKey PRIVATE_KEY = null;

    static {
        try (InputStream publicIs = IAuthorityService.class.getClassLoader().getResourceAsStream("rsa.pub");
                InputStream privateIs = IAuthorityService.class.getClassLoader().getResourceAsStream("rqa.pri")) {

            X509EncodedKeySpec x509 = new X509EncodedKeySpec(FileUtil.readAsByteArray(publicIs));
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PUBLIC_KEY = factory.generatePublic(x509);

            PKCS8EncodedKeySpec pks8 = new PKCS8EncodedKeySpec(FileUtil.readAsByteArray(privateIs));
            PRIVATE_KEY = factory.generatePrivate(pks8);
        }
        catch (Exception e) {
            logger.error("初始化公钥私钥异常:", e);
        }
    }

    @Override
    public UserSessionDTO createSession(UserDTO userParam) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, EXP_HOUR);
        Date expireTime = instance.getTime();

        String token = Jwts.builder().setClaims(BeanCopier.object2Map(userParam))//
                .signWith(SignatureAlgorithm.RS256, PRIVATE_KEY)//
                .setExpiration(expireTime)//
                .compact();

        UserSessionDTO sessionUser = BeanCopier.copy(userParam, UserSessionDTO.class);
        sessionUser.setToken(token);
        sessionUser.setExpireTime(expireTime.getTime());

        return sessionUser;
    }

    @Override
    public UserSessionDTO checkSession(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(PUBLIC_KEY).parseClaimsJws(token).getBody();
            UserSessionDTO sessionUser = BeanCopier.map2Object(claims, UserSessionDTO.class);
            sessionUser.setExpireTime(claims.getExpiration().getTime());
            sessionUser.setToken(token);
            return sessionUser;
        }
        catch (ExpiredJwtException e) {
            throw new BaseException(BaseError.SEC_ACCOUNT_EXPIRED.getCode(), e.getMessage());
        }
        catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BaseException(BaseError.SEC_DECRYPT_FAILED.getCode(), e.getMessage());
        }
    }

}
