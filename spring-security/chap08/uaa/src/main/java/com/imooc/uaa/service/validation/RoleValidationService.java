package com.imooc.uaa.service.validation;

import com.imooc.uaa.repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleValidationService {

    private final RoleRepo roleRepo;

    public boolean isRoleNameExisted(String roleName) {
        return roleRepo.countByRoleNameIgnoreCase(roleName) > 0;
    }

    public boolean isRoleNameExistedAndIdIsNot(String roleName, Long id) {
        return roleRepo.countByRoleNameIgnoreCaseAndIdNot(roleName, id) > 0;
    }

    public boolean isRoleAssigned(Long id) {
        return roleRepo.countByAssigned(id) > 0;
    }
}
