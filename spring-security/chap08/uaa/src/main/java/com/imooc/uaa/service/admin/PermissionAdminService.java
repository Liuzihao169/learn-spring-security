package com.imooc.uaa.service.admin;

import com.imooc.uaa.annotation.RoleAdminOrRead;
import com.imooc.uaa.domain.Permission;
import com.imooc.uaa.repository.PermissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PermissionAdminService {

    private final PermissionRepo permissionRepo;

    @RoleAdminOrRead
    public List<Permission> findAll() {
        return permissionRepo.findAll();
    }
}
