package com.blueskykong.demo.entity;

import lombok.Data;

import java.util.UUID;

/**
 * @author keets
 * @date 2017/11/21
 */
@Data
public class UserAccess {
    private Long id;

    private UUID userId;

    private Integer accessLevel;
}
