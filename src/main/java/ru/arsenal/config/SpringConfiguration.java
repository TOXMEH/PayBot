package ru.arsenal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.arsenal.service.BotService;
import ru.arsenal.service.PayService;
import ru.arsenal.service.UserService;
import ru.arsenal.serviceimpl.ArsenalPayService;
import ru.arsenal.serviceimpl.BotServiceImpl;
import ru.arsenal.serviceimpl.UserServiceImpl;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Created by Anton Nesudimov on 29.09.2016.
 */
@Configuration
public class SpringConfiguration {
    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

    @Bean
    public BotService botService() {
        return new BotServiceImpl();
    }

    @Bean
    public PayService payService() {
        return new ArsenalPayService();
    }

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false);
    }
}
