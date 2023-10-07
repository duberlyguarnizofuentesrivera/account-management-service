/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.domain.model;

import com.duberlyguarnizo.accountmanagementservice.domain.enums.AccountType;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
  private UUID clientId;
  private String accountCode;
  private double balance;
  private AccountType accountType;
  private Instant createdAt;
}
