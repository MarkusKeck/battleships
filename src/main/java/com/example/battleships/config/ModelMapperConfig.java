package com.example.battleships.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    /**
     * Creates a ModelMapper which enables us to merge objects
     * This is mainly used to map the data transfer objects to the entities
     *
     * @return ModelMapper
     */

    @Bean
    public org.modelmapper.ModelMapper modelMapper() {
        org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        return modelMapper;
    }

}
