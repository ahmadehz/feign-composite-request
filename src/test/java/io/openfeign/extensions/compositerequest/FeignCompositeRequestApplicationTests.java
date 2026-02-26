package io.openfeign.extensions.compositerequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableFeignClients
class FeignCompositeRequestApplicationTests {

    @Test
    void contextLoads() {
    }

}
