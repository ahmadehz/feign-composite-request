package io.openfeign.extensions.compositerequest.config;

import feign.Contract;
import io.openfeign.extensions.compositerequest.feign.CompositeRequestContract;
import io.openfeign.extensions.compositerequest.feign.CompositeRequestInvocationHandlerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;

import java.util.List;


public class CompositeRequestConfiguration {

    @Bean
    Contract compositeRequestContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors,
                                      @Qualifier("feignConversionService") ConversionService conversionService,
                                      FeignClientProperties properties) {
        SpringMvcContract springMvcContract = new SpringMvcContract(annotatedParameterProcessors, conversionService, properties);
        return new CompositeRequestContract(springMvcContract);
    }

    @Bean
    FeignBuilderCustomizer customizeBuilderInvocationHandlerFactory() {
        return builder -> builder.invocationHandlerFactory(new CompositeRequestInvocationHandlerFactory());
    }
}
