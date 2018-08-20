package ru.arsenal.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.arsenal.model.Bot;

/**
 * Created by Anton Nesudimov on 27.09.2016.
 */
@Repository
@Transactional
public interface BotRepository extends CrudRepository<Bot, String> {
//    String getNameOfServiceByProviderId(String providerId);
//
//    String getUsernameByProviderId(String providerId);
//
//    String getTokenByProviderId(String providerId);

    Bot getBotByProviderId(String providerId);
}
