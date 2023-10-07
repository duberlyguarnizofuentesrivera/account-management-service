/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.domain.model;

import com.duberlyguarnizo.accountmanagementservice.domain.enums.AssetAccountType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetAccount extends Account {
  private AssetAccountType assetAccountType;
  private double loanOrCreditCardLimit;
  private Instant lastPaymentDate;
}
