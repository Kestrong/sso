package com.xjbg.sso.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * @author kesc
 * @since 2019/5/21
 */
@Slf4j
public class CommonUtil {
    private static HttpClient httpClient = HttpClientBuilder.create().build();

    /**
     * Constructs a service url from the HttpServletRequest or from the given
     * serviceUrl. Prefers the serviceUrl provided if both a serviceUrl and a
     * serviceName.
     *
     * @param request               the HttpServletRequest
     * @param response              the HttpServletResponse
     * @param service               the configured service url (this will be used if not null)
     * @param serverNames           the server name to  use to construct the service url if the service param is empty.
     *                              Note,  it can be a space-separated value.  We keep it as a single value, but will convert it to an array internally to get the matching value. This keeps backward compatability with anything using this public
     *                              method.
     * @param serviceParameterName  the service parameter name to remove (i.e. service)
     * @param artifactParameterName the artifact parameter name to remove (i.e. ticket)
     * @param encode                whether to encode the url or not (i.e. Jsession).
     * @return the service url to use.
     */
    public static String constructServiceUrl(final HttpServletRequest request, final HttpServletResponse response,
                                             final String service, final String serverNames, final String serviceParameterName,
                                             final String artifactParameterName, final boolean encode) {
        if (StringUtil.isNotBlank(service)) {
            return encode ? StringUtil.substringBefore(response.encodeURL(service), ";") : service;
        }

        final String serverName = findMatchingServerName(request, serverNames);
        final URIBuilder originalRequestUrl = new URIBuilder(request.getRequestURL().toString(), encode);
        originalRequestUrl.setParameters(request.getQueryString());

        final URIBuilder builder;
        if (!serverName.startsWith("https://") && !serverName.startsWith("http://")) {
            String scheme = request.isSecure() ? "https://" : "http://";
            builder = new URIBuilder(scheme + serverName, encode);
        } else {
            builder = new URIBuilder(serverName, encode);
        }

        if (builder.getPort() == -1 && !requestIsOnStandardPort(request)) {
            builder.setPort(request.getServerPort());
        }

        builder.setEncodedPath(builder.getEncodedPath() + request.getRequestURI());

        final List<String> serviceParameterNames = Arrays.asList(serviceParameterName.split(","));
        if (!serviceParameterNames.isEmpty() && !originalRequestUrl.getQueryParams().isEmpty()) {
            for (final URIBuilder.BasicNameValuePair pair : originalRequestUrl.getQueryParams()) {
                String name = pair.getName();
                if (!name.equals(artifactParameterName) && !serviceParameterNames.contains(name)) {
                    if (name.contains("&") || name.contains("=")) {
                        URIBuilder encodedParamBuilder = new URIBuilder();
                        encodedParamBuilder.setParameters(name);
                        for (final URIBuilder.BasicNameValuePair pair2 : encodedParamBuilder.getQueryParams()) {
                            String name2 = pair2.getName();
                            if (!name2.equals(artifactParameterName) && !serviceParameterNames.contains(name2)) {
                                builder.addParameter(name2, pair2.getValue());
                            }
                        }
                    } else {
                        builder.addParameter(name, pair.getValue());
                    }
                }
            }
        }

        final String result = builder.toString();
        final String returnValue = encode ? response.encodeURL(result) : result;
        log.debug("serviceUrl generated: {}", returnValue);
        return returnValue;
    }

    private static boolean requestIsOnStandardPort(final HttpServletRequest request) {
        final int serverPort = request.getServerPort();
        return serverPort == 80 || serverPort == 443;
    }

    protected static String findMatchingServerName(final HttpServletRequest request, final String serverName) {
        final String[] serverNames = serverName.split(" ");

        if (serverNames.length == 0 || serverNames.length == 1) {
            return serverName;
        }

        final String host = request.getHeader("Host");
        final String xHost = request.getHeader("X-Forwarded-Host");

        final String comparisonHost;
        if (xHost != null && "localhost".equals(host)) {
            comparisonHost = xHost;
        } else {
            comparisonHost = host;
        }

        if (comparisonHost == null) {
            return serverName;
        }

        for (final String server : serverNames) {
            final String lowerCaseServer = server.toLowerCase();

            if (lowerCaseServer.contains(comparisonHost)) {
                return server;
            }
        }

        return serverNames[0];
    }

    /**
     * Sends the redirect message and captures the exceptions that we can't possibly do anything with.
     *
     * @param response the HttpServletResponse.  CANNOT be NULL.
     * @param url      the url to redirect to.
     */
    public static void sendRedirect(final HttpServletResponse response, final String url) {
        try {
            response.sendRedirect(url);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * Constructs the URL with ticket to use to redirect to the CAS client.
     *
     * @param serviceUrl
     * @param ticket
     * @return
     */
    public static String constructTicketUrl(final String serviceUrl, final String ticketParamName, final String ticket) {
        return serviceUrl + (serviceUrl.contains("?") ? "&" : "?") + (ticketParamName + "="
                + ticket);
    }

    /**
     * Url encode a value using UTF-8 encoding.
     *
     * @param value the value to encode.
     * @return the encoded value.
     */
    public static String urlEncode(final String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Safe method for retrieving a parameter from the request without disrupting the reader UNLESS the parameter
     * actually exists in the query string.
     * <p>
     * Note, this does not work for POST Requests for "logoutRequest".  It works for all other CAS POST requests because the
     * parameter is ALWAYS in the GET request.
     * <p>
     * If we see the "logoutRequest" parameter we MUST treat it as if calling the standard request.getParameter.
     *
     * @param request   the request to check.
     * @param parameter the parameter to look for.
     * @return the value of the parameter.
     */
    public static String safeGetParameter(final HttpServletRequest request, final String parameter) {
        if ("POST".equals(request.getMethod())) {
            log.debug("safeGetParameter called on a POST HttpServletRequest for Restricted Parameters.  Cannot complete check safely.  Reverting to standard behavior for this Parameter");
            return request.getParameter(parameter);
        }
        return request.getQueryString() == null || !request.getQueryString().contains(parameter) ? null : request
                .getParameter(parameter);
    }

    /**
     * Constructs the URL to use to redirect to the CAS server.
     *
     * @param casServerLoginUrl    the CAS Server login url.
     * @param serviceParameterName the name of the parameter that defines the service.
     * @param serviceUrl           the actual service's url.
     * @param renew                whether we should send renew or not.
     * @return the fully constructed redirect url.
     */
    public static String constructRedirectUrl(final String casServerLoginUrl, final String serviceParameterName,
                                              final String serviceUrl, final boolean renew) {
        return casServerLoginUrl + (casServerLoginUrl.contains("?") ? "&" : "?") + serviceParameterName + "="
                + urlEncode(serviceUrl) + (renew ? "&renew=true" : "");
    }

    public static void logoutRequest(String serviceUrl, String logoutRequestParam, String ticket) {
        log.debug("serviceUrl:{},logoutRequestParam:{},ticket:{}", serviceUrl, logoutRequestParam, ticket);
        HttpGet httpGet = new HttpGet(constructTicketUrl(serviceUrl, logoutRequestParam, ticket));
        try {
            String result = httpClient.execute(httpGet, new BasicResponseHandler());
            log.info("logoutRequest result from client", result);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
