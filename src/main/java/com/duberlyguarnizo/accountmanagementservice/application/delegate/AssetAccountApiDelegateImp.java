/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.application.delegate;

import com.duberlyguarnizo.accountmanagementservice.api.AccountsApi;
import com.duberlyguarnizo.accountmanagementservice.api.AccountsApiDelegate;
import com.duberlyguarnizo.accountmanagementservice.application.rest.AccountUseCases;
import com.duberlyguarnizo.accountmanagementservice.domain.AccountListForUserDto;
import com.duberlyguarnizo.accountmanagementservice.domain.AssetAccountCreationDto;
import com.duberlyguarnizo.accountmanagementservice.domain.AssetAccountDto;
import com.duberlyguarnizo.accountmanagementservice.domain.PassiveAccountCreationDto;
import com.duberlyguarnizo.accountmanagementservice.domain.PassiveAccountDto;
import com.duberlyguarnizo.accountmanagementservice.domain.model.AssetAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.model.PassiveAccount;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.adapter.rxjava.RxJava3Adapter;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AssetAccountApiDelegateImp implements AccountsApiDelegate {
  private final AccountUseCases useCases;

  public AssetAccountApiDelegateImp(AccountUseCases useCases) {
    this.useCases = useCases;
  }

  /**
   * POST /accounts/asset : Create new asset account
   * Create new asset account for a client.
   *
   * @param assetAccountCreationDto (optional)
   * @param exchange                The request exchange
   * @return New account created. (status code 201)
   *     or Bad request. Check the request body. (status code 400)
   *     or Account not created. Internal server error. (status code 500)
   * @see AccountsApi#createAssetAccount
   */
  @Override
  public Mono<ResponseEntity<Void>> createAssetAccount(
      Mono<AssetAccountCreationDto> assetAccountCreationDto, ServerWebExchange exchange) {
    try {
      useCases.createAssetAccount(RxJava3Adapter.monoToSingle(assetAccountCreationDto));
      return Mono.just(new ResponseEntity<>(HttpStatus.CREATED));
    } catch (RuntimeException e) {
      return Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
  }

  /**
   * POST /accounts/passive : Create new passive account
   * Create new passive account for a client.
   *
   * @param passiveAccountCreationDto (optional)
   * @param exchange                  The request exchange
   * @return New account created. (status code 201)
   *     or Bad request. Check the request body. (status code 400)
   *     or Account not created. Internal server error. (status code 500)
   * @see AccountsApi#createPassiveAccount
   */
  @Override
  public Mono<ResponseEntity<Void>> createPassiveAccount(
      Mono<PassiveAccountCreationDto> passiveAccountCreationDto, ServerWebExchange exchange) {
    log.warn("Creating account at delegate");
    return passiveAccountCreationDto.flatMap(dto -> {
      useCases.createPassiveAccount(dto).blockingSubscribe();
      log.warn("Account created at delegate");
      return Mono.just(new ResponseEntity<Void>(HttpStatus.CREATED));
    }).switchIfEmpty(Mono.error(new Exception("passiveAccountCreationDto was empty")));
  }

  /**
   *
   * GET /accounts/{clientId} : Get accounts for client
   * Composed list of accounts for the specified client.
   *
   * @param clientId the Id card or legal Id of the client. (optional)
   * @param exchange The request exchange
   * @return Successful Operation (status code 200)
   * @see AccountsApi#getAccountsByClientId
   */
  /**
   * GET /accounts/{clientId} : Get accounts for client
   * Composed list of accounts for the specified client.
   *
   * @param clientId the Id card or legal Id of the client. (optional)
   * @param exchange
   * @return Successful Operation (status code 200)
   * @see AccountsApi#getAccountsByClientId
   */
  @Override
  public Mono<ResponseEntity<AccountListForUserDto>> getAccountsByClientId(UUID clientId,
                                                                           ServerWebExchange exchange) {
    final AccountListForUserDto[] result = new AccountListForUserDto[1];
    useCases.getAllAccountsForClient(clientId)
        .subscribe(accountList -> result[0] = accountList).dispose();
    return Mono.just(new ResponseEntity<>(result[0], HttpStatus.OK));
  }

  /**
   * GET /accounts/asset/{accountCode} : Get a single asset account
   * Get a single asset account by account code.
   *
   * @param accountCode The account number (required)
   * @param exchange    The request exchange
   * @return Successful Operation (status code 200)
   *     or No account found with the provided account code. (status code 404)
   * @see AccountsApi#getAssetAccountByAccountCode
   */
  @Override
  public Mono<ResponseEntity<AssetAccountDto>> getAssetAccountByAccountCode(
      String accountCode, ServerWebExchange exchange) {
    return RxJava3Adapter.maybeToMono(useCases.getAccountByAccountCode(accountCode).map(acc -> {
      AssetAccount asset = (AssetAccount) acc;
      var result = AssetAccountDto.builder()
          .accountCode(asset.getAccountCode())
          .createdAt(OffsetDateTime.from(asset.getCreatedAt()))
          .clientId(asset.getClientId())
          .balance(asset.getBalance())
          .assetAccountType(AssetAccountDto.AssetAccountTypeEnum.fromValue(asset.getAssetAccountType().name()))
          .build();
      return ResponseEntity.ok(result);
    }));
  }

  /**
   * GET /accounts/passive/{accountCode} : Get a single passive accounts
   * Get a single passive account by accountId.
   *
   * @param accountCode The account number (required)
   * @param exchange    The request exchange
   * @return Successful Operation (status code 200)
   *     or No account found with the provided account code. (status code 404)
   * @see AccountsApi#getPassiveAccountByAccountCode
   */
  @Override
  public Mono<ResponseEntity<PassiveAccountDto>> getPassiveAccountByAccountCode(
      String accountCode, ServerWebExchange exchange) {
    return RxJava3Adapter.maybeToMono(useCases.getAccountByAccountCode(accountCode).map(acc -> {
      PassiveAccount passive = (PassiveAccount) acc;
      var result = PassiveAccountDto.builder()
          .accountCode(passive.getAccountCode())
          .createdAt(OffsetDateTime.from(passive.getCreatedAt()))
          .clientId(passive.getClientId())
          .balance(passive.getBalance())
          .passiveAccountType(PassiveAccountDto.PassiveAccountTypeEnum
              .fromValue(passive.getPassiveAccountType().name()))
          .build();
      return ResponseEntity.ok(result);
    }));
  }
}
