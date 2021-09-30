package com.subra.aem.rjs.oauth.services;

import com.subra.aem.rjs.oauth.dto.OauthResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

public interface OauthIntegrationService {

    String getAuthorizationHeaderValue();

    void updateAuthorizationHeader(HttpRequestBase httpRequest);

    String getAccessToken();

    String getJWTToken();

    OauthResponse getOauthResponse();

    String getJWTTokenFromFile() throws NoSuchAlgorithmException, InvalidKeySpecException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException;

    @ObjectClassDefinition(name = "Oauth Integration Service", description = "Integration Service Description")
    public @interface Config {

        @AttributeDefinition(name = "endpoint")
        String oauth_endpoint() default "http://localhost:4502/oauth/token";

        @AttributeDefinition(name = "Client Id")
        String client_id() default "f08l4uue4oe5pmk536uigqbmll-twmonwne";

        @AttributeDefinition(name = "Client Secret")
        String client_secret() default "jq74jsih5r867368ndg7q5fvg1";

        @AttributeDefinition(name = "Redirect URI", description = "redirection_uri should match with endpoint")
        String redirect_uri() default "http://localhost:4503/oauth/callback";

        @AttributeDefinition(name = "Redirect URI", description = "redirection_uri should match with endpoint")
        String grant_type() default "urn:ietf:params:oauth:grant-type:jwt-bearer";

        @AttributeDefinition(name = "Private Key", description = "redirection_uri should match with endpoint")
        String private_key() default "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEowIBAAKCAQEAkLW6rBG+jYpZ6bNoCmnHYDP2zkGvzOB+P6xvFgEAxeD1182j\n" +
                "w1rWAACe/jrcfbbWbdjZfZdXBLtS4w/9mhE7M9jaHPJ1DeEksRSD5EDWhdY7kiEc\n" +
                "3FWddA07Y/tX2eQGFxS9tM9rCXAsI1gCH0WXCN2F5EmvIV+ZiMvZ/fLcPcIIA+wy\n" +
                "Ex0jJo1YNrU+nuSzqz46EinXWuM8ZuCy/0RSV2oCOxN8jf8C1Piu4rPXBIGC8FMr\n" +
                "w77p1cU2jVYrjtdLhreNZbeGm1DS0u3C2uy2LUr5M1m6I3tMsILCbIS9eY0S9EP1\n" +
                "mqUnJs0WrEmIO79mq3URZQ40+I4cB66au3ABGQIDAQABAoIBACHBMizm6rLrXbPE\n" +
                "tHURXX9UP7K24cIBb5F8sGDKlRqTMeVHw3ZTHu3bNzsIEdyFJJl05q6D+blR1LDA\n" +
                "TyB7+npKj/4GlEVLihqANY+tjByl7zlT3f6LPfIPEBZvT3u007LYlLOzWj1WXAVW\n" +
                "hxAyVFuuCIhKo74+JXsiSdQRrUv+N8N3A4TUzdrdIhndM9K8wF7Klg3R43IOsJML\n" +
                "jB8uUKMUjfy+BbB5WLb6sXdLSQoRZK+EM8BcNMtlGaNk62epceIBKXYFdU6rnn0e\n" +
                "E2rxZaXB5TbmVGsT3PSJQAiJ0UPTZLOPhozj/2vavROfb321QoC7P1b5MBCZ4Mv1\n" +
                "qOnKLQECgYEAwHwBInLvEm+NrnVqz6L+hyJp84rPeDRXBl8Q2IubGu9YKXz/JvnI\n" +
                "J0+QCkL43s8APu+FwAYdDVaQqe3Y8RYDMa1xtML95hgtDk93Gzke475lL29K0sf3\n" +
                "SePv/78z+Pntr01x1NQFWwDiOZjs9X+5Ffkwt3XKwXyHUWf2v/d87qECgYEAwHYA\n" +
                "4bXG9r84prUPl6lhdUVo1Yjx4ymGuXWmCP6DfXukk4ItjANCEweNx19lOSDZfTFv\n" +
                "eEqAolgFUwY8H1EKvCEQMz8eptLNidsWUkasiY0IIpuwzRv5xSH5ymaMXuv1uWBh\n" +
                "2/msovi3dCd/sCwHHZH3uCDsjd0FlEBkDvS613kCgYA1XOuPml+PM+AKosDG6cA4\n" +
                "xKCtBSm5gIEz4f0wm5uXnw1JCz9NlegYheVAAwst8iN2Tz88WjWJshsPEUKYq+qc\n" +
                "xiIE5B4xwTgUhJFXucFHkExLF0aeIxP6pzetJlwJRXjQpd7OCy2k6hzNrQjcktlE\n" +
                "Lz6DTiO0+oolVnZBCCLZoQKBgQCaNTLJpxuvk1HyCTtgGqBzFVVFWz/s8tX2/bQ2\n" +
                "mq+CuLIKWxDhka68jp3kNKRnXNHGxPh62r8s1PgXKPS9ZvMCAD/5v8ZwhpGXtoqx\n" +
                "C92tLOpqyHKmNZcwg+OgcABjAg9WJErkta3EuyJLGGbwIqbk45Mn3oGLvZ99xQYn\n" +
                "EaIviQKBgDDBFP3kKv7G93LVY46G7BYtNCNaeW+myudmJ6ZaB+wueF2rZbmef7vc\n" +
                "N7loKbopR1DSmAv4Q87J8+c00gNMZglkwmoy0b84IV+hlVfV4q3XAMy5dGc9KV9w\n" +
                "dpaG6clGB9hDlxsx+3JDiNEcPPSYju+v+YEszncSHmI9nRd2NZ05\n" +
                "-----END RSA PRIVATE KEY-----";

    }

}
