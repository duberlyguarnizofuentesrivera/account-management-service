/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.domain.model;

import com.duberlyguarnizo.accountmanagementservice.domain.enums.PassiveAccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassiveAccount extends Account {
  //TODO: Rename this
  private PassiveAccountType passiveAccountType;
}
