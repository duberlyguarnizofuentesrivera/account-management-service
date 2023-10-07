/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice.domain.service;

import com.duberlyguarnizo.accountmanagementservice.domain.enums.ClientType;
import java.util.UUID;

public interface InterServiceOperations {

  public ClientType getClientType(UUID clientId);

}
