package com.github.feignclientutil.config;

import com.github.feignclientutil.requestmodel.RequestModelContract;
import com.github.feignclientutil.requestmodel.RequestModelBodyEncoder;
import feign.Contract;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;

import java.util.List;


public class RequestModelConfiguration {

    @Bean
    Encoder feignEncoder(ObjectProvider<HttpMessageConverters> converters) {
        return new RequestModelBodyEncoder(new SpringEncoder(converters));
    }

    @Bean
    Contract defaultContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors,
                                  @Qualifier("feignConversionService") ConversionService conversionService,
                                  FeignClientProperties properties) {
        Contract springMvcContract = new SpringMvcContract(annotatedParameterProcessors, conversionService, properties);
        return new RequestModelContract(springMvcContract);
    }
}
