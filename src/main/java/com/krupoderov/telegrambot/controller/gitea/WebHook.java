package com.krupoderov.telegrambot.controller.gitea;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.krupoderov.telegrambot.model.Commit;
import com.krupoderov.telegrambot.model.GiteaWebHook;
import com.krupoderov.telegrambot.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RestController
@RequestMapping("/api/public/gitea")
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class WebHook {

    private final BotService botService;

    //The channel to which we will send notifications
    @Value("${chartId}")
    String chartId;

    /* A secret key that will come to the inside of JSON from Gitea,
       so that unauthorized people do not have access to the bot,
       since the API is public and does not have authorization */
    @Value("${secret}")
    String secret;

    @PostMapping(value = "/webhook")
    public ResponseEntity<?> webhook(@RequestBody String json){
        Gson gson = new Gson();
        GiteaWebHook giteaWebHook = null;
        try {
            giteaWebHook = gson.fromJson(json, GiteaWebHook.class);
        } catch (JsonSyntaxException e) {
            log.error(e.toString());
            return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
        }
        if (validationWebHookContent(giteaWebHook)) {
            SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder();
            messageBuilder.chatId(chartId);

            messageBuilder.parseMode(ParseMode.HTML);
            StringBuilder builder = new StringBuilder();
            builder.append("<b>Repository</b> : ").append(giteaWebHook.getRepository().getName()).append("\n");
            for (Commit commit : giteaWebHook.getCommits()) {
                builder.append("<b>Author</b> : ").append(commit.getAuthor().getName()).append("\n");
                builder.append("<b>Comment</b> : ").append(commit.getMessage()).append("\n");
            }

            messageBuilder.text(buildToCorrectString(builder));
            try {
                botService.execute(messageBuilder.build());
            } catch (TelegramApiException e) {
                log.error(e.toString());
                return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    /**
     * Checking the received JSON data for validity
     * @param giteaWebHook - GiteaWebHook
     * @return true - if not null, PUSH to master, private key matched
     */
    private boolean validationWebHookContent(GiteaWebHook giteaWebHook){
        return giteaWebHook != null && //If there is anything at all
                giteaWebHook.getRef().contains(giteaWebHook.getRepository().getDefaultBranch()) &&  //If there was a PUSH to / master
                giteaWebHook.getSecret().equals(secret); //If the secret key matches
    }

    private String buildToCorrectString(StringBuilder builder){
        return builder.toString()
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("`", "\\`")
                .replace("&nbsp;", " ")
                .replace("&frac", " ")
                .replaceAll(" \\u003c","");
    }
}
