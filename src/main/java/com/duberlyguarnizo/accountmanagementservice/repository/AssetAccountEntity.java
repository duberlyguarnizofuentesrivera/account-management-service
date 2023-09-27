/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.repository;

import com.duberlyguarnizo.accountmanagementservice.domain.MoneyAmount;
import com.duberlyguarnizo.accountmanagementservice.enums.AssetAccountType;
import com.duberlyguarnizo.accountmanagementservice.enums.ClientType;
import javax.validation.Valid;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AssetAccountEntity {
  @Id
  private String id;
  private String accountCode;
  @Valid
  private MoneyAmount balance;
  private ClientType clientType;
  private String clientId;
  private AssetAccountType assetAccountType;
  private OffsetDateTime createdAt;
  private String createdBy;
}
