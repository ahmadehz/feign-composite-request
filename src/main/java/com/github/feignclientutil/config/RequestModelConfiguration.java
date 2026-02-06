package com.github.feignclientutil.config;

import com.github.feignclientutil.binding.RequestModelFieldBinder;
import com.github.feignclientutil.feign.RequestModelBodyEncoder;
import com.github.feignclientutil.binding.RequestModelParameterProcessor;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

import java.util.List;


public class RequestModelConfiguration {

    @Bean
    Encoder feignEncoder(ObjectProvider<HttpMessageConverters> converters) {
        return new RequestModelBodyEncoder(new SpringEncoder(converters));
    }

    @Bean
    RequestModelParameterProcessor requestModelProcessor(List<RequestModelFieldBinder> requestModelFieldBinders) {
        return new RequestModelParameterProcessor(requestModelFieldBinders);
    }
}
