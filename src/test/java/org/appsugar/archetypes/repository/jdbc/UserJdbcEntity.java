package org.appsugar.archetypes.repository.jdbc;

import lombok.ToString;
import org.appsugar.archetypes.domain.User;
import org.springframework.data.relational.core.mapping.Table;

/**
 * for jdbc test entity
 */
@Table(User.TABLE_NAME)
@ToString(callSuper = true)
public class UserJdbcEntity extends User {
}
