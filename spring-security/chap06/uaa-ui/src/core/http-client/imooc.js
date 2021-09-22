import axios from "axios";

export const IMOOC_AXIOS = axios.create({
  baseURL: `https://apis.imooc.com`,
  timeout: 10000,
});
