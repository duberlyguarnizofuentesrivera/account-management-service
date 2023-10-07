/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.domain.service;

import com.duberlyguarnizo.accountmanagementservice.domain.enums.ClientType;
import com.duberlyguarnizo.accountmanagementservice.domain.enums.PassiveAccountType;
import com.duberlyguarnizo.accountmanagementservice.domain.model.Account;
import com.duberlyguarnizo.accountmanagementservice.domain.model.PassiveAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.persistence.AccountPersistence;
import com.duberlyguarnizo.accountmanagementservice.exception.DebtPastDueException;
import com.duberlyguarnizo.accountmanagementservice.exception.IncompatibleAccountTypeException;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PassiveAccountService {
  private final InterServiceOperations operations;
  private final AccountPersistence persistence;

  public PassiveAccountService(InterServiceOperations operations, AccountPersistence persistence) {
    this.operations = operations;
    this.persistence = persistence;
  }

  public Observable<PassiveAccount> getAllPassiveAccountsForClient(UUID clientId) {
    return persistence.getPassiveAccountsByClientId(clientId);
  }

  public Maybe<Account> getPassiveAccountByAccountCode(String accountCode) {
    return persistence.getAccountByAccountCode(accountCode);
  }

  public Completable createPassiveAccount(PassiveAccount account) {
    log.warn("entering create passive account at Service level");

    ClientType clientType = ClientType.REGULAR_CLIENT; //TODO: implement client type from Kafka

    return cardOrLoanIsPastDue(account.getClientId())
        .flatMapCompletable(isPastDue -> {
          if (isPastDue) {
            log.error("cannot create passive account: is past due");
            return Completable.error(
                new DebtPastDueException("Client has a past due card or loan. Cannot create "
                                         + "account")
            );
          }
          return persistence.getPassiveAccountsByClientId(account.getClientId()).toList()
              .flatMapCompletable(
                  accList -> {
                    boolean isValid = isValidCreation(account, clientType, accList);
                    log.warn("isValid is: " + isValid);
                    if (isValid) {
                      log.warn("account before persistence: " + account.toString());
                      account.setAccountCode(generatePassiveAccountCode(account
                          .getPassiveAccountType()));
                      account.setCreatedAt(Instant.now());
                      persistence.createAccount(account).subscribeOn(Schedulers.io())
                          .subscribe(() -> {
                            log.warn("passive account created at service level");
                          }, error -> log.error("error creating passive account at service level "
                                                + error.getMessage()));
                      log.warn("account after persistence: " + account.toString());
                      return Completable.complete();
                    } else {
                      log.error("is not valid");
                      return Completable.error(
                          new IncompatibleAccountTypeException("The account type is not "
                                                               + "compatible with the client type.")
                      );
                    }
                  }
              );
        });
  }
  //TODO: implement delete and update methods

  public Single<Double> getAccountBalance(String accountCode) {
    return persistence.getAccountBalance(accountCode);
  }

  public Completable increaseAccountBalance(UUID accountId, double amount) {
    return persistence.addToBalance(accountId, amount);
  }

  public Completable decreaseAccountBalance(UUID accountId, double amount) {
    return persistence.subtractFromBalance(accountId, amount);
  }

  private boolean isValidCreation(PassiveAccount account, ClientType clientType,
                                  List<PassiveAccount> accountList) {
    boolean result = false;
    switch (clientType) {
      case REGULAR_CLIENT:
        result = accountList.stream()
            .map(PassiveAccount::getPassiveAccountType)
            .filter(Objects::nonNull)
            .noneMatch(enumerator -> enumerator == account.getPassiveAccountType());

        log.warn("isValidCreation result evaluation for Regular client is: " + result);
        break;
      case CORPORATE_CLIENT:
        result = account.getPassiveAccountType() == PassiveAccountType.CHECKING_ACCOUNT;
        break;
      default:
        throw new IncompatibleAccountTypeException("The client should be of "
                                                   + "type Regular or Corporate");
    }
    log.warn("isValidCreation result is: " + result);
    return result;
  }

  private Single<Boolean> cardOrLoanIsPastDue(UUID clientId) {
    log.warn("entering card or loan is past due method at Service level");
    return persistence.getAssetAccountsByClientId(clientId)
        .any(x -> x.getLastPaymentDate().isBefore(Instant.now().minus(30, ChronoUnit.DAYS)))
        .doOnSuccess(result -> {
          log.warn("leaving card or loan is past due method at Service level");
          log.warn("result is: " + result);
        });

  }

  private String generatePassiveAccountCode(PassiveAccountType passiveAccountType) {
    Random random = new Random();
    StringBuilder builder = new StringBuilder(14);
    builder.append("191");
    switch (passiveAccountType) {
      case SAVINGS_ACCOUNT:
        builder.append("101");
        break;
      case CHECKING_ACCOUNT:
        builder.append("102");
        break;
      case FIXED_TERM_SAVINGS_ACCOUNT:
        builder.append("103");
        break;
      default:
    }
    for (int i = 0; i < 8; ++i) {
      builder.append(random.nextInt(10));
    }
    return builder.toString();
  }

}
