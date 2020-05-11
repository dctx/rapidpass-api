package ph.devcon.rapidpass.utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Mockito.when;

/**
 * @author j-espelita@ti.com
 */
@ExtendWith(MockitoExtension.class)
class KeycloakUtilsTest {

    @Mock
    Authentication authentication;

    @Test
    void getAttributes() {
        // arrange
        // mock security context to return keycloak principal with aportypes attribute
        final AccessToken accessToken = new AccessToken();
        accessToken.setOtherClaims("aportypes", "AP1,AP2");
        final KeycloakPrincipal<KeycloakSecurityContext> testPrincipal =
                new KeycloakPrincipal<>("test principal", new KeycloakSecurityContext(
                        "token", accessToken, "test", new IDToken()));

        when(authentication.getPrincipal()).thenReturn(testPrincipal);

        final SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);

        //act
        final Map<String, String> attributes = KeycloakUtils.getAttributes();

        // assert
        assertThat(attributes, hasEntry("aportypes", "AP1,AP2"));
    }
}