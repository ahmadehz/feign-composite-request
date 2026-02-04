package com.github.feignclientutil.config;

import com.github.feignclientutil.requestmodel.RequestModelBodyEncoder;
import com.github.feignclientutil.requestmodel.RequestModelProcessor;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;


public class RequestModelConfiguration {

    @Bean
    Encoder feignEncoder(ObjectProvider<HttpMessageConverters> converters) {
        return new RequestModelBodyEncoder(new SpringEncoder(converters));
    }

    @Bean
    AnnotatedParameterProcessor requestModelProcessor() {
        return new RequestModelProcessor();
    }
}
