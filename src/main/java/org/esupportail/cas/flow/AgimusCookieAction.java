package org.esupportail.cas.flow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.util.HostNameBasedUniqueTicketIdGenerator;
import org.apereo.cas.web.flow.actions.AbstractNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.apereo.cas.web.support.WebUtils;
import org.esupportail.cas.config.CasAgimusConfigurationProperties;
import org.esupportail.cas.util.CasAgimusLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.webflow.execution.RequestContext;
/**
 * This is {@link AgimusCookieAction} that extracts basic authN credentials from the request.
 *
 * @author Julien Marchal
 * @since 4.2.0
 */
public class AgimusCookieAction extends AbstractNonInteractiveCredentialsAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgimusCookieAction.class);
    
    private CasAgimusConfigurationProperties agimusConfigurationProperties;
    
    @Autowired
    @Qualifier("agimusCookieGenerator")
    private CookieRetrievingCookieGenerator agimusCookieGenerator;

    @Autowired
    @Qualifier("agimusTokenTicketIdGenerator")
    private HostNameBasedUniqueTicketIdGenerator agimusTokenTicketIdGenerator;

    @Autowired
    @Qualifier("agimusLogger")
    private CasAgimusLogger agimusLogger;   
    
    public AgimusCookieAction(final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
                                     final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
                                     final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy, CasAgimusConfigurationProperties casAgimusConfigurationProperties) {    	
    	super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
    	this.agimusConfigurationProperties = casAgimusConfigurationProperties;
    	LOGGER.debug("AgimusCookieAction::AgimusCookieAction : create bean AgimusCookieAction");
    }

    @Override
    protected Credential constructCredentialsFromRequest(final RequestContext requestContext) {    	
    	try {
        	final HttpServletResponse response = WebUtils.getHttpServletResponseFromExternalWebflowContext(requestContext);
            final HttpServletRequest request = WebUtils.getHttpServletRequestFromExternalWebflowContext(requestContext);
        	
            String hasAlreadyCookie = agimusCookieGenerator.retrieveCookieValue(request);
            if(null == hasAlreadyCookie || "".equals(hasAlreadyCookie)) {
            	LOGGER.debug("AgimusCookieAction::constructCredentialsFromRequest : User have no Agimus cookie, we going to put new");
                
            	String agimusIdValue = agimusTokenTicketIdGenerator.getNewTicketId(agimusConfigurationProperties.getCookieValuePrefix());          	
                agimusCookieGenerator.addCookie(response, agimusIdValue);
                            
                final Authentication authentication = WebUtils.getAuthentication(requestContext);
                if (authentication == null) {            	
                	LOGGER.error("AgimusCookieAction::constructCredentialsFromRequest authentication is NULL, strange");
                }
                else {
                	LOGGER.debug("AgimusCookieAction::constructCredentialsFromRequest : Put Agimus cookie for ["+ authentication.getPrincipal() +"] ["+agimusIdValue+"]");
                	agimusLogger.log(authentication.getPrincipal().getId(), agimusIdValue);
                }
            }
            else {
            	LOGGER.debug("AgimusCookieAction::constructCredentialsFromRequest : user have already cookie, value ["+ hasAlreadyCookie +"]");
            }
        } catch (final Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }
}
