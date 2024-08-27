package org.esupportail.cas.util;

import org.esupportail.cas.config.CasAgimusCookieConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is {@link CasAgimusLogger}.
 *
 * @author Julien Marchal
 * @since 5.0.0
 */
public class CasAgimusLogger {
	private static final Logger LOGGER = LoggerFactory.getLogger(CasAgimusLogger.class);
   
    private CasAgimusCookieConfigurationProperties agimusConfigurationProperties;
    
	public CasAgimusLogger(CasAgimusCookieConfigurationProperties casAgimusConfigurationProperties) {
		LOGGER.debug("CasAgimusLogger::CasAgimusLogger : create bean CasAgimusLogger");    	
		this.agimusConfigurationProperties= casAgimusConfigurationProperties;
	}
	
	public void log(String username, String value) {
		LOGGER.info(value + "" + agimusConfigurationProperties.getTraceFileSeparator() + "" + username);
	}
}
