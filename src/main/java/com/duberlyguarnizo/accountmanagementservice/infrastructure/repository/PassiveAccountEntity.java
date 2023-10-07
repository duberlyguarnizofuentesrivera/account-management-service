/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.infrastructure.repository;

import com.duberlyguarnizo.accountmanagementservice.domain.enums.AccountType;
import com.duberlyguarnizo.accountmanagementservice.domain.enums.PassiveAccountType;
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
@Document(collection = "passive_accounts")
public class PassiveAccountEntity {
  @Id
  private String id;
  private String accountCode;
  private double balance;
  private UUID clientId;
  private PassiveAccountType passiveAccountType;
  private AccountType accountType;
  private Instant createdAt;
}
