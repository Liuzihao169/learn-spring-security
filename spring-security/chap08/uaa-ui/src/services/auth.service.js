import { AUTH_AXIOS } from "@/core/http-client/auth";

export default {
  login(username, password) {
    const url = `/token`;
    return AUTH_AXIOS.post(url, { username, password });
  },
  sendMfa(mfaId, mfaType) {
    const url = `/totp`;
    return AUTH_AXIOS.put(url, { mfaId, mfaType });
  },
  verifyMfa(mfaId, code) {
    const url = `/totp`;
    return AUTH_AXIOS.post(url, { mfaId, code });
  },
  register(user) {
    const url = "/register";
    return AUTH_AXIOS.post(url, user);
  },
  checkUsername(username) {
    const url = `/validation/username?username=${username}`;
    return AUTH_AXIOS.get(url);
  },
  checkEmail(email) {
    const url = `/validation/email?email=${email}`;
    return AUTH_AXIOS.get(url);
  },
  checkMobile(mobile) {
    const url = `/validation/mobile?mobile=${mobile}`;
    return AUTH_AXIOS.get(url);
  },
};
