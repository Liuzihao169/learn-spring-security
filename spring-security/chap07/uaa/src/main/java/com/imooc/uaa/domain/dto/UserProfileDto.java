package com.imooc.uaa.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imooc.uaa.util.Constants;
import com.imooc.uaa.validation.ValidEmail;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
@With
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @Pattern(regexp = Constants.PATTERN_MOBILE)
    @NotNull
    private String mobile;

    @ValidEmail
    @NotNull
    @Size(min = 1)
    private String email;
}
