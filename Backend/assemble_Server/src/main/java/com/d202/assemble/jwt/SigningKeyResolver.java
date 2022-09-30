package com.d202.assemble.jwt;

import java.security.Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;

/**
 * JwsHeader를 통해 Signature 검증에 필요한 Key를 가져오는 코드를 구현합니다.
 */
public class SigningKeyResolver extends SigningKeyResolverAdapter {
    public static SigningKeyResolver instance = new SigningKeyResolver();

    @SuppressWarnings("rawtypes")
	@Override
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        String keyId = jwsHeader.getKeyId();
        if (keyId == null)
            return null;
        return JwtKey.getKey(keyId);
    }
}