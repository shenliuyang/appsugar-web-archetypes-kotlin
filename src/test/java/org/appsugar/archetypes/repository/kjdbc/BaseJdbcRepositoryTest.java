package org.appsugar.archetypes.repository.kjdbc;

import org.appsugar.archetypes.BaseTest;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseJdbcRepositoryTest extends BaseTest {
}
