package com.subra.aem.rjs.oauth.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subra.aem.rjs.oauth.dto.OauthResponse;
import com.subra.aem.rjs.oauth.services.OauthIntegrationService;
import io.jsonwebtoken.Jwts;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.helpers.CommonHelper;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static io.jsonwebtoken.SignatureAlgorithm.RS256;

@Component(service = OauthIntegrationService.class, immediate = true)
@ServiceDescription("RJS OAUTH Integration Service")
@Designate(ocd = OauthIntegrationService.Config.class)
public class OauthIntegrationServiceImpl implements OauthIntegrationService {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String CACHE_CONTROL = "cache-control";
    private static final String NO_CACHE = "no-cache";
    private static final String CONTENT_TYPE = "content-type";
    private static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";

    private static final Logger LOGGER = LoggerFactory.getLogger(OauthIntegrationServiceImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = CommonHelper.getObjectMapper();
    private static final Base64.Decoder DECODER = Base64.getMimeDecoder();

    private OauthResponse oauthResponse;
    private String endpoint;
    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private String grantType;
    private String privateKey;

    @Activate
    @Modified
    protected void activate(Config config) {
        endpoint = config.oauth_endpoint();
        clientId = config.client_id();
        clientSecret = config.client_secret();
        redirectURI = config.redirect_uri();
        grantType = config.grant_type();
        privateKey = config.private_key();
    }

    @Override
    public String getAuthorizationHeaderValue() {
        return BEARER + getAccessToken();
    }

    @Override
    public void updateAuthorizationHeader(HttpRequestBase httpRequest) {
        httpRequest.addHeader(AUTHORIZATION, getAuthorizationHeaderValue());
    }

    @Override
    public String getAccessToken() {
        final String accessToken = StringUtils.defaultIfBlank(getOauthResponse().getAccessToken(), StringUtils.EMPTY);
        LOGGER.debug("Access Token : {}", accessToken);
        return accessToken;
    }

    @Override
    public String getJWTToken() {
        final String jwtToken = StringUtils.defaultIfBlank(getOauthResponse().getJwtToken(), StringUtils.EMPTY);
        LOGGER.debug("JWT Token : {}", jwtToken);
        return jwtToken;
    }

    // Currently, WIP because of OauthIntegrationServiceImpl#getKey()
    @Override
    public String getJWTTokenFromFile() throws NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException {
        Instant now = Instant.now();
        return Jwts
                .builder()
                .setAudience(endpoint)
                .setIssuer(clientId)
                .setSubject(clientId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(5L, ChronoUnit.MINUTES)))
                .addClaims(Collections.singletonMap("scope", "profile"))
                .addClaims(Collections.singletonMap("cty", "code"))
                .signWith(RS256, getKey())
                .compact();
    }

    @Override
    public OauthResponse getOauthResponse() {
        if (isValid()) {
            return oauthResponse;
        }
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost(endpoint);
            post.addHeader(CACHE_CONTROL, NO_CACHE);
            post.addHeader(CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED);

            final String generatedJWTToken = generateJwtToken();
            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("client_id", clientId));
            params.add(new BasicNameValuePair("client_secret", clientSecret));
            params.add(new BasicNameValuePair("assertion", generatedJWTToken));
            params.add(new BasicNameValuePair("redirect_uri", redirectURI));
            params.add(new BasicNameValuePair("grant_type", grantType));
            post.setEntity(new UrlEncodedFormEntity(params));
            Instant now = Instant.now();
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.error("response code {} ", response.getStatusLine().getStatusCode());
            }
            String result = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
            oauthResponse = OBJECT_MAPPER.readValue(result, OauthResponse.class);
            oauthResponse.setIssuedAt(now.getEpochSecond());
            oauthResponse.setJwtToken(generatedJWTToken);
            LOGGER.debug("JSON Response : {}", result);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.error("Exception generating OauthResponse", e);
        }
        return oauthResponse;
    }

    private boolean isValid() {
        return oauthResponse != null && (oauthResponse.getExpiresAt() > Instant.now().getEpochSecond());
    }

    private String generateJwtToken() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Instant now = Instant.now();
        return Jwts
                .builder()
                .setId(UUID.randomUUID().toString())
                .setAudience(endpoint)
                .setIssuer(clientId)
                .setSubject(clientId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(60L, ChronoUnit.MINUTES)))
                .addClaims(Collections.singletonMap("scope", "profile"))
                .addClaims(Collections.singletonMap("cty", "code"))
                .signWith(RS256, getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buildPkcs8Key(privateKey));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private byte[] buildPkcs8Key(String privateKey) {
        if (privateKey.contains("--BEGIN PRIVATE KEY--")) {
            return DECODER.decode(privateKey.replaceAll("-----\\w+ PRIVATE KEY-----", ""));
        }
        if (!privateKey.contains("--BEGIN RSA PRIVATE KEY--")) {
            LOGGER.error("Invalid cert format: {}", privateKey);
            return StringUtils.EMPTY.getBytes();
        }
        final byte[] innerKey = DECODER.decode(privateKey.replaceAll("-----\\w+ RSA PRIVATE KEY-----", ""));
        final byte[] result = new byte[innerKey.length + 26];
        System.arraycopy(DECODER.decode("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKY="), 0, result, 0, 26);
        System.arraycopy(BigInteger.valueOf((long) result.length - 4).toByteArray(), 0, result, 2, 2);
        System.arraycopy(BigInteger.valueOf(innerKey.length).toByteArray(), 0, result, 24, 2);
        System.arraycopy(innerKey, 0, result, 26, innerKey.length);
        return result;
    }

    // Currently, WIP, getting "java.io.IOException: PBE parameter parsing error: expecting the object identifier for AES cipher"
    private Key getKey() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(this.getClass().getClassLoader().getResourceAsStream("store.p12"), "notasecret".toCharArray());
        return keystore.getKey("privatekey", "notasecret".toCharArray());
    }

}
