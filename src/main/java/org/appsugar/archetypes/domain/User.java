package org.appsugar.archetypes.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(name = User.TABLE_NAME)
@FieldNameConstants
public class User implements Serializable {
    public static final String TABLE_NAME = "APPSUGAR_USER";
    @Id
    @org.springframework.data.annotation.Id
    @GenericGenerator(name = "snowflake", strategy = "org.appsugar.archetypes.hibernate.SnowflakeIdGenerator")
    @GeneratedValue(generator = "snowflake")
    //@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    private Long id;
    private String name;
    private String loginName;
    private String address;
    private String email;
    private Integer age;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private Role role;
}
