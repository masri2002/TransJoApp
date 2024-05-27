package com.dashbrod.adminsDashbord.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordResetDto {
    private String email;
    private String code;
    private String newPassword;
}
