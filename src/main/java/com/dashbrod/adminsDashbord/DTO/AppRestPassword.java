package com.dashbrod.adminsDashbord.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppRestPassword {
String oldPassword;
String code;
String newPassword;
}
