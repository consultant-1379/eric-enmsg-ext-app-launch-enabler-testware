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

package com.ericsson.oss.testware.extapplaunchenabler.flows;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.AVAILABLE_USERS;
import static com.ericsson.oss.testware.extapplaunchenabler.teststeps.EaleTestSteps.*;
import static com.ericsson.oss.testware.security.authentication.steps.LoginLogoutRestTestSteps.TEST_STEP_IS_USER_LOGGED_IN;
import static com.ericsson.oss.testware.security.authentication.steps.LoginLogoutRestTestSteps.TEST_STEP_LOGIN;
import static com.ericsson.oss.testware.security.authentication.steps.LoginLogoutRestTestSteps.TEST_STEP_LOGOUT;

import javax.inject.Inject;

import com.ericsson.oss.testware.extapplaunchenabler.UserPredicates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.oss.testware.extapplaunchenabler.teststeps.EaleTestSteps;

import com.ericsson.oss.testware.security.authentication.steps.LoginLogoutRestTestSteps;

public class EaleTestFlows {
    private static final Logger logger = LoggerFactory.getLogger(EaleTestFlows.class);
    private static final String CONFIG_FLOW_CALLED = "\t\t{}::CONFIG FLOW CALLED\n\n";
    @Inject
    private UserPredicates userPredicates;
    @Inject
    private EaleTestSteps ealeConfigTestSteps;
    @Inject
    private LoginLogoutRestTestSteps loginLogoutRestTestSteps;

    /**
     * Lists the AVAILABLE_USERS data source content.
     */
    public TestStepFlow checkAvailableUsersFlow() {
        logger.info(CONFIG_FLOW_CALLED, "checkAvailableUsersFlow()");
        return flow("Check the available users list - CONFIG FLOW")
                .addTestStep(annotatedMethod(ealeConfigTestSteps, LIST_AVAILABLE_USERS_TEST_STEP))
                .withDataSources(dataSource(AVAILABLE_USERS).allowEmpty())
                .build();
    }


    /**
     * Configure the launch link and set the host to a random string.
     * This is so we can verify that the launch link configuration has been updated later.
     */
    public TestStepFlow configureLaunchLinkFlow() {
        logger.info("\t\tconfigureLaunchLinkFlow()::FLOW CALLED\n\n");
        return flow("configureLaunchLinkFlow - FLOW")
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_LOGIN))
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_IS_USER_LOGGED_IN))
                .addSubFlow(configureLaunchLinkSubFlow())
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_LOGOUT))
                .withDataSources(dataSource(AVAILABLE_USERS).withFilter(userPredicates.ealeAdminUser())).build();
    }

    private TestStepFlow configureLaunchLinkSubFlow() {
        logger.info("\t\tconfigureLaunchLinkFlowSubFlow()::SUB FLOW CALLED\n\n");
        return flow("configureLaunchLinkFlowSubFlow - SUB FLOW")
                .addTestStep(annotatedMethod(ealeConfigTestSteps, CONFIGURE_LAUNCH_LINK_TEST_STEP))
                .build();
    }

    /**
     * Verify that the launch link configuration has been updated, with the host set to the random string previously configured.
     * This verification is repeated for each of the BO roles.
     */
    public TestStepFlow verifyLaunchLinkAsBoAdminFlow() {
        logger.info("\t\tverifyLaunchLinkAsBoAdminFlow()::FLOW CALLED\n\n");
        return flow("verifyLaunchLinkAsBoAdminFlow - FLOW")
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_LOGIN))
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_IS_USER_LOGGED_IN))
                .addSubFlow(verifyLaunchLinkSubFlow())
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_LOGOUT))
                .withDataSources(dataSource(AVAILABLE_USERS).withFilter(userPredicates.boAdminUser())).build();
    }

    public TestStepFlow verifyLaunchLinkAsBoReportOperatorFlow() {
        logger.info("\t\tverifyLaunchLinkAsBoReportOperatorFlow()::FLOW CALLED\n\n");
        return flow("verifyLaunchLinkAsBoReportOperatorFlow - FLOW")
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_LOGIN))
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_IS_USER_LOGGED_IN))
                .addSubFlow(verifyLaunchLinkSubFlow())
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_LOGOUT))
                .withDataSources(dataSource(AVAILABLE_USERS).withFilter(userPredicates.boReportOperatorUser())).build();
    }

    public TestStepFlow verifyLaunchLinkAsBoUniverseOperatorFlow() {
        logger.info("\t\tverifyLaunchLinkAsBoUniverseOperatorFlow()::FLOW CALLED\n\n");
        return flow("verifyLaunchLinkAsBoUniverseOperatorFlow - FLOW")
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_LOGIN))
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_IS_USER_LOGGED_IN))
                .addSubFlow(verifyLaunchLinkSubFlow())
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_LOGOUT))
                .withDataSources(dataSource(AVAILABLE_USERS).withFilter(userPredicates.boUniverseOperatorUser())).build();
    }

    private TestStepFlow verifyLaunchLinkSubFlow() {
        logger.info("\t\tverifyLaunchLinkSubFlow()::SUB FLOW CALLED\n\n");
        return flow("verifyLaunchLinkFlowSubFlow - SUB FLOW")
                .addTestStep(annotatedMethod(ealeConfigTestSteps, VERIFY_LAUNCH_LINK))
                .build();
    }

    /**
     * Restore the launch link configuration to how it was previously configured.
     * If it was not previously configured then the host will be configured as "notConfigured".
     */
    public TestStepFlow restoreLaunchLinkFlow() {
        logger.info("\t\trestoreLaunchLinkFlow()::FLOW CALLED\n\n");
        return flow("restoreLaunchLinkFlow - FLOW")
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_LOGIN))
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_IS_USER_LOGGED_IN))
                .addSubFlow(restoreLaunchLinkSubFlow())
                .addTestStep(annotatedMethod(loginLogoutRestTestSteps, TEST_STEP_LOGOUT))
                .withDataSources(dataSource(AVAILABLE_USERS).withFilter(userPredicates.ealeAdminUser())).build();
    }

    private TestStepFlow restoreLaunchLinkSubFlow() {
        logger.info("\t\trestoreLaunchLinkSubFlow()::SUB FLOW CALLED\n\n");
        return flow("restoreLaunchLinkSubFlow - SUB FLOW")
                .addTestStep(annotatedMethod(ealeConfigTestSteps, RESTORE_LAUNCH_LINK))
                .build();
    }

}
