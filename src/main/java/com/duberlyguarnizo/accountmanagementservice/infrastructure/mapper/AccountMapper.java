/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.infrastructure.mapper;

import com.duberlyguarnizo.accountmanagementservice.domain.model.AssetAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.model.PassiveAccount;
import com.duberlyguarnizo.accountmanagementservice.infrastructure.repository.AssetAccountEntity;
import com.duberlyguarnizo.accountmanagementservice.infrastructure.repository.PassiveAccountEntity;
import java.time.Instant;

public class AccountMapper {
  public static AssetAccountEntity toEntity(AssetAccount assetAccount) {
    return AssetAccountEntity.builder()
        .accountCode(assetAccount.getAccountCode())
        .balance(assetAccount.getBalance())
        .createdAt(Instant.now())
        .accountType(assetAccount.getAccountType())
        .clientId(assetAccount.getClientId())
        .assetAccountType(assetAccount.getAssetAccountType())
        .lastPaymentDate(assetAccount.getLastPaymentDate())
        .loanOrCreditCardLimit(assetAccount.getLoanOrCreditCardLimit())
        .createdAt(assetAccount.getCreatedAt())
        .build();
  }

  public static AssetAccount toDomain(AssetAccountEntity assetAccountEntity) {
    AssetAccount result = new AssetAccount();
    result.setLastPaymentDate(assetAccountEntity.getLastPaymentDate());
    result.setLoanOrCreditCardLimit(assetAccountEntity.getLoanOrCreditCardLimit());
    result.setAccountType(assetAccountEntity.getAccountType());
    result.setBalance(assetAccountEntity.getBalance());
    result.setAccountCode(assetAccountEntity.getAccountCode());
    result.setClientId(assetAccountEntity.getClientId());
    result.setAccountType(assetAccountEntity.getAccountType());
    result.setCreatedAt(assetAccountEntity.getCreatedAt());
    return result;
  }

  public static PassiveAccountEntity toEntity(PassiveAccount passiveAccount) {
    return PassiveAccountEntity.builder()
        .accountCode(passiveAccount.getAccountCode())
        .balance(passiveAccount.getBalance())
        .createdAt(Instant.now())
        .accountType(passiveAccount.getAccountType())
        .clientId(passiveAccount.getClientId())
        .passiveAccountType(passiveAccount.getPassiveAccountType())
        .createdAt(passiveAccount.getCreatedAt())
        .build();
  }

  public static PassiveAccount toDomain(PassiveAccountEntity passiveEntity) {
    PassiveAccount result = new PassiveAccount();
    result.setBalance(passiveEntity.getBalance());
    result.setAccountType(passiveEntity.getAccountType());
    result.setAccountCode(passiveEntity.getAccountCode());
    result.setClientId(passiveEntity.getClientId());
    result.setAccountType(passiveEntity.getAccountType());
    result.setCreatedAt(passiveEntity.getCreatedAt());
    return result;
  }

}
