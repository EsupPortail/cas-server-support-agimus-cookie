package org.esupportail.cas.flow;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * This is {@link AgimusCookieWebflowConfigurer}.
 *
 * @author Julien Marchal
 * @since 4.2.0
 */
public class AgimusCookieWebflowConfigurer extends AbstractCasWebflowConfigurer {
	private static final Logger LOGGER = LoggerFactory.getLogger(AgimusCookieWebflowConfigurer.class);
	    
    public AgimusCookieWebflowConfigurer(final FlowBuilderServices flowBuilderServices, 
                                                final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                final ApplicationContext applicationContext,
                                                final CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
        LOGGER.debug("AgimusCookieWebflowConfigurer::AgimusCookieWebflowConfigurer : create bean AgimusCookieWebflowConfigurer");
    }

    @Override
    protected void doInitialize() {
    	LOGGER.debug("AgimusCookieWebflowConfigurer::doInitialize");
    	
    	final Flow flow = getLoginFlow();
        if (flow != null) {
        	LOGGER.debug("AgimusCookieWebflowConfigurer::doInitialize : Add agimusCookieAction in WebFlow");
        	
        	final ActionState actionState = getState(flow, CasWebflowConstants.STATE_ID_GENERATE_SERVICE_TICKET, ActionState.class);
        	actionState.getEntryActionList().add(createEvaluateAction("agimusCookieAction"));
        }
    }
    
}
