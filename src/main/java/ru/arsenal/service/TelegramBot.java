package ru.arsenal.service;

import org.telegram.telegrambots.bots.ITelegramLongPollingBot;

/**
 * Created by Anton Nesudimov on 02.11.2016.
 */
public interface TelegramBot extends ITelegramLongPollingBot {
    void makeDistribution(String message);
}
