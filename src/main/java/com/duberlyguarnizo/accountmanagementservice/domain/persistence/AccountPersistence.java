/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.domain.persistence;

import com.duberlyguarnizo.accountmanagementservice.domain.model.Account;
import com.duberlyguarnizo.accountmanagementservice.domain.model.AssetAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.model.PassiveAccount;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.UUID;


public interface AccountPersistence {
  Single<Double> getAccountBalance(String accountCode);

  Maybe<Account> getAccountByAccountId(UUID accountId);

  Maybe<Account> getAccountByAccountCode(String accountCode);

  Observable<AssetAccount> getAssetAccountsByClientId(UUID clientId);

  Observable<PassiveAccount> getPassiveAccountsByClientId(UUID clientId);

  Completable createAccount(Account account);

  Completable addToBalance(UUID accountId, double amount);

  Completable subtractFromBalance(UUID accountId, double amount);

  Completable updateAccount(Account account);

  Completable deleteAccount(UUID accountId);

}
