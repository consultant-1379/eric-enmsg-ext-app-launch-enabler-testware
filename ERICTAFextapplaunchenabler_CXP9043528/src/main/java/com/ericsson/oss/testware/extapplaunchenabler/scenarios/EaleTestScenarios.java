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
import static com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler.PROPAGATE;
import static com.ericsson.oss.testware.extapplaunchenabler.utils.EaleConstants.RFA250;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.oss.testware.extapplaunchenabler.flows.EaleTestFlows;

public class EaleTestScenarios extends TafTestBase {
    private static final Logger logger = LoggerFactory.getLogger(EaleTestScenarios.class);

    @Inject
    private EaleTestFlows ealeFlows;

    @Test(groups = {RFA250})
    @TestId(id = "77653 Configure Launch Link with unique string", title = "Configure Launch Link with Unique String")
    public void configureLaunchLinkScenario() {
        logger.info("\t\tconfigureLaunchLinkScenario()::SCENARIO STARTED\n\n");
        final TestScenario scenario = scenario("Configure Launch Link - SCENARIO")
                .addFlow(ealeFlows.configureLaunchLinkFlow())
                .build();
                executeScenario(scenario, PROPAGATE);
        logger.info("\t\tconfigureLaunchLinkScenario()::FINISHED TEST SCENARIO\n\n");
    }

    @Test(groups = {RFA250})
    @TestId(id = "77653 Configure Launch Link with unique string", title = "Verify Redirect link contains unique string as user with BO Roles")
    public void verifyLaunchLinkScenario() {
        logger.info("\t\tverifyEaleLaunchLinkScenario()::SCENARIO STARTED\n\n");
        final TestScenario scenario = scenario("Verify Redirect link contains unique string - SCENARIO")
                .addFlow(ealeFlows.verifyLaunchLinkAsBoAdminFlow())
                .addFlow(ealeFlows.verifyLaunchLinkAsBoReportOperatorFlow())
                .addFlow(ealeFlows.verifyLaunchLinkAsBoUniverseOperatorFlow())
                .build();
        executeScenario(scenario, PROPAGATE);
        logger.info("\t\tverifyEaleLaunchLinkScenario()::FINISHED TEST SCENARIO\n\n");
    }

    private void executeScenario(final TestScenario scenario, final ScenarioExceptionHandler handler) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).withDefaultExceptionHandler(handler).build();
        runner.start(scenario);
    }
}
