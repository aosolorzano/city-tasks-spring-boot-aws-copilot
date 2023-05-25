package com.hiperium.city.tasks.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hiperium.city.tasks.api.config.hints.QuartzHints;
import com.hiperium.city.tasks.api.config.hints.ResourceBundleHints;
import com.hiperium.city.tasks.api.dto.ErrorDetailsDto;
import com.hiperium.city.tasks.api.logger.HiperiumLogger;
import com.hiperium.city.tasks.api.scheduler.execution.JobExecution;
import com.hiperium.city.tasks.api.utils.PropertiesLoaderUtil;
import com.hiperium.city.tasks.api.vo.AuroraSecretsVo;
import lombok.SneakyThrows;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EnableTransactionManagement
@ImportRuntimeHints({QuartzHints.class, ResourceBundleHints.class})
@RegisterReflectionForBinding({AuroraSecretsVo.class, JobExecution.class, ErrorDetailsDto.class})
public class TasksApplication {

    private static final HiperiumLogger LOGGER = HiperiumLogger.getLogger(TasksApplication.class);

    @SneakyThrows(JsonProcessingException.class)
    public static void main(String[] args) {
        LOGGER.info("main() - BEGIN");
        PropertiesLoaderUtil.loadProperties();
        SpringApplication.run(TasksApplication.class, args);
        LOGGER.info("main() - END");
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding(StandardCharsets.ISO_8859_1.name());
        return messageSource;
    }
}
