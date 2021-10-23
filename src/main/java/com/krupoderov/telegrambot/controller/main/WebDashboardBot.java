package com.krupoderov.telegrambot.controller.main;

import com.krupoderov.telegrambot.config.BotConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebDashboardBot {

    private final BotConfig botConfig;

    public WebDashboardBot(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @GetMapping(value = "/")
    public String hello() {
        return "Hello this " + botConfig.getBotUserName();
    }
}
