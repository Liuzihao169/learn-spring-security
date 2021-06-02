package com.springsecurity.demospringsecurity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springsecurity.demospringsecurity.entity.Users;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liuzihao
 * @create 2021-05-31-22:06
 */
@Mapper
public interface UserMapper extends BaseMapper<Users> {
}
