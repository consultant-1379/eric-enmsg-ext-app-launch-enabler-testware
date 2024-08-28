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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TafDataSources;
import com.ericsson.cifwk.taf.datasource.TestDataSource;

/**
 * Utility class for adding DataSouces to context.
 */
public final class DataSourceHelper {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceHelper.class);

    private DataSourceHelper() {}

    /**
     * Initializes and creates a shared datasource from a csv file.
     *
     * @param datasourceName
     *            Name of the datasource to add the csv
     *            data to.
     * @param csvName
     *            Name of the csv file to use to populate the
     *            datasource.
     * @param dataType
     *            This is the type that the datasource will be initialised
     *            too. </i>
     */
    public static <T extends DataRecord> void initializeAndCreateSharedDataSourceFromCsv(
            final String datasourceName, final String csvName,
            final Class<T> dataType) {
        TafTestContext.getContext().dataSource(datasourceName, dataType);
        TestDataSource<DataRecord> datasource = TafDataSources.fromCsv(csvName);
        datasource = TafDataSources.shared(datasource);
        TafTestContext.getContext().addDataSource(datasourceName, datasource);
        logger.info(
                "Datasource '{}' with dataType '{}.class' has been populated using the csvFile '{}' .",
                datasourceName, dataType.getSimpleName(), csvName);
    }
}
