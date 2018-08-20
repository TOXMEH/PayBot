package ru.arsenal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.BotSession;
import ru.arsenal.model.Bot;
import ru.arsenal.repository.BotRepository;
import ru.arsenal.service.TelegramBot;
import ru.arsenal.serviceimpl.ArsenalPayBot;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;

@SpringBootApplication
@EnableSwagger2
@EnableAutoConfiguration
public class ArsenalPayBotApplication {

    public static HashMap<String, TelegramBot> botMap = new HashMap<>();

    @Autowired
    BotRepository botRepository;
    @Autowired
    private ArsenalPayBot telegramBot;
    private BotSession session;

    public static void main(String[] args) {
        SpringApplication.run(ArsenalPayBotApplication.class, args);
    }

    @PostConstruct
    public void start() {
        Bot bot = new Bot();


        botRepository.save(bot);

        botMap.put("3", telegramBot);

        TelegramBotsApi api = new TelegramBotsApi();
        try {
            session = api.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        if (session != null) {
            session.close();
        }
    }
}
