/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.service;

import com.duberlyguarnizo.accountmanagementservice.domain.AssetAccount;
import com.duberlyguarnizo.accountmanagementservice.repository.AssetAccountEntity;
import com.duberlyguarnizo.accountmanagementservice.repository.AssetAccountRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AssetAccountService {
  private final AssetAccountRepository repository;

  public Optional<AssetAccount> getAccountByAccountCode(String accountCode) {
    Optional<AssetAccountEntity> result = repository.findByAccountCode(accountCode);
    return result.map(AssetAccountMapper::toDomain);
  }
}
