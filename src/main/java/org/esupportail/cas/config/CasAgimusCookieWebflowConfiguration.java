package org.esupportail.cas.config;

import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.cookie.CookieProperties;
import org.apereo.cas.util.HostNameBasedUniqueTicketIdGenerator;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.CookieUtils;
import org.apereo.cas.web.support.gen.CookieRetrievingCookieGenerator;
import org.esupportail.cas.flow.AgimusCookieAction;
import org.esupportail.cas.flow.AgimusCookieWebflowConfigurer;
import org.esupportail.cas.util.CasAgimusLogger;
import org.esupportail.cas.web.AgimusCookieRetrievingCookieGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

/**
 * This is {@link CasAgimusCookieWebflowConfiguration}.
 *
 * @author Julien Marchal
 * @since 5.0.0
 */
@Configuration("casAgimusCookieWebflowConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasAgimusCookieWebflowConfiguration implements CasWebflowExecutionPlanConfigurer {
	private static final Logger LOGGER = LoggerFactory.getLogger(CasAgimusCookieWebflowConfiguration.class);
	
	private static final int WEBFLOW_CONFIGURER_ORDER = 1000;
	
	@Autowired
	@Qualifier("adaptiveAuthenticationPolicy")
	private ObjectProvider<AdaptiveAuthenticationPolicy> adaptiveAuthenticationPolicy;
	
	@Autowired
	@Qualifier("serviceTicketRequestWebflowEventResolver")
	private ObjectProvider<CasWebflowEventResolver> serviceTicketRequestWebflowEventResolver;
	
	@Autowired
	@Qualifier("initialAuthenticationAttemptWebflowEventResolver")
	private ObjectProvider<CasDelegatingWebflowEventResolver> initialAuthenticationAttemptWebflowEventResolver;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private CasConfigurationProperties casProperties;
    
    @Autowired
    private CasAgimusCookieConfigurationProperties casAgimusConfigurationProperties;
    
    @Autowired
    @Qualifier("loginFlowRegistry")
    private ObjectProvider<FlowDefinitionRegistry> loginFlowDefinitionRegistry;

    @Autowired
    private FlowBuilderServices flowBuilderServices;
     
    @Autowired
    @Qualifier("agimusTokenTicketIdGenerator")
    private HostNameBasedUniqueTicketIdGenerator agimusTokenTicketIdGenerator;
    
    @Bean
    @RefreshScope
    public HostNameBasedUniqueTicketIdGenerator agimusTokenTicketIdGenerator() {
        /*return new DefaultUniqueTicketIdGenerator();*/
    	return new HostNameBasedUniqueTicketIdGenerator(
    				casAgimusConfigurationProperties.getCookieValueMaxLength(),
    				casProperties.getServer().getName()
    			);
    }
    
   @Bean
   public CasAgimusLogger agimusLogger() {
      	LOGGER.debug("CasAgimusCookieWebflowConfiguration::CasAgimusLogger : Create bean agimusLogger");        
      	return new CasAgimusLogger(casAgimusConfigurationProperties);
   }

    
    @Bean
    public Action agimusCookieAction() {
    	LOGGER.debug("CasAgimusCookieWebflowConfiguration::agimusCookieAction : Create bean agimusCookieAction");    	
    
        return new AgimusCookieAction(initialAuthenticationAttemptWebflowEventResolver.getIfAvailable(), 
                serviceTicketRequestWebflowEventResolver.getIfAvailable(),
                adaptiveAuthenticationPolicy.getIfAvailable(), casAgimusConfigurationProperties);
    }
    
    @Bean
    @RefreshScope
    public CookieRetrievingCookieGenerator agimusCookieGenerator() {
    	LOGGER.debug("CasAgimusCookieWebflowConfiguration::agimusCookieGenerator : Create bean agimusCookieGenerator");
    	LOGGER.debug("CasAgimusCookieWebflowConfiguration::agimusCookieGenerator : Read Properties");
    	

    	LOGGER.debug("CasAgimusCookieWebflowConfiguration::agimusCookieGenerator : AgimusCookieName = ["+ casAgimusConfigurationProperties.getCookieName() +"]");                        
        LOGGER.debug("CasAgimusCookieWebflowConfiguration::agimusCookieGenerator : AgimusCookieMaxAge = ["+ casAgimusConfigurationProperties.getCookieMaxAge() +"]");
        LOGGER.debug("CasAgimusCookieWebflowConfiguration::agimusCookieGenerator : AgimusCookiePath = ["+ casAgimusConfigurationProperties.getCookiePath() +"]");
        LOGGER.debug("CasAgimusCookieWebflowConfiguration::agimusCookieGenerator : AgimusCookieDomain = ["+ casAgimusConfigurationProperties.getCookieDomain() +"]");
        LOGGER.debug("CasAgimusCookieWebflowConfiguration::agimusCookieGenerator : AgimusCookieValuePrefix = ["+ casAgimusConfigurationProperties.getCookieValuePrefix() +"]");
        LOGGER.debug("CasAgimusCookieWebflowConfiguration::agimusCookieGenerator : AgimusTraceFileSeparator = ["+ casAgimusConfigurationProperties.getTraceFileSeparator() +"]");
        
        CookieProperties cookieProperties = new CookieProperties();
    	cookieProperties.setName(casAgimusConfigurationProperties.getCookieName());
    	cookieProperties.setPath(casAgimusConfigurationProperties.getCookiePath());
    	cookieProperties.setMaxAge(casAgimusConfigurationProperties.getCookieMaxAge());
    	cookieProperties.setDomain(casAgimusConfigurationProperties.getCookieDomain());
    	cookieProperties.setHttpOnly(false);
    	cookieProperties.setSecure(false);
    	
    	return new AgimusCookieRetrievingCookieGenerator(CookieUtils.buildCookieGenerationContext(cookieProperties));
    }
    
    @ConditionalOnMissingBean(name = "agimusCookieWebflowConfigurer")
    @Bean
    @RefreshScope
    public CasWebflowConfigurer agimusCookieWebflowConfigurer() {
    	LOGGER.info("CasAgimusCookieWebflowConfiguration::agimusCookieWebflowConfigurer : configure Agimus WebFlow");    
    	AgimusCookieWebflowConfigurer w = new AgimusCookieWebflowConfigurer(flowBuilderServices,          
                loginFlowDefinitionRegistry.getIfAvailable(),
                applicationContext, casProperties);
    	w.setOrder(WEBFLOW_CONFIGURER_ORDER);
    	return w;
    }   
    
    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
    	plan.registerWebflowConfigurer(agimusCookieWebflowConfigurer());
    }

    
}
