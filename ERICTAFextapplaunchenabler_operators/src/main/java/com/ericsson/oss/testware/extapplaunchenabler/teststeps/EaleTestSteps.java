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

package com.ericsson.oss.testware.extapplaunchenabler.teststeps;

import static com.ericsson.oss.testware.extapplaunchenabler.utils.EaleConstants.APPLICATION_JSON;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.AVAILABLE_USERS;
import static com.google.common.truth.Truth.assertThat;
import static java.text.MessageFormat.format;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.xpath.operations.Bool;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.constants.HttpStatus;
import com.ericsson.oss.testware.enmbase.data.ENMUser;
import com.ericsson.oss.testware.extapplaunchenabler.operators.EaleRestOperator;
import com.ericsson.oss.testware.security.authentication.tool.TafToolProvider;

public class EaleTestSteps {
    // Test Step IDs.
    public static final String LIST_AVAILABLE_USERS_TEST_STEP = "ListAvailableUsersTestStep";
    public static final String CONFIGURE_LAUNCH_LINK = "/eale/v1/systems/ENIQ-S/applications/bo";
    public static final String APP_LAUNCH_LINK = "/rest/apps/web/bilaunchpad";
    public static final String VERIFY_LAUNCH_LINK = "verifyLaunchLinkTestStep";
    public static final String CONFIGURE_LAUNCH_LINK_TEST_STEP = "configureLaunchLinkTestStep";
    public static final String GET_CURRENT_LAUNCH_LINK = "getCurrentLaunchLinkTestStep";
    public static final String RESTORE_LAUNCH_LINK = "revertLaunchLinkTestStep";

    // Logging.
    private static final Logger logger = LoggerFactory.getLogger(EaleTestSteps.class);
    private static final String LOG_START_TEST_STEP = "\t\tSTART TEST STEP: {0} \n\n";
    private static final String LOG_END_TEST_STEP = "END TEST STEP: {0} \n\n";
    private static final String LOG_REST_CALL = "\t\tSending REST Call. . . . .\n\n================================================\n\n";
    private static final String UTF_8 = "UTF-8";
    
    @Inject
    private TafToolProvider tafToolProvider;
    
    @Inject
    private Provider<EaleRestOperator> ealeRestOperatorProvider;

    private EaleRestOperator operator;

    private String randomHost = RandomStringUtils.randomAlphabetic(10);
    private String previousHost = null;
    private String port = "8443";
    private String protocol = "https";

    @TestStep(id = LIST_AVAILABLE_USERS_TEST_STEP)
    public void listAvailableUsersTestStep(@Input(AVAILABLE_USERS) final ENMUser user) {
        logger.debug("");
        logger.debug("AVAILABLE USER: " + user.getUsername());
        logger.debug("");
    }
    private boolean getCurrentConfig(){
        boolean launchLinkPreviouslyConfigured;

        final HttpResponse getResponse = operator.performHttpGet(CONFIGURE_LAUNCH_LINK);
        String body = getResponse.getBody();
        JSONObject bodyJson = new JSONObject(body);
        logger.info("{} GET current link response code: {}", GET_CURRENT_LAUNCH_LINK, getResponse.getResponseCode().getCode());

        if (getResponse.getResponseCode().getCode() == 404){
            previousHost = "notConfigured";
            launchLinkPreviouslyConfigured=false;
        }
        else {
            previousHost = bodyJson.get("host").toString();
            launchLinkPreviouslyConfigured=true;
        }
        logger.info("{} link was previously configured: {} ",GET_CURRENT_LAUNCH_LINK,launchLinkPreviouslyConfigured);
        logger.info("{} current link host: {}", GET_CURRENT_LAUNCH_LINK, previousHost);
        return launchLinkPreviouslyConfigured;
    }

    @TestStep(id = CONFIGURE_LAUNCH_LINK_TEST_STEP)
    public void configureLaunchLinkTestStep() throws UnsupportedEncodingException {
        logger.info(format(LOG_START_TEST_STEP, CONFIGURE_LAUNCH_LINK_TEST_STEP));
        try {
            initRestOperators();
            boolean linkWasConfigured=getCurrentConfig();
            HttpPut httpPut = new HttpPut(CONFIGURE_LAUNCH_LINK);
            JSONObject json = new JSONObject(); 
            json.put("host", randomHost);
            json.put("port", port);
            json.put("protocol", protocol);
            StringEntity stringEntity = new StringEntity(json.toString());
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            httpPut.setEntity(stringEntity);
            logHttpRequest(httpPut);
            final HttpResponse putResponse = operator.performHttpPut(httpPut);
            logger.info("configureLaunchLinkTestStep random host " + randomHost);
            logger.info("configureLaunchLinkTestStep HttpResponse " + putResponse.getResponseCode());
            logger.info("configureLaunchLinkTestStep link was previously configured: {} ",linkWasConfigured);

            if(linkWasConfigured){
                assertThat(SC_OK).isEqualTo(putResponse.getResponseCode().getCode());
            }
            else{
                assertThat(SC_CREATED).isEqualTo(putResponse.getResponseCode().getCode());
            }
        } catch (Exception e) {
            logger.error(CONFIGURE_LAUNCH_LINK_TEST_STEP, e);
        }
        logger.info(format(LOG_END_TEST_STEP, CONFIGURE_LAUNCH_LINK_TEST_STEP));
    }

    @TestStep(id = VERIFY_LAUNCH_LINK)
    public void verifyLaunchLinkTestStep() {
        logger.info(format(LOG_START_TEST_STEP, VERIFY_LAUNCH_LINK));
        try{
            initRestOperators();
            operator.performHttpGet(APP_LAUNCH_LINK);
            logger.info("{} - Hostname expected (Random 10 character string): {}", VERIFY_LAUNCH_LINK, randomHost);
        } catch (Exception e) {
            assertThat(e.getCause().toString()).contains(randomHost.toLowerCase());
        }
        logger.info(format(LOG_END_TEST_STEP, VERIFY_LAUNCH_LINK));
    }

    @TestStep(id = RESTORE_LAUNCH_LINK)
    public void restoreLaunchLinkTestStep() {
        logger.info(format(LOG_START_TEST_STEP, RESTORE_LAUNCH_LINK));

        try {
            initRestOperators();
            HttpPut httpPut = new HttpPut(CONFIGURE_LAUNCH_LINK);
            JSONObject json = new JSONObject();
            json.put("host", previousHost);
            json.put("port", port);
            json.put("protocol", protocol);
            StringEntity stringEntity = new StringEntity(json.toString());
            stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            httpPut.setEntity(stringEntity);
            logHttpRequest(httpPut);
            final HttpResponse response = operator.performHttpPut(httpPut);
            logger.info("restoreLaunchLinkTestStep HttpResponse " + response.getResponseCode());
            response.getResponseCode().compareTo(HttpStatus.OK);
            assertThat(SC_OK).isEqualTo(response.getResponseCode().getCode());
        } catch (Exception e) {
            logger.error(RESTORE_LAUNCH_LINK, e);
        }
        logger.info(format(LOG_END_TEST_STEP, RESTORE_LAUNCH_LINK));
    }

    private void initRestOperators() {
        operator = ealeRestOperatorProvider.get();
        operator.setHttpTool(getHttpTool());
    }

    private HttpTool getHttpTool() {
        return tafToolProvider.getHttpTool();
    }

    private void logHttpRequest(final HttpRequestBase httpReq) {
        try {
            logger.info(LOG_REST_CALL, URLDecoder.decode(httpReq.getRequestLine().getUri(), UTF_8));
        } catch (final UnsupportedEncodingException e) {
            logger.error("Error decoding URI", e);
        }
    }
}
