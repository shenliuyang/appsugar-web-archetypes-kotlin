package org.appsugar.archetypes.repository.kjdbc;

import org.appsugar.archetypes.BaseTest;
import org.appsugar.archetypes.repository.DbImportConfiguration;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DbImportConfiguration.class)
public abstract class BaseJdbcRepositoryTest extends BaseTest {
}
