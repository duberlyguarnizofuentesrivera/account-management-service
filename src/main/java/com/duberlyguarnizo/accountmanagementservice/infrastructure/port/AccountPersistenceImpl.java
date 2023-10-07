/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.infrastructure.port;

import com.duberlyguarnizo.accountmanagementservice.domain.model.Account;
import com.duberlyguarnizo.accountmanagementservice.domain.model.AssetAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.model.PassiveAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.persistence.AccountPersistence;
import com.duberlyguarnizo.accountmanagementservice.infrastructure.mapper.AccountMapper;
import com.duberlyguarnizo.accountmanagementservice.infrastructure.repository.AssetAccountEntity;
import com.duberlyguarnizo.accountmanagementservice.infrastructure.repository.AssetAccountRepository;
import com.duberlyguarnizo.accountmanagementservice.infrastructure.repository.PassiveAccountEntity;
import com.duberlyguarnizo.accountmanagementservice.infrastructure.repository.PassiveAccountRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountPersistenceImpl implements AccountPersistence {
  private final AssetAccountRepository assetRepository;
  private final PassiveAccountRepository passiveRepository;

  public AccountPersistenceImpl(AssetAccountRepository assetRepository,
                                PassiveAccountRepository passiveRepository) {
    this.assetRepository = assetRepository;
    this.passiveRepository = passiveRepository;
  }


  @Override
  public Single<Double> getAccountBalance(String accountCode) {

    return assetRepository.findByAccountCode(accountCode)
        .map(AssetAccountEntity::getBalance)
        .switchIfEmpty(passiveRepository.findByAccountCode(accountCode)
            .map(PassiveAccountEntity::getBalance)).toSingle();

  }

  @Override
  public Maybe<Account> getAccountByAccountId(UUID accountId) {
    return null;
  }

  @Override
  public Maybe<Account> getAccountByAccountCode(String accountCode) {
    return null;
  }

  @Override
  public Observable<AssetAccount> getAssetAccountsByClientId(UUID clientId) {
    log.warn("Entering getAssetAccountsByClientId at persistence implementation");
    return assetRepository.findByClientId(clientId).map(AccountMapper::toDomain);
  }

  @Override
  public Observable<PassiveAccount> getPassiveAccountsByClientId(UUID clientId) {
    return passiveRepository.findByClientId(clientId).map(AccountMapper::toDomain);
  }

  @Override
  public Completable createAccount(Account account) {
    log.warn("Creating account!!!!");
    switch (account.getAccountType()) {
      case ASSET:
        log.warn("Saving passive account at repository CreateAccount method!!!!");
        return assetRepository.save(AccountMapper.toEntity((AssetAccount) account))
            .ignoreElement();
      case PASSIVE:
        return passiveRepository.save(AccountMapper.toEntity((PassiveAccount) account))
            .ignoreElement();
      default:
        return Completable.error(new RuntimeException("Invalid account type"));
    }
  }

  @Override
  public Completable addToBalance(UUID accountId, double amount) {
    return null;
  }

  @Override
  public Completable subtractFromBalance(UUID accountId, double amount) {
    return null;
  }

  @Override
  public Completable updateAccount(Account account) {
    return null;
  }

  @Override
  public Completable deleteAccount(UUID accountId) {
    return null;
  }
}
