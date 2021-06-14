package org.appsugar.archetypes.repository.jpa;

import org.appsugar.archetypes.BaseTest;
import org.appsugar.archetypes.repository.DbImportConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(DbImportConfiguration.class)
public abstract class BaseJpaRepositoryTest extends BaseTest {

}
