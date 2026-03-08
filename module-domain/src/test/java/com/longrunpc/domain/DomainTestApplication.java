package com.longrunpc.domain;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.longrunpc.domain")
@EnableJpaRepositories(basePackages = "com.longrunpc.domain")
public class DomainTestApplication {
}