/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetAccountRepository extends MongoRepository<AssetAccountEntity, String> {
  Optional<AssetAccountEntity> findByAccountCode(String accountCode);
}
