package ru.arsenal.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.arsenal.model.Bot;
import ru.arsenal.model.User;
import ru.arsenal.model.states;
import ru.arsenal.service.BotService;
import ru.arsenal.service.PayService;
import ru.arsenal.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNumeric;
import static ru.arsenal.model.states.*;

@Component
public class ArsenalPayBot extends TelegramLongPollingBot implements ru.arsenal.service.TelegramBot {

    @Autowired
    UserService userService;

    @Autowired
    BotService botService;

    @Autowired
    PayService payService;

    private String providerId = "3";
    private String numberOfPayment = "";

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
//
        if (payService == null) {
            System.out.println("arse");
        }
        if (botService == null) {
            System.out.println("bots");
        }
        if (userService == null) {
            System.out.println("users");
        }

        if (message != null) {
            String chatId = message.getChatId().toString();
            SendMessage sendMessageRequest = new SendMessage();
            sendMessageRequest.enableHtml(true);
            sendMessageRequest.setChatId(chatId);
            if (message.hasText()) {

                String messageText = message.getText();
                switch (messageText) {
                    case "/start": {
                        requestContact(sendMessageRequest);
                        return;
                    }
                    case "Передать номер договора": {
                        getContractNumber(sendMessageRequest);
                        return;
                    }
                    case "Баланс": {
                        getBalance(sendMessageRequest);
                        return;
                    }
                    case "Оплата": {
                        makePayment(sendMessageRequest);
                        return;
                    }
                    case "Со счета мобильного телефона": {
                        askForAmountOfPaymentFromMobilePhoneAccount(sendMessageRequest);
                        return;
                    }
                    case "С карты": {
                        askForAmountOfPaymentFromCard(sendMessageRequest);
                        return;
                    }
                    case "Конфигурация": {
                        getConfiguration(sendMessageRequest);
                        return;
                    }
                    case "/help":
                    case "Вернуться":
                    case "Справка": {
                        showHelp(sendMessageRequest);
                        return;
                    }
                    case "Синхронизировать карту с аккаунтом": {
                        synchronizeCard(sendMessageRequest);
                        return;
                    }
                }
                if (messageText.contains("Проверить статус платежа")) {
                    checkStatusOfLastPayment(sendMessageRequest);
                }

                User user = userService.getUserByChatId(chatId);
                states state = user.getState();

                switch (state) {
                    case waitForContractNumber: {
                        bindContract(messageText, sendMessageRequest);
                        return;
                    }
                    case paymentFromMobilePhoneAccount: {
                        makePaymentFromMobilePhoneAccount(message, sendMessageRequest);
                        return;
                    }
                    case paymentFromCard: {
                        makePaymentFromCard(message, sendMessageRequest);
                        return;
                    }
                }

//                showHelp(sendMessageRequest);

            } else if (message.getContact() != null) {
                String phoneNumber = message.getContact().getPhoneNumber();
                bindNumber(phoneNumber, sendMessageRequest);
            }
        }
    }

    private void synchronizeCard(SendMessage sendMessageRequest) {
        SendMessage sendMessage;
        if (userService.synchronizeCard(userService.getUserByChatId(sendMessageRequest.getChatId()))) {
            sendMessage = sendMessageRequest.setText("Кредитная карта была успешно синхронизирована с аккаунтом");
        } else {
            sendMessage = sendMessageRequest.setText("Синхронизация карты не удалась");
        }

        User user = userService.getUserByChatId(sendMessageRequest.getChatId());
        if ((user.getAccount() == null) || (user.getMsisdn() == null) || (!user.isHavingCard())) {
            sendMessage.setReplyMarkup(getConfigurationKeyboard(sendMessageRequest));
        } else {
            sendMessage.setReplyMarkup(getWorkingKeyboard(sendMessageRequest));
        }

        shipMessage(sendMessage);
    }

    private void getConfiguration(SendMessage sendMessageRequest) {
        SendMessage sendMessage = sendMessageRequest.setText("Вы можете передать новые параметры для расшрения возможностей бота");

        sendMessage.setReplyMarkup(getConfigurationKeyboard(sendMessageRequest));

        shipMessage(sendMessage);
    }

    private ReplyKeyboard getConfigurationKeyboard(SendMessage sendMessageRequest) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        User user = userService.getUserByChatId(sendMessageRequest.getChatId());

        if (user.getMsisdn() == null) {
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            KeyboardButton getNumber = new KeyboardButton();
            getNumber.setRequestContact(true);
            getNumber.setText("Передать номер телефона");

            keyboardFirstRow.add(getNumber);
            keyboard.add(keyboardFirstRow);
        } else if (user.getAccount() == null) {
            KeyboardRow keyboardSecondRow = new KeyboardRow();
            KeyboardButton getContractNumber = new KeyboardButton();
            getContractNumber.setText("Передать номер договора");

            keyboardSecondRow.add(getContractNumber);
            keyboard.add(keyboardSecondRow);
        }

        if ((user.getAccount() != null) && (!user.isHavingCard())) {
            KeyboardRow keyboardThirdRow = new KeyboardRow();
            KeyboardButton getCardNumber = new KeyboardButton();
            getCardNumber.setText("Синхронизировать карту с аккаунтом");

            keyboardThirdRow.add(getCardNumber);
            keyboard.add(keyboardThirdRow);
        }

        KeyboardRow keyboardFourthRow = new KeyboardRow();
        KeyboardButton goBack = new KeyboardButton();
        goBack.setText("Вернуться");

        keyboardFourthRow.add(goBack);
        keyboard.add(keyboardFourthRow);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;

    }

    private void makePaymentFromCard(Message message, SendMessage sendMessageRequest) {
        String chatId = sendMessageRequest.getChatId();

        String sum = message.getText();

        if (isNumeric(sum)) {
            int sumInt = Integer.parseInt(sum);

            User user = userService.getUserByChatId(chatId);

            Pair<String, String> res = payService.makePaymentFromCard(user.getAccount(), sumInt, providerId);

            acceptPaymentForProceeding(sendMessageRequest, res);
        } else {
            SendMessage sendMessage = sendMessageRequest.setText("Вы ввели некорректное значение. Пожалуйста, введите сумму платежа:");

            shipMessage(sendMessage);
        }
    }

    private void acceptPaymentForProceeding(SendMessage sendMessageRequest, Pair<String, String> res) {

        numberOfPayment = res.getFirst();
        String statusOfPayment = res.getSecond();

        User user = userService.getUserByChatId(sendMessageRequest.getChatId());

        userService.setState(user, states.waitPaymentForProceeding);

        SendMessage sendMessage = sendMessageRequest.setText("Ваш платеж c номером " + numberOfPayment + " находится в состоянии: " + statusOfPayment);

        sendMessage.setReplyMarkup(getWorkingKeyboard(sendMessageRequest));

        shipMessage(sendMessage);

    }

    private void makePaymentFromMobilePhoneAccount(Message message, SendMessage sendMessageRequest) {
        String chatId = sendMessageRequest.getChatId();

        String sum = message.getText();

        if (isNumeric(sum)) {
            int sumInt = Integer.parseInt(sum);

            User user = userService.getUserByChatId(chatId);

            Pair<String, String> res = payService.makePaymentFromMobilePhoneAccount(user.getMsisdn(), sumInt, providerId);

            acceptPaymentForProceeding(sendMessageRequest, res);
        } else {
            SendMessage sendMessage = sendMessageRequest.setText("Вы ввели некорректное значение. Пожалуйста, введите сумму платежа:");

            shipMessage(sendMessage);
        }
    }

    private void askForAmountOfPaymentFromCard(SendMessage sendMessageRequest) {
        User user = userService.getUserByChatId(sendMessageRequest.getChatId());
        userService.setState(user, paymentFromCard);

        askForAmountOFPayment(sendMessageRequest);
    }

    private void askForAmountOfPaymentFromMobilePhoneAccount(SendMessage sendMessageRequest) {
        User user = userService.getUserByChatId(sendMessageRequest.getChatId());
        userService.setState(user, paymentFromMobilePhoneAccount);

        askForAmountOFPayment(sendMessageRequest);
    }

    private void askForAmountOFPayment(SendMessage sendMessageRequest) {
        SendMessage sendMessage = sendMessageRequest.setText("Пожалуйста, введите сумму платежа:");

        sendMessage.setReplyMarkup(getAmountOfPaymentKeyboard());

        shipMessage(sendMessage);
    }

    private ReplyKeyboard getAmountOfPaymentKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        Integer availableSums[] = {100, 200, 300, 500, 700, 1000};
        for (Integer sum : availableSums) {
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            KeyboardButton button = new KeyboardButton();
            button.setText(sum.toString());
            keyboardFirstRow.add(button);
            keyboard.add(keyboardFirstRow);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    private void makePayment(SendMessage sendMessageRequest) {
        String possibleWaysOfPayment = "";

        User user = userService.getUserByChatId(sendMessageRequest.getChatId());

        boolean hasCard = false;
        boolean hasPhoneNumber = false;

        if (user.getMsisdn() != null) {
            possibleWaysOfPayment += "Для оплаты с номера мобильного телефона, нажмите на кнопку \"Со счета мобильного телефона\"\n";
            hasPhoneNumber = true;
        } else {
            possibleWaysOfPayment += "Для оплаты с номера мобильного телефона требуется передать номер телефона\n";
        }
        if (user.isHavingCard()) {
            possibleWaysOfPayment += "Для оплаты с карты, нажмите на кнопку \"С карты\"\n";
            hasCard = true;
        } else {
            possibleWaysOfPayment += "Для оплаты с карты, необходимо прикрепить карту в личном кабинете " + getBotUsername() + "\n";
        }
        SendMessage sendMessage = sendMessageRequest.setText(possibleWaysOfPayment);

        userService.setState(user, none);

        sendMessage.setReplyMarkup(getPaymentKeyboard(hasPhoneNumber, hasCard));

        shipMessage(sendMessageRequest);
    }

    private ReplyKeyboard getPaymentKeyboard(boolean hasPhoneNumber, boolean hasCard) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);

        List<KeyboardRow> keyboard = new ArrayList<>();


        if (hasPhoneNumber) {
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            KeyboardButton fromNumber = new KeyboardButton();
            fromNumber.setText("Со счета мобильного телефона");

            keyboardFirstRow.add(fromNumber);
            keyboard.add(keyboardFirstRow);
        }
        if (hasCard) {
            KeyboardRow keyboardSecondRow = new KeyboardRow();
            KeyboardButton fromCard = new KeyboardButton();
            fromCard.setText("С карты");

            keyboardSecondRow.add(fromCard);
            keyboard.add(keyboardSecondRow);

        }

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    void checkStatusOfLastPayment(SendMessage sendMessageRequest) {
        String chatId = sendMessageRequest.getChatId();
        User user = userService.getUserByChatId(chatId);

//        String lastPaymentId = payService.getInfoAboutLastPaymentOfUser(user).getFirst();

        String statusOfLastPayment = payService.getStatusOfPayment(numberOfPayment);

        if (statusOfLastPayment.equals("passed")) {
            userService.setState(user, none);
        }

        String sendingText = "Ваш платеж под номером " + numberOfPayment + " находится в состоянии " + statusOfLastPayment;
//        if (statusOfLastPayment) {
//            sendingText += " успешно прошел";
//        } else {
//            sendingText += " еще не прошел";
//        }
        SendMessage sendMessage = sendMessageRequest.setText(sendingText);

        sendMessage.setReplyMarkup(getWorkingKeyboard(sendMessageRequest));

        shipMessage(sendMessageRequest);
    }

    void getBalance(SendMessage sendMessageRequest) {
        String chatId = sendMessageRequest.getChatId();
        User user = userService.getUserByChatId(chatId);

        int balance = payService.getBalance(user.getAccount());

        SendMessage sendMessage = sendMessageRequest.setText("Ваш баланс равен " + balance + ".");

        sendMessage.setReplyMarkup(getWorkingKeyboard(sendMessageRequest));

        shipMessage(sendMessageRequest);
    }

    void bindContract(String messageText, SendMessage sendMessageRequest) {
        String contractId = messageText;

        if (!payService.contractNumberExists(contractId)) {
            SendMessage sendMessage = sendMessageRequest.setText("Пользователь с таким номером договора не найден.\n" +
                    "Пожалуйста, введите номер договора еще раз.");
            sendMessage.setReplyMarkup(getNumberKeyboard());

            shipMessage(sendMessageRequest);
            User userInTelegram = userService.getUserByChatId(sendMessageRequest.getChatId());
            User user = userService.getUserByChatId(sendMessageRequest.getChatId());
            userService.setState(user, waitForContractNumber);
        } else {
            String chatId = sendMessageRequest.getChatId();
            userService.bindChatIdOnContractId(chatId, contractId);
            successfulAuthorization(sendMessageRequest);
        }
    }

    private void successfulAuthorization(SendMessage sendMessageRequest) {
        SendMessage sendMessage = sendMessageRequest.setText("Ваш аккаунт в Telegram успешно связан " +
                "с аккаунтом в " + botService.getNameOfService(providerId));

        shipMessage(sendMessageRequest);

        User user = userService.getUserByChatId(sendMessageRequest.getChatId());
        userService.setState(user, none);

        showHelp(sendMessageRequest);
    }

    private ReplyKeyboard getNumberKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        KeyboardButton getNumber = new KeyboardButton();
        getNumber.setRequestContact(true);
        getNumber.setText("Передать номер телефона");

        keyboardFirstRow.add(getNumber);

        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    void getContractNumber(SendMessage sendMessageRequest) {
        SendMessage sendMessage = sendMessageRequest.setText("Введите ваш номер договора");

        shipMessage(sendMessageRequest);

        User user = userService.getUserByChatId(sendMessageRequest.getChatId());
        userService.setState(user, waitForContractNumber);
    }

    void requestContact(SendMessage sendMessageRequest) {
        String chatId = sendMessageRequest.getChatId();
        userService.createUserWithChatIdAndBot(chatId, providerId);
        SendMessage sendMessage = sendMessageRequest.setText("Здравствуйте, для использования всех возможностей нашего бота вы можете " +
                "ввести номер телефона и номер договора в платежной системе " + botService.getNameOfService(providerId) + "\n" +
                "Для ввода номера телефона, нажмите на кнопку \"Передать номер телефона\"\n" +
                "Для ввода номера договора, нажмите на кнопку \"Передать номер договора\"");
        sendMessage.setReplyMarkup(getRequestKeyboard());

        shipMessage(sendMessageRequest);
    }

    void shipMessage(SendMessage sendMessageRequest) {
        try {
            sendMessage(sendMessageRequest);
//            System.out.println(sendMessageRequest.getText()+"\n");
        } catch (TelegramApiException e) {
//            Mailer.logger.error("\"TelegramApiException \ + e);
            System.out.println(e);
        }
    }

    private ReplyKeyboard getRequestKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        KeyboardButton getNumber = new KeyboardButton();
        getNumber.setRequestContact(true);
        getNumber.setText("Передать номер телефона");

        KeyboardButton getContractNumber = new KeyboardButton();
        getContractNumber.setText("Передать номер договора");

        keyboardFirstRow.add(getNumber);

        keyboardSecondRow.add(getContractNumber);

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;

    }

    private void bindNumber(String phoneNumber, SendMessage sendMessageRequest) {
        String chatId = sendMessageRequest.getChatId();
        userService.bindChatIdOnPhoneNumber(chatId, phoneNumber);
        successfulAuthorization(sendMessageRequest);
    }

    private ReplyKeyboard getContractKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        KeyboardButton getNumber = new KeyboardButton();
        getNumber.setText("Передать номер договора");

        keyboardFirstRow.add(getNumber);

        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }


    public String getBotUsername() {
        return botService.getBotName(providerId);
    }

    public String getBotToken() {
        return botService.getBotToken(providerId);
    }

    private void showHelp(SendMessage sendMessageRequest) {
        SendMessage sendMessage = sendMessageRequest.setText("Вы можете проверить Баланс- нажав на кнопку \"Баланс\" \n" +
                "Проверить статус последнего платежа, нажав на кнопку \"Проверить статус последнего платежа\"\n" +
                "Пополнить счет, нажав кнопку \"Оплата\"");
        sendMessage.setReplyMarkup(getWorkingKeyboard(sendMessageRequest));

        shipMessage(sendMessageRequest);
    }

    private ReplyKeyboard getWorkingKeyboard(SendMessage sendMessageRequest) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        User user = userService.getUserByChatId(sendMessageRequest.getChatId());

        if (user.getAccount() != null) {
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            KeyboardButton getBalance = new KeyboardButton();
            getBalance.setText("Баланс");
            keyboardFirstRow.add(getBalance);
            keyboard.add(keyboardFirstRow);


            if ((user.getMsisdn() != null) || (user.isHavingCard())) {
                KeyboardRow keyboardPaymentRow = new KeyboardRow();

                KeyboardButton pay = new KeyboardButton();
                pay.setText("Оплата");

                keyboardPaymentRow.add(pay);
                keyboard.add(keyboardPaymentRow);
            }
        }

        if (user.getState() == states.waitPaymentForProceeding) {
            KeyboardButton checkStatusOFLastPayment = new KeyboardButton();
            checkStatusOFLastPayment.setText("Проверить статус платежа №" + numberOfPayment);
            KeyboardRow keyboardSecondRow = new KeyboardRow();

            keyboardSecondRow.add(checkStatusOFLastPayment);
            keyboard.add(keyboardSecondRow);
        }

        if ((user.getAccount() == null) || (user.getMsisdn() == null) || (!user.isHavingCard())) {
            KeyboardRow keyboardThirdRow = new KeyboardRow();
            KeyboardButton config = new KeyboardButton();
            config.setText("Конфигурация");

            keyboardThirdRow.add(config);
            keyboard.add(keyboardThirdRow);
        }

        KeyboardRow keyboard4Row = new KeyboardRow();
        KeyboardButton help = new KeyboardButton();
        help.setText("Справка");

        keyboard4Row.add(help);
        keyboard.add(keyboard4Row);

        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }

    @Override
    public void makeDistribution(String message) {
        Bot bot = this.botService.getBotByProviderId(providerId);
        for (User user : userService.getUsersByBot(bot)) {
            String chatId = user.getChatId();
            SendMessage sendMessageRequest = new SendMessage();
            sendMessageRequest.enableHtml(true);
            sendMessageRequest.setChatId(chatId);

            SendMessage sendMessage = sendMessageRequest.setText(message);
//            sendMessage.setReplyMarkup(getWorkingKeyboard(sendMessageRequest));

            shipMessage(sendMessageRequest);
        }
    }
}