import ADMIN_AXIOS from "@/core/http-client/admin";

export default {
  loadUsers(size, page, offset, sort, filters) {
    return ADMIN_AXIOS.get("/users", {
      params: { size, page, offset, sort, ...filters },
    });
  },
  loadByUsername(username) {
    const url = `/users/${username}`;
    return ADMIN_AXIOS.get(url);
  },
  loadAvailableRoles(username) {
    const url = `/users/${username}/roles/available`;
    return ADMIN_AXIOS.get(url);
  },
  saveUserRoles(username, roleIds) {
    const url = `/users/${username}/roles`;
    return ADMIN_AXIOS.put(url, roleIds);
  },
  checkEmail(email, username) {
    const url = `/validation/email?email=${email}&username=${username}`;
    return ADMIN_AXIOS.get(url);
  },
  checkMobile(mobile, username) {
    const url = `/validation/mobile?mobile=${mobile}&username=${username}`;
    return ADMIN_AXIOS.get(url);
  },
  addUser(user) {
    const url = `/users`;
    return ADMIN_AXIOS.post(url, user);
  },
  updateUser(user) {
    const url = `/users/${user.username}`;
    return ADMIN_AXIOS.put(url, user);
  },
  toggleEnabled(username) {
    const url = `/users/${username}/enabled`;
    return ADMIN_AXIOS.put(url, null);
  },
  loadRoles(size, page, offset, sort, filters) {
    return ADMIN_AXIOS.get("/roles", {
      params: { size, page, offset, sort, ...filters },
    });
  },
  checkRoleName(roleName) {
    const url = `/validation/role-name?roleName=${roleName}`;
    return ADMIN_AXIOS.get(url);
  },
  checkRoleNameNotSelf(id, roleName) {
    const url = `/validation/roles/${id}/role-name?roleName=${roleName}`;
    return ADMIN_AXIOS.get(url);
  },
  addRole(role) {
    const url = `/roles`;
    return ADMIN_AXIOS.post(url, role);
  },
  updateRole(role) {
    const url = `/roles/${role.id}`;
    return ADMIN_AXIOS.put(url, role);
  },
  deleteRole(id) {
    const url = `/roles/${id}`;
    return ADMIN_AXIOS.delete(url);
  },
  loadByRoleId(id) {
    const url = `/roles/${id}`;
    return ADMIN_AXIOS.get(url);
  },
  loadAvailablePermissions(roleId) {
    const url = `/roles/${roleId}/permissions/available`;
    return ADMIN_AXIOS.get(url);
  },
  loadPermissions() {
    const url = `/permissions`;
    return ADMIN_AXIOS.get(url);
  },
  saveRolePermissions(roleId, permissionIds) {
    const url = `/roles/${roleId}/permissions`;
    return ADMIN_AXIOS.put(url, permissionIds);
  },
  loadClients() {
    const url = `/clients`;
    return ADMIN_AXIOS.get(url);
  },
  addClient(client) {
    const url = `/clients`;
    return ADMIN_AXIOS.post(url, client);
  },
  updateClient(client) {
    const url = `/clients/${client.clientId}`;
    return ADMIN_AXIOS.put(url, client);
  },
  deleteClient(id) {
    const url = `/clients/${id}`;
    return ADMIN_AXIOS.delete(url);
  },
};
