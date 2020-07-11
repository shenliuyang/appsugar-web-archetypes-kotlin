package org.appsugar.archetypes.repository;

import org.appsugar.archetypes.BaseTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(FlywayMigrationConfiguration.class)
public abstract class BaseRepositoryTest extends BaseTest {
}
