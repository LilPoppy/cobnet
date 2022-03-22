package com.cobnet.security;

import com.cobnet.event.account.AccountAuthenticationCreatingEvent;
import com.cobnet.exception.AuthenticationCreateException;
import com.cobnet.interfaces.security.Account;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.ExternalUser;
import com.cobnet.spring.boot.entity.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class OAuth2LoginAccountAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_FILTER_PROCESSES_URI = "/login/oauth2/code/*";

    private static final String AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE = "authorization_request_not_found";

    private static final String CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE = "client_registration_not_found";

    private ClientRegistrationRepository clientRegistrationRepository;

    private OAuth2AuthorizedClientRepository authorizedClientRepository;

    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();

    private Converter<OAuth2LoginAuthenticationToken, AccountAuthenticationToken> authenticationResultConverter = this::createAuthenticationResult;

    public OAuth2LoginAccountAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        this(clientRegistrationRepository, authorizedClientService, DEFAULT_FILTER_PROCESSES_URI);
    }

    public OAuth2LoginAccountAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService, String filterProcessesUrl) {
        this(clientRegistrationRepository, new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService), filterProcessesUrl);
    }



    public OAuth2LoginAccountAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository, String filterProcessesUrl) {
        super(filterProcessesUrl);
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        Assert.notNull(authorizedClientRepository, "authorizedClientRepository cannot be null");
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientRepository = authorizedClientRepository;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        MultiValueMap<String, String> params = toMultiMap(request.getParameterMap());

        if (!isAuthorizationResponse(params)) {

            OAuth2Error oauth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);

            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository.removeAuthorizationRequest(request, response);

        if (authorizationRequest == null) {

            OAuth2Error oauth2Error = new OAuth2Error(AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE);

            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        String registrationId = authorizationRequest.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);

        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);

        if (clientRegistration == null) {

            OAuth2Error oauth2Error = new OAuth2Error(CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE, "Client Registration not found with Id: " + registrationId, null);

            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        String redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request)).replaceQuery(null).build().toUriString();

        OAuth2AuthorizationResponse authorizationResponse = convert(params, redirectUri);

        Object details = this.authenticationDetailsSource.buildDetails(request);

        OAuth2LoginAuthenticationToken authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration, new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse));
        authenticationRequest.setDetails(details);

        OAuth2LoginAuthenticationToken result = (OAuth2LoginAuthenticationToken) this.getAuthenticationManager().authenticate(authenticationRequest);

        AccountAuthenticationToken token = this.authenticationResultConverter.convert(result);
        Assert.notNull(token, "authentication result cannot be null");
        token.setDetails(details);

        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(result.getClientRegistration(), token.getName(), result.getAccessToken(), result.getRefreshToken());

        this.authorizedClientRepository.saveAuthorizedClient(client, token, request, response);

        return token;
    }

    private AccountAuthenticationToken createAuthenticationResult(OAuth2LoginAuthenticationToken authenticationResult) {

        if(authenticationResult.getPrincipal() instanceof Account account) {

            AccountAuthenticationCreatingEvent event = new AccountAuthenticationCreatingEvent(account, authenticationResult);

            ProjectBeanHolder.getApplicationEventPublisher().publishEvent(event);

            if (account instanceof ExternalUser external) {

                User user = external.getUser();

                if (user != null) {

                    if (user.getExternalUsers().stream().noneMatch(child -> child.getUsername().equals(external.getUsername()))) {

                        user.getOwnedExternalUserCollection().add(external);

                        ProjectBeanHolder.getUserRepository().save(user);
                    }

                    return new AccountAuthenticationToken(user, user.getPassword());
                }
            }

            return new AccountAuthenticationToken(account);
        }

        throw new AuthenticationCreateException("Unsupported principal type: " + authenticationResult.getPrincipal().getClass().getName());
    }

    private MultiValueMap<String, String> toMultiMap(Map<String, String[]> map) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(map.size());

        map.forEach((key, values) -> {

            if (values.length > 0) {

                for (String value : values) {
                    params.add(key, value);
                }
            }
        });

        return params;
    }

    boolean isAuthorizationResponse(MultiValueMap<String, String> request) {

        return isAuthorizationResponseSuccess(request) || isAuthorizationResponseError(request);
    }

    boolean isAuthorizationResponseSuccess(MultiValueMap<String, String> request) {

        return StringUtils.hasText(request.getFirst(OAuth2ParameterNames.CODE)) && StringUtils.hasText(request.getFirst(OAuth2ParameterNames.STATE));
    }

    boolean isAuthorizationResponseError(MultiValueMap<String, String> request) {

        return StringUtils.hasText(request.getFirst(OAuth2ParameterNames.ERROR)) && StringUtils.hasText(request.getFirst(OAuth2ParameterNames.STATE));
    }

    OAuth2AuthorizationResponse convert(MultiValueMap<String, String> request, String redirectUri) {

        String code = request.getFirst(OAuth2ParameterNames.CODE);
        String errorCode = request.getFirst(OAuth2ParameterNames.ERROR);
        String state = request.getFirst(OAuth2ParameterNames.STATE);

        if (StringUtils.hasText(code)) {

            return OAuth2AuthorizationResponse.success(code).redirectUri(redirectUri).state(state).build();
        }

        String errorDescription = request.getFirst(OAuth2ParameterNames.ERROR_DESCRIPTION);

        String errorUri = request.getFirst(OAuth2ParameterNames.ERROR_URI);

        return OAuth2AuthorizationResponse.error(errorCode).redirectUri(redirectUri).errorDescription(errorDescription).errorUri(errorUri).state(state).build();
    }
}
