package com.counsulteer.coolerimdb.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UpdateUserDto {
    private String firstName;
    private String lastName;
    private String email;
}
