package ru.arsenal.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.arsenal.model.Bot;
import ru.arsenal.repository.BotRepository;
import ru.arsenal.service.BotService;

/**
 * Created by Anton Nesudimov on 27.09.2016.
 */
@Service
public class BotServiceImpl implements BotService {

    @Autowired
    BotRepository botRepository;

    @Override
    public boolean addBot(String botName, String botToken, String providerId) {
        return true;
    }

    @Override
    public Bot getBotByProviderId(String providerId) {
        return botRepository.getBotByProviderId(providerId);
    }

    @Override
    public String getNameOfService(String providerId) {
        Bot bot = botRepository.getBotByProviderId(providerId);
        return bot.getNameOfService();
    }

    @Override
    public String getBotName(String providerId) {
        Bot bot = botRepository.getBotByProviderId(providerId);
        return bot.getName();
    }

    @Override
    public String getBotToken(String providerId) {
        Bot bot = botRepository.getBotByProviderId(providerId);
        return bot.getToken();
    }

}
