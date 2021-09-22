package com.imooc.uaa.service.admin;

import com.imooc.uaa.annotation.ReloadRoleHierarchy;
import com.imooc.uaa.annotation.RoleAdmin;
import com.imooc.uaa.annotation.RoleAdminOrRead;
import com.imooc.uaa.domain.Permission;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.dto.CreateOrUpdateRoleDto;
import com.imooc.uaa.exception.DataConflictProblem;
import com.imooc.uaa.exception.DuplicateProblem;
import com.imooc.uaa.repository.PermissionRepo;
import com.imooc.uaa.repository.RoleRepo;
import com.imooc.uaa.service.validation.RoleValidationService;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RoleAdminService {

    private final RoleRepo roleRepo;
    private final PermissionRepo permissionRepo;
    private final RoleValidationService roleValidationService;

    public Page<Role> findAll(Predicate predicate, Pageable pageable) {
        return roleRepo.findAll(predicate, pageable);
    }

    @ReloadRoleHierarchy
    @RoleAdmin
    @Transactional
    public Role createRole(final CreateOrUpdateRoleDto createOrUpdateRoleDto) {
        if (roleValidationService.isRoleNameExisted(createOrUpdateRoleDto.getRoleName())) {
            throw new DuplicateProblem("角色名称已存在，请修改后再试");
        }
        return roleRepo.save(Role.builder()
            .roleName(createOrUpdateRoleDto.getRoleName().toUpperCase())
            .displayName(createOrUpdateRoleDto.getDisplayName())
            .build());
    }

    @ReloadRoleHierarchy
    @RoleAdmin
    @Transactional
    public Role updateRole(Long id, CreateOrUpdateRoleDto createOrUpdateRoleDto) {
        if (roleValidationService.isRoleNameExistedAndIdIsNot(createOrUpdateRoleDto.getRoleName(), id)) {
            throw new DuplicateProblem("角色名称已存在，请修改后再试");
        }
        return roleRepo.findById(id)
            .map(role -> roleRepo.save(role
                .withRoleName(createOrUpdateRoleDto.getRoleName().toUpperCase())
                .withDisplayName(createOrUpdateRoleDto.getDisplayName())
                .withPermissions(new HashSet<>())
            ))
            .orElseThrow();
    }

    @ReloadRoleHierarchy
    @RoleAdmin
    @Transactional
    public void deleteRole(Long id) {
        if (roleValidationService.isRoleAssigned(id)) {
            throw new DataConflictProblem("该角色已经分配，请先从所有用户移除该角色");
        }
        roleRepo.findById(id).ifPresent(role -> {
            if (role.isBuiltIn()) {
                throw new DataConflictProblem("该角色为内置角色，不能删除！");
            }
            roleRepo.deleteById(id);
        });
    }

    @ReloadRoleHierarchy
    @RoleAdmin
    @Transactional
    public Role updatePermissions(Long id, List<Long> permissionIds) {
        return roleRepo.findById(id)
            .map(role -> {
                val permissionsFiltered = permissionRepo.findByIdIn(new HashSet<>(permissionIds));
                return roleRepo.save(role.withPermissions(permissionsFiltered));
            })
            .orElseThrow();
    }

    @RoleAdminOrRead
    public Optional<Role> findById(final Long roleId) {
        return roleRepo.findById(roleId);
    }

    @RoleAdminOrRead
    public Set<Permission> findAvailablePermissions(final Long roleId) {
        return roleRepo.findById(roleId)
            .map(role -> {
                val assignedPermissions = role.getPermissions().stream().map(Permission::getAuthority).collect(Collectors.toSet());
                return permissionRepo.findAll().stream().filter(permission -> !assignedPermissions.contains(permission.getAuthority())).collect(Collectors.toSet());
            })
            .orElseThrow();
    }
}
