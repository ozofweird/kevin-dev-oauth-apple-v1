package com.example.config.oauth;

import com.example.config.oauth.dto.AppleAccessTokenRes;
import com.example.config.oauth.dto.ApplePublicKeyRes;
import com.example.config.oauth.dto.AppleUserInfoRes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class AppleService {

    public String getRedirectUri() {

        String loginUrl = "https://appleid.apple.com/auth/authorize";

        StringBuilder redirectUri = new StringBuilder()
                .append(loginUrl)
                // client_id : https://developer.apple.com/account/resources/identifiers/list/serviceId
                .append("?client_id=").append("org.example.apple")
                .append("&redirect_uri=").append("https://apple.example.org/oauth/apple/callback")
                .append("&response_type=").append("code%20id_token")
                /**
                 * Send the Required Query Parameters : https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_js/incorporating_sign_in_with_apple_into_other_platforms
                 *
                 * - response_mode=form_post??? scope ?????? ??????
                 *   .append("&scope=").append("name%20email")
                 *   .append("&response_mode=").append("form_post")
                 *
                 * - response_mode=query??? code?????? ????????? ??? ????????? ?????? ??????
                 *   .append("&response_type=").append("code")
                 *   .append("&response_mode=").append("form_post")
                 */
                .append("&response_mode=").append("fragment")
                .append("&state=").append("test")
                .append("&nonce=").append("20B20D-0S8-1K8");

        return redirectUri.toString();
    }

    public AppleAccessTokenRes getAccessToken(String authorizationCode) {
        try {

            // 'access_token, expires_in, id_token, refresh_token, token_type' ????????? ?????? ?????? ????????? clientSecret ??????
            String clientSecret = createClientSecret();
            log.info("--------------------------------------");
            log.info("ClientSecret: " + clientSecret);

            /**
             * https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
             *
             * curl -v POST "https://appleid.apple.com/auth/token" \
             * -H 'content-type: application/x-www-form-urlencoded' \
             * -d 'client_id=org.example.apple' \
             * -d 'client_secret=clientSecret' \
             * -d 'code=authorizationCode' \
             * -d 'grant_type=authorization_code' \
             * -d 'redirect_uri=https://apple.example.org/oauth/apple/callback'
             */

            // Set header : Content-type: application/x-www-form-urlencoded
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Set body
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", authorizationCode);
            params.add("client_id", "org.example.apple");
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", "https://apple.example.org/oauth/apple/callback");
            params.add("grant_type", "authorization_code");

            // Set http entity
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // Request profile
            ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://appleid.apple.com/auth/token", request, String.class);
            Gson gson = new Gson();
            if (responseEntity.getStatusCode() == HttpStatus.OK)
                return gson.fromJson(responseEntity.getBody(), AppleAccessTokenRes.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String createClientSecret() {
        try {
            /**
             * ?????? ???????????? ????????? ???????????? ?????? ?????? ????????? jar?????? ?????? jar?????? classpath??? ?????? ??????????????? ?????? file??? ????????????
             * ????????? ??????????????? ??? ??????????????? ?????? ??????????????? FileSystem?????? ?????? ??? ?????? ??????('jar:file:..')??? ????????? FileNotFoundException ??????
             */
            ClassPathResource resource = new ClassPathResource("key/AuthKey_12345678.p8");
//            String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));

            byte[] bdata = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String privateKey = new String(bdata, StandardCharsets.UTF_8);

            Reader pemReader = new StringReader(privateKey);
            PEMParser pemParser = new PEMParser(pemReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();

            Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
            String clientSecret = Jwts.builder()
                    // ???????????? private key id: https://developer.apple.com/account/resources/authkeys/list
                    .setHeaderParam("kid", "12345678")
                    .setHeaderParam("alg", "ES256")
                    // App id prefix (Team id)
                    .setIssuer("TEAM_ID")
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(expirationDate)
                    .setAudience("https://appleid.apple.com")
                    // Service id identifier
                    .setSubject("org.example.oauth")
                    .signWith(SignatureAlgorithm.ES256, converter.getPrivateKey(object))
                    .compact();

            log.info("clientSecret: " + clientSecret);
            return clientSecret;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AppleUserInfoRes getUserId(String idToken) {

        try {
            // Public Key ????????????
            ApplePublicKeyRes applePublicKeyRes = getApplePublicKey();

            // id_token?????? kid??? alg ??? ??????
            String headerOfIdentityToken = idToken.substring(0, idToken.indexOf("."));
            Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerOfIdentityToken), StandardCharsets.UTF_8), Map.class);

            // ????????? ?????? ???????????? public key??? ??????
            ApplePublicKeyRes.Key key = applePublicKeyRes.getMatchedKeyBy(header.get("kid"), header.get("alg"))
                    .orElseThrow(() -> new NullPointerException("Failed get public key from apple's id server."));

            // n, e??? ?????? base64 uri-safe??? ????????? ?????? ?????? ????????? ????????? ????????? ??? Public key??? ???????????? ???
            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            /**
             * Verify the Identity Token : https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/verifying_a_user
             * Claim?????? ???????????? ????????? ???????????? ?????? ?????? ??????
             */
            Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(idToken).getBody();
            log.info("--------------------------------------");
            log.info("id: " + claims.getId());
            log.info("sub: " + claims.getSubject());
            log.info("iss: " + claims.getIssuer());
            log.info("aud: " + claims.getAudience());
            log.info("exp: " + claims.getExpiration());
            log.info("issAt: " + claims.getIssuedAt());

            return AppleUserInfoRes.builder()
                    .id(claims.getId())
                    .sub(claims.getSubject())
                    .iss(claims.getIssuer())
                    .aud(claims.getAudience())
                    .exp(claims.getExpiration().toString())
                    .iat(claims.getIssuedAt().toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ApplePublicKeyRes getApplePublicKey() {

        RestTemplate restTemplate = new RestTemplate();
        Gson gson  = new Gson();

        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://appleid.apple.com/auth/keys", String.class);
            if(responseEntity.getStatusCode() == HttpStatus.OK)
                return gson.fromJson(responseEntity.getBody(), ApplePublicKeyRes.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
