package com.dashbrod.adminsDashbord.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDetailsDto {
    private  String name;
    private String password;
    private String email;

}
