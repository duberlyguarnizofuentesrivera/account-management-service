/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.service;

import com.duberlyguarnizo.accountmanagementservice.domain.AssetAccount;
import com.duberlyguarnizo.accountmanagementservice.enums.AssetAccountType;
import com.duberlyguarnizo.accountmanagementservice.enums.ClientType;
import com.duberlyguarnizo.accountmanagementservice.repository.AssetAccountEntity;

public class AssetAccountMapper {
  public static AssetAccountEntity toEntity(AssetAccount assetAccount) {
    return AssetAccountEntity.builder()
        .accountCode(assetAccount.getAccountCode())
        .balance(assetAccount.getBalance())
        .createdBy(assetAccount.getCreatedBy())
        .createdAt(assetAccount.getCreatedAt())
        .clientType(toDomainEnum(assetAccount.getClientType()))
        .clientId(assetAccount.getClientId())
        .assetAccountType(toDomainEnum(assetAccount.getAssetAccountType()))
        .build();
  }

  public static AssetAccount toDomain(AssetAccountEntity assetAccountEntity) {
    return AssetAccount.builder()
        .accountCode(assetAccountEntity.getAccountCode())
        .balance(assetAccountEntity.getBalance())
        .createdBy(assetAccountEntity.getCreatedBy())
        .createdAt(assetAccountEntity.getCreatedAt())
        .clientType(toApiEnum(assetAccountEntity.getClientType()))
        .clientId(assetAccountEntity.getClientId())
        .assetAccountType(toApiEnum(assetAccountEntity.getAssetAccountType()))
        .build();
  }

  private static AssetAccount.ClientTypeEnum toApiEnum(ClientType clientType) {
    return AssetAccount.ClientTypeEnum.valueOf(clientType.name());
  }

  private static ClientType toDomainEnum(AssetAccount.ClientTypeEnum clientType) {
    return ClientType.valueOf(clientType.name());
  }

  private static AssetAccount.AssetAccountTypeEnum toApiEnum(AssetAccountType assetAccountType) {
    return AssetAccount.AssetAccountTypeEnum.valueOf(assetAccountType.name());
  }

  private static AssetAccountType toDomainEnum(AssetAccount.AssetAccountTypeEnum assetAccountType) {
    return AssetAccountType.valueOf(assetAccountType.name());
  }


}
