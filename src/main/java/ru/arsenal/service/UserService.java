package ru.arsenal.service;

import ru.arsenal.model.Bot;
import ru.arsenal.model.User;
import ru.arsenal.model.states;

import java.util.List;

/**
 * Created by Anton Nesudimov on 27.09.2016.
 */
public interface UserService {
//    User getUserByAccount(String account);
//
//    User getUserByMsisdn(String msisdn);

    void bindChatIdOnContractId(String chatId, String contractId);

    void bindChatIdOnPhoneNumber(String chatId, String phoneNumber);

    User getUserByChatId(String chatId);

    void setState(User user, states state);

//    void createUserWithChatIdAndBot(String chatId);

    void createUserWithChatIdAndBot(String chatId, String providerId);

    boolean synchronizeCard(User userByChatId);

    List<User> getUsersByBot(Bot bot);
}
