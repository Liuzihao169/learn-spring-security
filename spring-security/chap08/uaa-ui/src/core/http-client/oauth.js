import axios from "axios";

export const OAUTH_AXIOS = axios.create({
  baseURL: `${process.env.VUE_APP_API_URL}`,
  timeout: 10000,
  headers: {
    "Content-Type": "application/x-www-form-urlencoded; charset=utf-8",
  },
});
