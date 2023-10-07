/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.infrastructure.repository;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import java.util.UUID;
import org.springframework.data.repository.reactive.RxJava3CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetAccountRepository extends RxJava3CrudRepository<AssetAccountEntity, String> {
  Maybe<AssetAccountEntity> findByAccountCode(String accountCode);

  Observable<AssetAccountEntity> findByClientId(UUID clientId);
}
