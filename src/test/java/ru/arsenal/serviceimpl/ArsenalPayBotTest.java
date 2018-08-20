package ru.arsenal.serviceimpl;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import ru.arsenal.config.SpringConfiguration;
import ru.arsenal.model.User;
import ru.arsenal.service.BotService;
import ru.arsenal.service.PayService;
import ru.arsenal.service.UserService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static ru.arsenal.model.states.*;

//import org.junit.Test;

/**
 * Created by Anton Nesudimov on 26.10.2016.
 */
@ContextConfiguration(classes = {SpringConfiguration.class, ArsenalPayBot.class})
class ArsenalPayBotTest {

    private UserService userService = mock(UserServiceImpl.class);
    private BotService botService = mock(BotServiceImpl.class);
    private Long chatId = Long.valueOf("123");
    private Message message = mock(Message.class);
    private Update update = mock(Update.class);
    private ArsenalPayBot bot = spy(ArsenalPayBot.class);
    private PayService payService = mock(ArsenalPayService.class);
    private String contractId = "123";
    private User mockUser = new User();

    @Test
    void assigningAccountSuccess() {
        mockUser.setState(waitForContractNumber);
        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn(contractId);

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        when(payService.contractNumberExists(any())).thenReturn(true);
        bot.payService = payService;

        doNothing().when(bot).shipMessage(any());

        bot.onUpdateReceived(update);

        verify(bot).bindContract(any(), any());
        verify(payService).contractNumberExists(any());
        verify(userService).bindChatIdOnContractId(chatId.toString(), contractId);
    }

    @Test
    public void PaymentWithAssignedMobilePhone() throws Exception {
        mockUser.setMsisdn("123");
        mockUser.setHavingCard(false);

        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("Оплата");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                ReplyKeyboard replyKeyboard = argument.getReplyMarkup();
                assertTrue(replyKeyboard.toString().contains("Со счета мобильного телефона"));
                assertFalse(replyKeyboard.toString().contains("С карты"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);
    }

    @Test
    public void PaymentWithWrongSum() throws Exception {
        mockUser.setMsisdn("123");
        mockUser.setHavingCard(false);

        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("Оплата").thenReturn("Со счета мобильного телефона").thenReturn("asd");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doNothing().when(bot).shipMessage(any());


        bot.onUpdateReceived(update);
        bot.onUpdateReceived(update);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                assertTrue(argument.getText().contains("Вы ввели некорректное значение"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);
    }

    @Test
    public void completePayment() throws Exception {
        mockUser.setMsisdn("123");
        mockUser.setHavingCard(false);

        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("Оплата").thenReturn("Со счета мобильного телефона").thenReturn("100");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doNothing().when(bot).shipMessage(any());


        bot.onUpdateReceived(update);
        bot.onUpdateReceived(update);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                assertTrue(argument.getText().contains("Ваш платеж c номером"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);
    }

    @Test
    public void checkStatusOfPaymentWithPassedOnReturn() throws Exception {
        mockUser.setMsisdn("123");
        mockUser.setHavingCard(false);
        mockUser.setState(waitPaymentForProceeding);

        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("Проверить статус платежа");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doNothing().when(bot).shipMessage(any());

        when(payService.getStatusOfPayment(any())).thenReturn("passed");
        bot.payService = payService;

//        bot.onUpdateReceived(update);
//        bot.onUpdateReceived(update);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                assertTrue(argument.getText().contains("passed"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);

        verify(bot).checkStatusOfLastPayment(any());
        verify(userService).setState(any(), eq(none));

    }

    @Test
    public void checkStatusOfPaymentWithoutPassedOnReturn() throws Exception {
        mockUser.setMsisdn("123");
        mockUser.setHavingCard(false);
        mockUser.setState(waitPaymentForProceeding);

        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("Проверить статус платежа");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doNothing().when(bot).shipMessage(any());

        when(payService.getStatusOfPayment(any())).thenReturn("in process");
        bot.payService = payService;

//        bot.onUpdateReceived(update);
//        bot.onUpdateReceived(update);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                assertFalse(argument.getText().contains("passed"));
                assertTrue(argument.getText().contains("in process"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);

        verify(bot).checkStatusOfLastPayment(any());
        verify(userService, times(0)).setState(any(), eq(none));

    }

    @Test
    public void getBalance() throws Exception {
        mockUser.setMsisdn("123");
        mockUser.setAccount("123");
        mockUser.setHavingCard(true);

        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("Баланс");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doNothing().when(bot).shipMessage(any());

        when(payService.getBalance(any())).thenReturn(123);
        bot.payService = payService;

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                assertTrue(argument.getText().contains("123"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);

        verify(bot).getBalance(any());

    }

    @Test
    public void PaymentWithAssignedAccountWithoutAssignedCard() throws Exception {
        mockUser.setAccount("123");
        mockUser.setHavingCard(false);

        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("/help");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                ReplyKeyboard replyKeyboard = argument.getReplyMarkup();
                assertTrue(replyKeyboard.toString().contains("Баланс"));
                assertTrue(replyKeyboard.toString().contains("Конфигурация"));
                assertFalse(replyKeyboard.toString().contains("Оплата"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);
    }

    @Test
    public void PaymentWithAssignedAccountWithAssignedCard() throws Exception {
        mockUser.setAccount("123");
        mockUser.setHavingCard(true);

        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("/help").thenReturn("Оплата");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                ReplyKeyboard replyKeyboard = argument.getReplyMarkup();
                assertTrue(replyKeyboard.toString().contains("Баланс"));
                assertTrue(replyKeyboard.toString().contains("Конфигурация"));
                assertTrue(replyKeyboard.toString().contains("Оплата"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                ReplyKeyboard replyKeyboard = argument.getReplyMarkup();
                assertFalse(replyKeyboard.toString().contains("Со счета мобильного телефона"));
                assertTrue(replyKeyboard.toString().contains("С карты"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);
    }

    @Test
    public void PaymentWithAssignedAccountWithAssignedCardAndMSISDN() throws Exception {
        mockUser.setAccount("123");
        mockUser.setMsisdn("123");
        mockUser.setHavingCard(true);

        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("/help").thenReturn("Оплата");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doNothing().when(bot).shipMessage(any());

        bot.onUpdateReceived(update);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                ReplyKeyboard replyKeyboard = argument.getReplyMarkup();
                assertTrue(replyKeyboard.toString().contains("Со счета мобильного телефона"));
                assertTrue(replyKeyboard.toString().contains("С карты"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);
    }

    @Test
    void assigningAccountSuccessWithAssignedCard() {
        mockUser.setState(waitForContractNumber);
        mockUser.setAccount("123");
        mockUser.setHavingCard(true);
        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn(contractId).thenReturn("Конфигурация");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        when(payService.contractNumberExists(any())).thenReturn(true);
        bot.payService = payService;

        doNothing().when(bot).shipMessage(any());

        bot.onUpdateReceived(update);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                ReplyKeyboard replyKeyboard = argument.getReplyMarkup();
                assertTrue(replyKeyboard.toString().contains("Передать номер телефона"));
                assertFalse(replyKeyboard.toString().contains("Передать номер договора"));
                assertFalse(replyKeyboard.toString().contains("Синхронизировать карту с аккаунтом"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);
    }

    @Test
    void assigningAccountSuccessWithoutAssignedCreditCard() {
        mockUser.setState(waitForContractNumber);
        mockUser.setAccount("123");
        mockUser.setHavingCard(false);
        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn(contractId).thenReturn("Конфигурация");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        when(payService.contractNumberExists(any())).thenReturn(true);
        bot.payService = payService;

        doNothing().when(bot).shipMessage(any());

        bot.onUpdateReceived(update);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SendMessage argument = invocation.getArgument(0);
                ReplyKeyboard replyKeyboard = argument.getReplyMarkup();
                assertTrue(replyKeyboard.toString().contains("Передать номер телефона"));
                assertFalse(replyKeyboard.toString().contains("Передать номер договора"));
                assertTrue(replyKeyboard.toString().contains("Синхронизировать карту с аккаунтом"));

                return null;
            }
        }).when(bot).shipMessage(any());

        bot.onUpdateReceived(update);
    }

    @Test
    void assigningAccountFail() {
        mockUser.setState(waitForContractNumber);
        when(userService.getUserByChatId(any())).thenReturn(mockUser);
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn(contractId);

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        when(payService.contractNumberExists(any())).thenReturn(false);
        bot.payService = payService;

        doNothing().when(bot).shipMessage(any());

        bot.onUpdateReceived(update);

        verify(bot).bindContract(any(), any());
        verify(payService).contractNumberExists(any());
        verify(userService, times(0)).bindChatIdOnContractId(chatId.toString(), contractId);
    }

    @Test
    void assigningAccountMessage() {
        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("/start").thenReturn("Передать номер договора");

        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doNothing().when(bot).shipMessage(any());

        bot.onUpdateReceived(update);
        bot.onUpdateReceived(update);

        verify(bot).getContractNumber(any());
        verify(userService).setState(any(), eq(waitForContractNumber));
    }

    @Test
    public void addingBot() {
        //отправляем сообщение с текстом "/start", после этого должен вызываться метод requestContact,
        // который в свою очередь вызывает метод createUserWithChatIdAndBot

        bot.userService = userService;

        when(botService.getNameOfService(any())).thenReturn("1");

        bot.botService = botService;

        when(message.getText()).thenReturn("/start");
        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);

        when(update.getMessage()).thenReturn(message);

        doNothing().when(bot).shipMessage(any());

        bot.onUpdateReceived(update);

        verify(bot).requestContact(any());
        verify(userService).createUserWithChatIdAndBot(any(), any());
    }

}