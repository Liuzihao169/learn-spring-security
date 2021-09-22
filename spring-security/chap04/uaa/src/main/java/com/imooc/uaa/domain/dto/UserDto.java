package com.imooc.uaa.domain.dto;

import com.imooc.uaa.validation.PasswordMatches;
import com.imooc.uaa.validation.ValidEmail;
import com.imooc.uaa.validation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@PasswordMatches
@Data
public class UserDto implements Serializable {
    @NotNull
    @Size(min = 1)
    private String name;

    @ValidPassword
    private String password;

    @NotNull
    @Size(min = 1)
    private String matchingPassword;

    @ValidEmail
    @NotNull
    @Size(min = 1)
    private String email;

    private boolean isUsing2FA;

}
