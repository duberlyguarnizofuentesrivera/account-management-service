/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.infrastructure.repository;

import com.duberlyguarnizo.accountmanagementservice.domain.enums.AccountType;
import com.duberlyguarnizo.accountmanagementservice.domain.enums.AssetAccountType;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Document(collection = "asset_accounts")
public class AssetAccountEntity {
  @Id
  private String id;
  private String accountCode;
  private double balance;
  private double loanOrCreditCardLimit;
  private UUID clientId;
  private AssetAccountType assetAccountType;
  private AccountType accountType;
  private Instant createdAt;
  private Instant lastPaymentDate;
}
