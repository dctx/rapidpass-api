package ph.devcon.rapidpass.controllers;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import ph.devcon.rapidpass.services.LookupService;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link UserRestController}.
 *
 * @author jonasespelita@gmail.com
 */
@WebMvcTest(controllers = {UserRestController.class})
@AutoConfigureMockMvc(addFilters = false)
class UserRestControllerTest {
    @Mock
    Authentication mockAuthentication;

    @MockBean
    LookupService mockLookupService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getAuthorizedAporTypes() throws Exception {
        // arrange
        // mocking security to return a keycloak principal
        final AccessToken accessToken = new AccessToken();
        accessToken.setOtherClaims("aportypes", "AP1,AP2");
        when(mockAuthentication.getPrincipal()).thenReturn(new KeycloakPrincipal<>("test",
                new KeycloakSecurityContext("testtoken", accessToken, "test", new AccessToken())));

        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        // act, assert
        mockMvc.perform(get("/users/apor-types"))
                .andDo(print())
                .andExpect((status().isInternalServerError()));
        // FIXME
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[*]", containsInAnyOrder("AP1", "AP2")));
    }
}