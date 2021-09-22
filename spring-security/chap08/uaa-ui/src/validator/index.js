import AUTH_API from "@/services/auth.service";
import ADMIN_API from "@/services/admin.service";
export default {
  objectNamePattern: /^[a-zA-Z0-9_]{3,50}$/,
  capitalObjectNamePattern: /^[A-Z_]{3,50}$/,
  humanNamePattern: /^([\u4e00-\u9fa5]{2,20}|[a-zA-Z.\s]{2,50})$/,
  mobilePattern: /^[a-zA-Z0-9_]{3,50}$/,
  emailPattern: /^\S+@\S+\.\S+$/,
  passwordPattern: /^.*(?=.{8,})(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*? ]).*$/,
  checkUsername: (rule, value, callback) => {
    if (value === "") {
      callback();
    }
    AUTH_API.checkUsername(value)
      .then((res) => {
        if (res.data) {
          callback(new Error("用户名已存在！"));
        }
        callback();
      })
      .catch(() => {
        callback(new Error("服务器异常"));
      });
  },
  checkEmail: (rule, value, callback) => {
    if (value === "") {
      callback();
    }
    AUTH_API.checkEmail(value)
      .then((res) => {
        if (res.data) {
          callback(new Error("电子邮件已存在！"));
        }
        callback();
      })
      .catch(() => {
        callback(new Error("服务器异常"));
      });
  },
  checkMobile: (rule, value, callback) => {
    if (value === "") {
      callback();
    }
    AUTH_API.checkMobile(value)
      .then((res) => {
        if (res.data) {
          callback(new Error("手机号码已存在！"));
        }
        callback();
      })
      .catch(() => {
        callback(new Error("服务器异常"));
      });
  },
  checkEmailNotSelf: (username) => {
    return (rule, value, callback) => {
      if (value === "") {
        callback();
      }
      ADMIN_API.checkEmail(value, username)
        .then((res) => {
          if (res.data) {
            callback(new Error("电子邮件已存在！"));
          }
          callback();
        })
        .catch(() => {
          callback(new Error("服务器异常"));
        });
    };
  },
  checkMobileNotSelf: (username) => {
    return (rule, value, callback) => {
      if (value === "") {
        callback();
      }
      ADMIN_API.checkMobile(value, username)
        .then((res) => {
          if (res.data) {
            callback(new Error("手机号码已存在！"));
          }
          callback();
        })
        .catch(() => {
          callback(new Error("服务器异常"));
        });
    };
  },
  checkRoleName: (rule, value, callback) => {
    if (value === "") {
      callback();
    }
    ADMIN_API.checkRoleName(value)
      .then((res) => {
        if (res.data) {
          callback(new Error("角色标识已存在！"));
        }
        callback();
      })
      .catch(() => {
        callback(new Error("服务器异常"));
      });
  },
  checkRoleNameNotSelf: (id) => {
    return (rule, value, callback) => {
      if (value === "") {
        callback();
      }
      ADMIN_API.checkRoleNameNotSelf(id, value)
        .then((res) => {
          if (res.data) {
            callback(new Error("角色标识已存在！"));
          }
          callback();
        })
        .catch(() => {
          callback(new Error("服务器异常"));
        });
    };
  },
};
