package io.openfeign.extensions.compositerequest;

import io.openfeign.extensions.compositerequest.client.FeignClientExample;
import io.openfeign.extensions.compositerequest.dto.RequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
@EnableFeignClients
public class FeignCompositeRequestApplication implements ApplicationRunner {

    @Autowired
    FeignClientExample feignClientExample;
    public static void main(String[] args) {
        SpringApplication.run(FeignCompositeRequestApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(FeignClientExample feignClientExample) {
//        feignClientExample.post(new RequestDto());
        return args -> {};
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        feignClientExample.post(new RequestDto());
        feignClientExample.postQueryMap("{}", Map.of("query", "queryValue"));
        feignClientExample.post(new RequestDto());
    }

//    @Bean
//    Contract feignContract() {
//        return new SpringMvcContract();
//    }
}
