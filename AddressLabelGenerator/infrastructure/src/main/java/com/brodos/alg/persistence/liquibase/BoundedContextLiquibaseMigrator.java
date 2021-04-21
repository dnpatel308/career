/*
 * Copyright (C) Brodos AG - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.brodos.alg.persistence.liquibase;

import com.brodos.commons.liquibase.DataSourceLiquibaseMigrator;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Component;

/**
 *
 * @author Alexander Sahler <alexander.sahler at brodos.de>
 */
@Component(property="name=AddressLabelGenerator", immediate = true)
public class BoundedContextLiquibaseMigrator implements PreHook {

    @Override
    public void prepare(DataSource ds) throws SQLException {
        DataSourceLiquibaseMigrator mig = new DataSourceLiquibaseMigrator(ds, "com/brodos/alg/persistence/liquibase/changesets.xml");
        mig.prepare();
    }

}
