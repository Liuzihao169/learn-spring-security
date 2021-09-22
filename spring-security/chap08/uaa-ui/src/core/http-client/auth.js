import axios from "axios";

export const AUTH_AXIOS = axios.create({
  baseURL: `${process.env.VUE_APP_API_URL}/authorize`,
  timeout: 10000,
});
