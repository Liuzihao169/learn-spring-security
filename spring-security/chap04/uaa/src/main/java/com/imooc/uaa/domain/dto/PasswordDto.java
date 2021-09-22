package com.imooc.uaa.domain.dto;

import com.imooc.uaa.validation.ValidPassword;
import lombok.Data;

@Data
public class PasswordDto {
    private String oldPassword;

    @ValidPassword
    private String newPassword;
}
