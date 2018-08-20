package ru.arsenal.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.arsenal.model.Bot;
import ru.arsenal.model.User;
import ru.arsenal.model.states;
import ru.arsenal.repository.UserRepository;
import ru.arsenal.service.BotService;
import ru.arsenal.service.PayService;
import ru.arsenal.service.UserService;

import java.util.List;

import static ru.arsenal.model.states.none;

/**
 * Created by Anton Nesudimov on 27.09.2016.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PayService payService;
    @Autowired
    private BotService botService;

    @Override
    public void bindChatIdOnContractId(String chatId, String contractId) {
        User user = userRepository.getUserByChatId(chatId);
        user.setAccount(contractId);
//        if (account.equals("123"))
        synchronizeCard(user);
        userRepository.save(user);
    }

    @Override
    public void bindChatIdOnPhoneNumber(String chatId, String phoneNumber) {
        User user = getUserByChatId(chatId);
        user.setMsisdn(phoneNumber);
        String contractId = user.getAccount();
        if (contractId != null) {
            synchronizeCard(user);
        }
        userRepository.save(user);
    }

    @Override
    public User getUserByChatId(String chatId) {
        return userRepository.getUserByChatId(chatId);
    }

    @Override
    public void setState(User user, states state) {
        user.setState(state);
        userRepository.save(user);
    }

    @Override
    public void createUserWithChatIdAndBot(String chatId, String providerId) {
        User user = new User();
        user.setChatId(chatId);
        user.setState(none);
        user.setBot(botService.getBotByProviderId(providerId));
        userRepository.save(user);
    }

    @Override
    public boolean synchronizeCard(User user) {
        if (payService.hasUserCard(user.getAccount())) {
            user.setHavingCard(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public List<User> getUsersByBot(Bot bot) {
        return userRepository.getUsersByBot(bot);
    }
}
