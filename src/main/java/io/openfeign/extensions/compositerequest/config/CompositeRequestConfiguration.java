package io.openfeign.extensions.compositerequest.config;

import io.openfeign.extensions.compositerequest.binding.CompositeRequestFieldBinder;
import io.openfeign.extensions.compositerequest.binding.CompositeRequestParameterProcessor;
import feign.codec.Encoder;
import io.openfeign.extensions.compositerequest.feign.CompositeRequestBodyEncoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

import java.util.List;


public class CompositeRequestConfiguration {

    @Bean
    Encoder feignEncoder(ObjectProvider<HttpMessageConverters> converters) {
        return new CompositeRequestBodyEncoder(new SpringEncoder(converters));
    }

    @Bean
    CompositeRequestParameterProcessor requestModelProcessor(List<CompositeRequestFieldBinder> compositeRequestFieldBinders) {
        return new CompositeRequestParameterProcessor(compositeRequestFieldBinders);
    }
}
