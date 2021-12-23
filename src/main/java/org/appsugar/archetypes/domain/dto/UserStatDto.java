package org.appsugar.archetypes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.appsugar.archetypes.domain.User;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.domain.dto
 * @className UserStatDto
 * @date 2021-12-09  14:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatDto {
    private User user;
    private Long roleCount;
}
