/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.delegate;

import com.duberlyguarnizo.accountmanagementservice.api.AccountsApi;
import com.duberlyguarnizo.accountmanagementservice.api.AccountsApiDelegate;
import com.duberlyguarnizo.accountmanagementservice.domain.AssetAccount;
import com.duberlyguarnizo.accountmanagementservice.domain.AssetAccountCreationDto;
import com.duberlyguarnizo.accountmanagementservice.service.AssetAccountService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssetAccountApiDelegateImp implements AccountsApiDelegate {
  private final AssetAccountService service;

  /**
   * GET /accounts/asset/{accountCode} : Get a single asset account
   * Get a single asset account by account code.
   *
   * @param accountCode The account number (required)
   * @return Successful Operation (status code 200)
   *     or No account found with the provided account code. (status code 404)
   * @see AccountsApi#getAssetAccountByAccountCode
   */
  @Override
  public ResponseEntity<AssetAccount> getAssetAccountByAccountCode(String accountCode) {
    Optional<AssetAccount> assetAccount = service.getAccountByAccountCode(accountCode);
    return assetAccount.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * POST /accounts/asset : Create new asset account
   * Create new asset account for a client.
   *
   * @param assetAccountCreationDto (optional)
   * @return New account created. (status code 201)
   *     or Bad request. Check the request body. (status code 400)
   *     or Account not created. Internal server error. (status code 500)
   * @see AccountsApi#createAssetAccount
   */
  @Override
  public ResponseEntity<AssetAccount> createAssetAccount(AssetAccountCreationDto assetAccountCreationDto) {
    return AccountsApiDelegate.super.createAssetAccount(assetAccountCreationDto);
  }

}
