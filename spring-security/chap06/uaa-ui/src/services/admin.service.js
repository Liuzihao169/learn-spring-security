import ADMIN_AXIOS from "@/core/http-client/admin";

export default {
  loadUsers(page, offset, sort, filters) {
    return ADMIN_AXIOS.get("/users", {
      params: { page, offset, sort, ...filters },
    });
  },
  checkEmail(email, username) {
    const url = `/validation/email?email=${email}&username=${username}`;
    return ADMIN_AXIOS.get(url);
  },
  checkMobile(mobile, username) {
    const url = `/validation/mobile?mobile=${mobile}&username=${username}`;
    return ADMIN_AXIOS.get(url);
  },
};
