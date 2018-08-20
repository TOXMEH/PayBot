package ru.arsenal.service;

import ru.arsenal.model.Bot;

/**
 * Created by Anton Nesudimov on 27.09.2016.
 */
public interface BotService {
    boolean addBot(String botName, String botToken, String providerId);

    Bot getBotByProviderId(String providerId);

    String getNameOfService(String providerId);

    String getBotName(String providerId);

    String getBotToken(String providerId);

//    TelegramBot getRealization(Bot bot);
}
