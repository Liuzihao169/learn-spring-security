import { IMOOC_AXIOS } from "@/core/http-client/imooc";

export default {
  verifyCode(icode) {
    const url = `?icode=${icode}`;
    return IMOOC_AXIOS.get(url);
  },
};
