package ru.arsenal.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.arsenal.model.Bot;
import ru.arsenal.model.User;
import ru.arsenal.repository.BotRepository;
import ru.arsenal.repository.UserRepository;
import ru.arsenal.service.BotService;
import ru.arsenal.service.TelegramBot;
import ru.arsenal.view.BotView;

import java.util.ArrayList;
import java.util.List;

import static ru.arsenal.ArsenalPayBotApplication.botMap;

/**
 * Created by Anton Nesudimov on 01.11.2016.
 */
@EnableWebMvc
@RestController
@RequestMapping("/api/manage")
@Api(value = "BotManagement", description = "merchant API  for managing bot")
public class BotManagement {

    @Autowired
    BotService botService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BotRepository botRepository;

    @ApiOperation(value = "make distribition", notes = "make distribution to all bot users", tags = {"Bot management"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 404, message = "No bot with such providerId registered")})
    @RequestMapping(value = "/distribute-{providerId}", method = RequestMethod.POST)
    public void makeDistribution(@ApiParam(value = "provider ID", required = true) @PathVariable String providerId, @ApiParam(value = "message for distribution", required = true) @RequestParam("message") String message) {
        TelegramBot bot;
        bot = botMap.get(providerId);
        bot.makeDistribution(message);
    }

    @ApiOperation(value = "bot users", notes = "returns users belonging to bot ", response = User.class, responseContainer = "List", tags = {"Bot management"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "", response = User.class),
            @ApiResponse(code = 404, message = "")})
    @RequestMapping(value = "/users-{providerId}", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getUsers(@ApiParam(value = "provider ID", required = true) @PathVariable String providerId) {
        Bot bot = botRepository.getBotByProviderId(providerId);
        if (bot == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            List<String> usersInfo = new ArrayList<>();
            for (User user : bot.getUsers()) {
                String userInfo = user.getChatId();
                usersInfo.add(userInfo);
            }
            return new ResponseEntity<>(usersInfo, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "bot info", notes = "returns info about bot ", response = Bot.class, responseContainer = "List", tags = {"Bot management"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "", response = Bot.class),
            @ApiResponse(code = 404, message = "")})
    @RequestMapping(value = "/bot-{providerId}", method = RequestMethod.GET)
    public ResponseEntity<BotView> getBotInfo(@ApiParam(value = "provider ID", required = true) @PathVariable String providerId) {
        Bot bot = botRepository.getBotByProviderId(providerId);
        if (bot == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new BotView(bot), HttpStatus.OK);
        }
    }
}
