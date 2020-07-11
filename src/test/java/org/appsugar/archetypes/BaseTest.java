package org.appsugar.archetypes;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public abstract class BaseTest {
}
