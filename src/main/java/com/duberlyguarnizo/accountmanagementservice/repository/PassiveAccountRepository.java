/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.repository;

import com.duberlyguarnizo.accountmanagementservice.domain.PassiveAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassiveAccountRepository extends MongoRepository<PassiveAccount, String> {
}
