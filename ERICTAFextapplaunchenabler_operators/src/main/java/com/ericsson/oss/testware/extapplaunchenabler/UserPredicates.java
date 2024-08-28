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

package com.ericsson.oss.testware.extapplaunchenabler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.google.common.base.Predicate;

/**
 * Contains users predicates for doing tests based on authorized users.
 */
public class UserPredicates {
    public static final String EALE_Administrator = "Eale_Administrator";
    public static final String BO_Administrator = "BO_Administrator";
    public static final String BO_Report_Operator = "BO_Report_Operator";
    public static final String BO_Universe_Operator = "BO_Universe_Operator";
    public static final String ROLES = "roles";
    public static final String USERNAME = "username";
    private static final Logger logger = LoggerFactory.getLogger(UserPredicates.class);

    /**
     * Returns true / false depending on whether the ROLE in Eale_Administrator.
     */
    public Predicate<DataRecord> ealeAdminUser() {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord data) {
                final String userStr = data.getFieldValue(USERNAME).toString();
                final String roleStr = data.getFieldValue(ROLES).toString();
                final boolean allowed = EALE_Administrator.equals(roleStr);
                logger.debug("\nOne nbi user '{}' with role '{}' is '{}'\n\n", userStr, roleStr, allowed);
                return allowed;
            }
        };
    }


    /**
     * Returns true / false depending on whether the ROLE in BO_Administrator.
     */
    public Predicate<DataRecord> boAdminUser() {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord data) {
                final String userStr = data.getFieldValue(USERNAME).toString();
                final String roleStr = data.getFieldValue(ROLES).toString();
                final boolean allowed = BO_Administrator.equals(roleStr);
                logger.debug("\nOne nbi user '{}' with role '{}' is '{}'\n\n", userStr, roleStr, allowed);
                return allowed;
            }
        };
    }

    public Predicate<DataRecord> boUniverseOperatorUser() {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord data) {
                final String userStr = data.getFieldValue(USERNAME).toString();
                final String roleStr = data.getFieldValue(ROLES).toString();
                final boolean allowed = BO_Universe_Operator.equals(roleStr);
                logger.debug("\nOne nbi user '{}' with role '{}' is '{}'\n\n", userStr, roleStr, allowed);
                return allowed;
            }
        };
    }

    public Predicate<DataRecord> boReportOperatorUser() {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord data) {
                final String userStr = data.getFieldValue(USERNAME).toString();
                final String roleStr = data.getFieldValue(ROLES).toString();
                final boolean allowed = BO_Report_Operator.equals(roleStr);
                logger.debug("\nOne nbi user '{}' with role '{}' is '{}'\n\n", userStr, roleStr, allowed);
                return allowed;
            }
        };
    }
}

