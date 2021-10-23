package com.krupoderov.telegrambot.service;

import com.krupoderov.telegrambot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/* Basic functionality for interacting with Telegram */
@Component
@Slf4j
public class BotService extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public BotService(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    //Accepts and processes messages received in private messages or in the channel where the bot administrator is located
    public void onUpdateReceived(Update update) {
        update.getUpdateId();
        SendMessage.SendMessageBuilder builder = SendMessage.builder();
        String messageText;
        String chatId;
        if (update.getMessage() != null) {
            chatId = update.getMessage().getChatId().toString();
            builder.chatId(chatId);
            messageText = update.getMessage().getText();
        } else {
            chatId = update.getChannelPost().getChatId().toString();
            builder.chatId(chatId);
            messageText = update.getChannelPost().getText();
        }

        if (messageText.contains("/hello")) {
            builder.text("Привет");
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }

        if (messageText.contains("/chartId")) {
            builder.text("ID канала: " + chatId);
            try {
                execute(builder.build());
            } catch (TelegramApiException e) {
                log.debug(e.toString());
            }
        }
    }

    //Return bot name
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    //Return bot token
    public String getBotToken() {
        return botConfig.getToken();
    }
}
