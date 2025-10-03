package com.example.VTM.userAdministartion.config;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.VTM.userAdministartion.repository", // ✅ Path to UserRepository
        entityManagerFactoryRef = "thirdEntityManagerFactory",
        transactionManagerRef = "thirdTransactionManager"
)
public class ThirdDbJpaConfig {

    @Bean(name = "thirdEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean thirdEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("thirdDataSource") DataSource thirdDataSource) {

        return builder
                .dataSource(thirdDataSource)
                .packages("com.example.VTM.userAdministartion.entityOrDomain") // ✅ Path to User entity
                .persistenceUnit("third")
                .build();
    }

    @Bean(name = "thirdTransactionManager")
    public PlatformTransactionManager thirdTransactionManager(
            @Qualifier("thirdEntityManagerFactory") EntityManagerFactory thirdEntityManagerFactory) {

        return new JpaTransactionManager(thirdEntityManagerFactory);
    }
}
