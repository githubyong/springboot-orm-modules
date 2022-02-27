package org.example.sdj.advance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.sdj.advance.entity.UserAddress;
import org.example.sdj.advance.support.entity.CustomField;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressDTO extends UserAddress {

    @CustomField(refValue = "user_info.LOGIN_NAME")
    private String loginName;
}
