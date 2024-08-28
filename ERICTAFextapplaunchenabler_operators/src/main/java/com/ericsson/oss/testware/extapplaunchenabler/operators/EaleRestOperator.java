package com.ericsson.oss.testware.extapplaunchenabler.operators;

/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

import static com.ericsson.cifwk.taf.tools.http.constants.ContentType.APPLICATION_JSON;
import static com.ericsson.oss.testware.extapplaunchenabler.utils.EaleConstants.ACCEPT;

import java.io.IOException;

import org.apache.http.client.methods.HttpPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;

/**
 * Operator for performing RESTful Http Requests
 */
public class EaleRestOperator {
    private static final Logger logger = LoggerFactory.getLogger(EaleRestOperator.class);


    private HttpTool httpTool;

    /**
     * Sets the {@link HttpTool} to get the user-session (Logged in).
     *
     * @param hTool - http session
     */
    public void setHttpTool(final HttpTool hTool) {
        httpTool = hTool;
    }

    public HttpResponse performHttpPut(final HttpPut httpPut) {
        final String putRequest = httpPut.getRequestLine().getUri().toString();
        logger.info("\t\tSending RESTful Put: [{}]\n\n", putRequest);
        HttpResponse response = null;
        try {
            response = httpTool.request().header(ACCEPT, APPLICATION_JSON).contentType(APPLICATION_JSON).body(httpPut.getEntity().getContent())
                    .put(httpPut.getURI().toString());
        } catch (final UnsupportedOperationException | IOException e) {
            logger.error("Exception hit: ", e);
        }
        return response;
    }

    public HttpResponse performHttpGet(final String link) {
        logger.info("\t\tSending RESTful Get to: {}\n\n", link);
        HttpResponse response = null;
        try {
            response = httpTool.request().header(ACCEPT, APPLICATION_JSON).contentType(APPLICATION_JSON).get(link);
        } catch (final Exception e) {
            return response;
        }
        return response;
    }
}

