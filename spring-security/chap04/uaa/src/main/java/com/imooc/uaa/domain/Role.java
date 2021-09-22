package com.imooc.uaa.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 角色实体类，实现 GrantedAuthority 接口
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "mooc_roles")
public class Role implements GrantedAuthority, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增长 ID，唯一标识
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色名称，有唯一约束，不能重复
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String authority;
}
