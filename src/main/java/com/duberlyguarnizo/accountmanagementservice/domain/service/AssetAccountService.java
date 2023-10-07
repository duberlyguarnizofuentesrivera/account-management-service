/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.domain.service;

import com.duberlyguarnizo.accountmanagementservice.domain.enums.AccountType;
import com.duberlyguarnizo.accountmanagementservice.domain.enums.AssetAccountType;
import com.duberlyguarnizo.accountmanagementservice.domain.model.Account;
import com.duberlyguarnizo.accountmanagementservice.domain.model.AssetAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.model.PassiveAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.persistence.AccountPersistence;
import com.duberlyguarnizo.accountmanagementservice.exception.AccountDoesNotExistException;
import com.duberlyguarnizo.accountmanagementservice.exception.CreditCardLimitReachedException;
import com.duberlyguarnizo.accountmanagementservice.exception.DebtPastDueException;
import com.duberlyguarnizo.accountmanagementservice.exception.IncompatibleAccountTypeException;
import com.duberlyguarnizo.accountmanagementservice.exception.OverpaidAssetAccountException;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AssetAccountService {
  private final AccountPersistence persistence;
  private final InterServiceOperations operations;

  public AssetAccountService(AccountPersistence persistence, InterServiceOperations operations) {
    this.persistence = persistence;
    this.operations = operations;
  }

  public Observable<AssetAccount> getAssetAccountsForClient(UUID clientId) {
    return persistence.getAssetAccountsByClientId(clientId);
  }

  public Observable<PassiveAccount> getAllPassiveAccountsForClient(UUID clientId) {
    return persistence.getPassiveAccountsByClientId(clientId);
  }

  public Maybe<Account> getAssetAccountByAccountCode(String accountCode) {
    return persistence.getAccountByAccountCode(accountCode);
  }

  public Completable createAssetAccount(AssetAccount account) {
    return cardOrLoanIsPastDue(account.getLastPaymentDate()).flatMapCompletable(hasDueDebt -> {
      if (hasDueDebt) {
        return Completable.error(
            new DebtPastDueException("Debt is past due, cannot create new account until paid"));
      } else {
        account.setLastPaymentDate(Instant.now());

        var assetSum = persistence.getAssetAccountsByClientId(account.getClientId())
            .filter(ac -> ac.getAccountType() == AccountType.ASSET)
            .map(Account::getBalance)
            .reduce(0.0, Double::sum);
        var passiveSum = persistence.getPassiveAccountsByClientId(account.getClientId())
            .filter(ac -> ac.getAccountType() == AccountType.PASSIVE)
            .map(Account::getBalance)
            .reduce(0.0, Double::sum);
        Single.zip(passiveSum, assetSum, (passive, asset) -> passive - asset)
            .subscribe(creditLimit -> {
              if (creditLimit > 0) {
                account.setLoanOrCreditCardLimit(creditLimit);
              } else {
                account.setLoanOrCreditCardLimit(0.0);
              }
            }).dispose();
        account.setLastPaymentDate(Instant.now());
        account.setAccountCode(generateAssetAccountCode(account.getAssetAccountType()));
        return persistence.createAccount(account);
      }
    });
  }

  public Single<Double> getAccountBalance(String accountCode) {
    return persistence.getAccountBalance(accountCode);
  }

  public Completable increaseLoanDebtForBankClient(UUID loanAccountId, UUID destinationAccountId,
                                                   double amount) {
    return persistence.getAccountByAccountId(loanAccountId)
        .flatMapCompletable(account -> {
          persistence.addToBalance(loanAccountId, amount);
          return persistence.addToBalance(destinationAccountId, amount);
        });
  }


  public Completable increaseLoanDebtForNonBankClient(UUID loanAccountId, double amount) {
    return persistence.getAccountByAccountId(loanAccountId)
        .flatMapCompletable(account ->
            persistence.getPassiveAccountsByClientId(account.getClientId()).count()
                .flatMapCompletable(passiveAccountCount -> {
                  if (passiveAccountCount == 0) {
                    if (account.getAccountType() == AccountType.ASSET) {
                      return persistence.addToBalance(loanAccountId,
                          amount);
                    } else {
                      return Completable.error(
                          new IncompatibleAccountTypeException("Account is not a loan"
                                                               + " or credit card account"));
                    }
                  } else {
                    return Completable.error(
                        new IncompatibleAccountTypeException("For non clients you must specify"
                                                             + " a destination account"));
                  }
                })
        );
  }

  public Completable increaseCreditCardDebt(UUID accountId, double amount) {
    return persistence.getAccountByAccountId(accountId)
        .flatMapCompletable(account -> {
          if (account.getAccountType() == AccountType.ASSET) {
            AssetAccount assetAccount = (AssetAccount) account;

            if (assetAccount.getLoanOrCreditCardLimit() > account.getBalance() + amount) {
              return persistence.addToBalance(accountId, amount);
            } else {
              return Completable.error(
                  new CreditCardLimitReachedException("Credit card debt limit exceeded"));
            }
          } else {
            return Completable.error(
                new IncompatibleAccountTypeException("Account is not a credit card account"));
          }
        });
  }

  public Completable payLoanOrCreditCard(UUID accountId, double amount) {
    return persistence.getAccountByAccountId(accountId)
        .flatMapCompletable(account -> {
          if (account.getAccountType() == AccountType.ASSET) {
            if (account.getBalance() < amount) {
              return Completable.error(
                  new OverpaidAssetAccountException("Account or card debt is less than "
                                                    + "amount to pay"));
            } else {
              return persistence.subtractFromBalance(accountId, amount);
            }
          }
          return Completable.error(
              new AccountDoesNotExistException("No account found with the id: "
                                               + accountId.toString()));
        });
  }

  private Single<Boolean> cardOrLoanIsPastDue(Instant lastPaymentDate) {
    Instant oneMonthAgo = Instant.now().minus(30, ChronoUnit.DAYS);
    return Single.just(lastPaymentDate.isBefore(oneMonthAgo));
  }


  private String generateAssetAccountCode(AssetAccountType assetAccountType) {
    Random random = new Random();
    StringBuilder builder = new StringBuilder(14);
    builder.append("191");
    switch (assetAccountType) {
      case CREDIT_CARD_ACCOUNT:
        builder.append("201");
        break;
      case LOAN_ACCOUNT:
        builder.append("202");
        break;
      default:
    }
    for (int i = 0; i < 8; ++i) {
      builder.append(random.nextInt(10));
    }
    return builder.toString();
  }
}
