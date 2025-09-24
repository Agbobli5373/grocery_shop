package com.groceryshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@SpringBootApplication
@Modulith
public class GroceryShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroceryShopApplication.class, args);
    }
}
