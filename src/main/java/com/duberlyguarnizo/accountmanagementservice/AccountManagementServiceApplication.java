/*
 * Copyright (c) 2023. Code by Duberly Guarnizo <duberlygfr@gmail.com>.
 */

package com.duberlyguarnizo.accountmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * The main class for the Account Management Service application.
 * This class is responsible for starting the application and enabling Eureka
 * client and Mongo repositories.
 */
@SpringBootApplication
@EnableEurekaClient
@EnableWebFlux
@EnableReactiveMongoRepositories
public class AccountManagementServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccountManagementServiceApplication.class, args);
  }

}
