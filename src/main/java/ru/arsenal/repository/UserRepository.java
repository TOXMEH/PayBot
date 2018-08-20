package ru.arsenal.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.arsenal.model.Bot;
import ru.arsenal.model.User;

import java.util.List;

/**
 * Created by Anton Nesudimov on 27.09.2016.
 */
@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, String> {
    User getUserByAccount(String account);

    User getUserByMsisdn(String msisdn);

    User getUserByChatId(String chatId);

    List<User> getUsersByBot(Bot bot);
}
