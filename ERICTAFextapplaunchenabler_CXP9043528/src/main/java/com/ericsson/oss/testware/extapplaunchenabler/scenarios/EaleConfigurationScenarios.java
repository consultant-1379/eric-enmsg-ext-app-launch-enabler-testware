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

package com.ericsson.oss.testware.extapplaunchenabler.scenarios;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler.LOGONLY;
import static com.ericsson.oss.testware.security.gim.flows.GimCleanupFlows.EnmObjectType.USER;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.oss.testware.extapplaunchenabler.flows.EaleTestFlows;
import com.ericsson.oss.testware.enmbase.data.CommonDataSources;
import com.ericsson.oss.testware.enmbase.data.ENMUser;
import com.ericsson.oss.testware.security.gim.flows.GimCleanupFlows;
import com.ericsson.oss.testware.security.gim.flows.UserManagementTestFlows;

public class EaleConfigurationScenarios extends TafTestBase {
    private static final Logger logger = LoggerFactory.getLogger(EaleConfigurationScenarios.class);
    private static final String LOG_START_SCENARIO = "\t\t{}::EALE SCENARIO CALLED\n\n";
    private static final String LOG_END_TEST_SCENARIO = "\t\t{}::FINISHED EALE SCENARIO\n\n";
    private static final String CONFIG_USERS = "usersToCreate.csv";

    @Inject
    private EaleTestFlows ealeConfigFlows;
    @Inject
    private UserManagementTestFlows gimFlows;
    @Inject
    private GimCleanupFlows gimCleanupFlows;

    @BeforeSuite(alwaysRun = true)
    public void initSuite() {
        deleteExistingUsers();
        createUserScenario();
        listAvailableUsersScenario();
    }

    private void deleteExistingUsers() {
        DataSourceHelper.initializeAndCreateSharedDataSourceFromCsv(CommonDataSources.USER_TO_CLEAN_UP, CONFIG_USERS, ENMUser.class);
        deleteUsersScenario();
    }

    private void listAvailableUsersScenario() {
        logger.info(LOG_START_SCENARIO, "listAvailableUsersScenario()");
        final TestScenario scenario = scenario("Listing Available Users - SCENARIO").addFlow(ealeConfigFlows.checkAvailableUsersFlow()).build();
        executeScenario(scenario, LOGONLY);
        logger.info(LOG_END_TEST_SCENARIO, "listAvailableUsersScenario()");
    }

    private void createUserScenario() {
        logger.info(LOG_START_SCENARIO, "createUserScenario()");
        final TestScenario scenario = scenario("Creating ENM user - SCENARIO").addFlow(gimFlows.createUser()).build();
        executeScenario(scenario, LOGONLY);
        DataSourceHelper.initializeAndCreateSharedDataSourceFromCsv(CommonDataSources.USERS_TO_CREATE,
                CONFIG_USERS, ENMUser.class);
        logger.info(LOG_END_TEST_SCENARIO, "createUserScenario()");
    }

    private void restoreLaunchLinkScenario() {
        logger.info(LOG_START_SCENARIO, "restoreLaunchLinkScenario()");
        final TestScenario scenario = scenario("Restoring Launch Link Config - SCENARIO").addFlow(ealeConfigFlows.restoreLaunchLinkFlow()).build();
        executeScenario(scenario, LOGONLY);
        logger.info(LOG_END_TEST_SCENARIO, "restoreLaunchLinkScenario()");
    }

    @AfterSuite(alwaysRun = true)
    public void tearDown() {
        restoreLaunchLinkScenario();
        deleteUsersScenario();
    }

    private void deleteUsersScenario() {
        logger.info(LOG_START_SCENARIO, "deleteUsersScenario()");
        final TestScenario scenario = scenario("Deleting Setup Users - SCENARIO").addFlow(gimCleanupFlows.cleanUp(USER)).build();
        executeScenario(scenario, LOGONLY);
        logger.info(LOG_END_TEST_SCENARIO, "deleteUsersScenario()");
    }

    private void executeScenario(final TestScenario scenario, final ScenarioExceptionHandler handler) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).withDefaultExceptionHandler(handler).build();
        runner.start(scenario);
    }
}
