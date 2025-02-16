package org.apereo.cas.authentication.principal;

import org.apereo.cas.CasProtocolConstants;
import org.apereo.cas.services.CasModelRegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.HttpRequestUtils;
import org.apereo.cas.util.function.FunctionUtils;
import org.apereo.cas.web.UrlValidator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract response builder that provides wrappers for building
 * post and redirect responses.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractWebApplicationServiceResponseBuilder implements ResponseBuilder<WebApplicationService> {
    @Serial
    private static final long serialVersionUID = -4584738964007702423L;

    /**
     * Services manager instance.
     */
    protected final ServicesManager servicesManager;

    private final UrlValidator urlValidator;

    private int order;

    /**
     * Build redirect.
     *
     * @param service    the service
     * @param parameters the parameters
     * @return the response
     */
    protected Response buildRedirect(final WebApplicationService service, final Map<String, String> parameters) {
        return DefaultResponse.getRedirectResponse(determineServiceResponseUrl(service), parameters);
    }

    /**
     * Determine service response url and provide url.
     *
     * @param service the service
     * @return the string
     */
    protected String determineServiceResponseUrl(final WebApplicationService service) {
        val registeredService = this.servicesManager.findServiceBy(service);
        if (registeredService instanceof final CasModelRegisteredService casService) {
            if (StringUtils.isNotBlank(casService.getRedirectUrl())
                && getUrlValidator().isValid(casService.getRedirectUrl())) {
                return casService.getRedirectUrl();
            }
        }
        return service.getOriginalUrl();
    }

    /**
     * Build header response.
     *
     * @param service    the service
     * @param parameters the parameters
     * @return the response
     */
    protected Response buildHeader(final WebApplicationService service, final Map<String, String> parameters) {
        return DefaultResponse.getHeaderResponse(determineServiceResponseUrl(service), parameters);
    }

    /**
     * Build post.
     *
     * @param service    the service
     * @param parameters the parameters
     * @return the response
     */
    protected Response buildPost(final WebApplicationService service, final Map<String, String> parameters) {
        return DefaultResponse.getPostResponse(determineServiceResponseUrl(service), parameters);
    }

    protected Response.ResponseType getWebApplicationServiceResponseType(final WebApplicationService finalService) {
        val request = HttpRequestUtils.getHttpServletRequestFromRequestAttributes();
        val methodRequest = Optional.ofNullable(request)
            .map(httpServletRequest -> httpServletRequest.getParameter(CasProtocolConstants.PARAMETER_METHOD))
            .orElse(null);
        val func = FunctionUtils.doIf(StringUtils::isBlank,
            __ -> {
                val registeredService = servicesManager.findServiceBy(finalService);
                return registeredService instanceof final CasModelRegisteredService casService ? casService.getResponseType() : null;
            },
            __ -> methodRequest);
        val method = func.apply(methodRequest);
        if (StringUtils.isBlank(method) || !EnumUtils.isValidEnum(Response.ResponseType.class, method.toUpperCase(Locale.ENGLISH))) {
            return Response.ResponseType.REDIRECT;
        }
        return Response.ResponseType.valueOf(method.toUpperCase(Locale.ENGLISH));
    }
}
