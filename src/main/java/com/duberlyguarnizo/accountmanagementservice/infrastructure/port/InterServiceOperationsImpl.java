/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.infrastructure.port;

import com.duberlyguarnizo.accountmanagementservice.domain.enums.ClientType;
import com.duberlyguarnizo.accountmanagementservice.domain.service.InterServiceOperations;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class InterServiceOperationsImpl implements InterServiceOperations {
  @Override
  public ClientType getClientType(UUID clientId) {
    return null;
  }

}
