package ai.sparklabinc.d1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class D1CoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(D1CoreApplication.class, args);
    }
}
