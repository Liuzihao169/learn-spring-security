package com.imooc.webclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Todo implements Serializable {
    private Long id;
    private String title;
    private String desc;
    private String createdBy;
}
