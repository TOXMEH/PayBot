//package ru.arsenal.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import ru.arsenal.service.BotService;
//
///**
// * Created by Anton Nesudimov on 06.09.2016.
// */
//
//@EnableWebMvc
//@RestController
//@RequestMapping("/api")
//public class AddBot {
//
//    @Autowired
//    private BotService botService;
//
//    @RequestMapping(value = "/", method = RequestMethod.POST)
//    public HttpStatus createTask(@RequestParam("botName") String botName, @RequestParam("botToken") String botToken, @RequestParam("providerId") String providerId) {
//
//        if (botService.addBot(botName, botToken, providerId)) {
//            return HttpStatus.OK;
//        } else {
//            return HttpStatus.NOT_ACCEPTABLE;
//        }
//    }
//    //todo @ExceptionHandler in @AbstractController
//}
