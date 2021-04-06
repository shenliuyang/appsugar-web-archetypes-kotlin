package org.appsugar.archetypes.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.domain
 * @className Role
 * @date 2021-03-29  22:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = Role.TABLE_NAME)
@DynamicUpdate
public class Role {
    public static final String TABLE_NAME = "APPSUGAR_ROLE";
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    private Long id;
    private String name;
}
