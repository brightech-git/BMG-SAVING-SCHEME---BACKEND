package com.example.VTM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class VtmApplication {
	public static void main(String[] args) {
		SpringApplication.run(VtmApplication.class, args);
	}
}