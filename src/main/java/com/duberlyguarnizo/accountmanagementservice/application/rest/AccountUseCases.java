/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.application.rest;

import com.duberlyguarnizo.accountmanagementservice.domain.AccountListForUserDto;
import com.duberlyguarnizo.accountmanagementservice.domain.AssetAccountCreationDto;
import com.duberlyguarnizo.accountmanagementservice.domain.AssetAccountDto;
import com.duberlyguarnizo.accountmanagementservice.domain.PassiveAccountCreationDto;
import com.duberlyguarnizo.accountmanagementservice.domain.PassiveAccountDto;
import com.duberlyguarnizo.accountmanagementservice.domain.enums.AccountType;
import com.duberlyguarnizo.accountmanagementservice.domain.enums.AssetAccountType;
import com.duberlyguarnizo.accountmanagementservice.domain.enums.PassiveAccountType;
import com.duberlyguarnizo.accountmanagementservice.domain.model.Account;
import com.duberlyguarnizo.accountmanagementservice.domain.model.AssetAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.model.PassiveAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.service.AssetAccountService;
import com.duberlyguarnizo.accountmanagementservice.domain.service.PassiveAccountService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountUseCases {
  private final AssetAccountService assetService;
  private final PassiveAccountService passiveService;

  public AccountUseCases(AssetAccountService assetService, PassiveAccountService passiveService) {
    this.assetService = assetService;
    this.passiveService = passiveService;
  }

  public Completable createAssetAccount(Single<AssetAccountCreationDto> dto) {
    AssetAccount account = new AssetAccount();
    return dto.flatMapCompletable(creationDto -> {
      account.setBalance(creationDto.getBalance());
      account.setAssetAccountType(AssetAccountType
          .valueOf(creationDto.getAssetAccountType().name()));
      account.setClientId(creationDto.getClientId());
      account.setAccountType(AccountType.ASSET);
      return assetService.createAssetAccount(account);
    });

  }

  public Completable createPassiveAccount(PassiveAccountCreationDto dto) {
    log.warn("Entering createPassiveAccount() at RestService");
    PassiveAccount account = new PassiveAccount();
    account.setBalance(dto.getBalance());
    account.setAccountType(AccountType.PASSIVE);
    account.setPassiveAccountType(PassiveAccountType
        .valueOf(dto.getPassiveAccountType().name()));
    account.setClientId(dto.getClientId());
    log.warn("Creating passive account at RestService");
    return passiveService.createPassiveAccount(account);

  }

  public Maybe<Account> getAccountByAccountCode(String accountCode) {
    return assetService.getAssetAccountByAccountCode(accountCode)
        .switchIfEmpty(passiveService.getPassiveAccountByAccountCode(accountCode));
  }

  public Single<AccountListForUserDto> getAllAccountsForClient(UUID clientId) {
    final List<AssetAccountDto> dtoAssetList = new ArrayList<>();
    final List<PassiveAccountDto> dtoPassiveList = new ArrayList<>();
    passiveService.getAllPassiveAccountsForClient(clientId).map(account -> {
      log.warn("Getting passive account: " + account);
      return PassiveAccountDto.builder()
          .accountCode(account.getAccountCode())
          .passiveAccountType(PassiveAccountDto.PassiveAccountTypeEnum
              .fromValue(account.getPassiveAccountType().name()))
          .clientId(account.getClientId())
          .balance(account.getBalance())
          .createdAt(OffsetDateTime.from(account.getCreatedAt()))
          .build();
    }).toList().subscribe(list -> dtoPassiveList.addAll(list),
        error -> log.error("Error getting passive accounts: " + error.toString()));

    assetService.getAssetAccountsForClient(clientId).map(account -> {
      return AssetAccountDto.builder()
          .accountCode(account.getAccountCode())
          .assetAccountType(AssetAccountDto.AssetAccountTypeEnum
              .fromValue(account.getAssetAccountType().name()))
          .clientId(account.getClientId())
          .balance(account.getBalance())
          .createdAt(OffsetDateTime.from(account.getCreatedAt()))
          .build();
    }).toList().subscribe(list -> dtoAssetList.addAll(list),
        error -> log.error("Error getting asset accounts: " + error.toString())).dispose();
    return Single.just(AccountListForUserDto.builder()
        .assetAccounts(dtoAssetList)
        .passiveAccounts(dtoPassiveList).build());
  }
}
