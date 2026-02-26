package io.openfeign.extensions.compositerequest.config;

import feign.Contract;
import io.openfeign.extensions.compositerequest.feign.CompositeRequestContract;
import io.openfeign.extensions.compositerequest.feign.CompositeRequestInvocationHandlerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.util.List;


public class CompositeRequestConfiguration {

    @Bean
    Contract compositeRequestContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors,
                                      @Qualifier("feignConversionService") ObjectProvider<ConversionService> conversionService,
                                      ObjectProvider<FeignClientProperties> properties) {
        ConversionService service = conversionService.getIfAvailable(DefaultFormattingConversionService::new);
        SpringMvcContract springMvcContract = new SpringMvcContract(annotatedParameterProcessors, service, properties.getIfAvailable());
        return new CompositeRequestContract(springMvcContract);
    }

    @Bean
    FeignBuilderCustomizer customizeBuilderInvocationHandlerFactory() {
        return builder -> builder.invocationHandlerFactory(new CompositeRequestInvocationHandlerFactory());
    }
}
