package com.developjjong.serverstats;

import com.developjjong.serverstats.commands.BotCommands;
import com.developjjong.serverstats.service.BotServices;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;

@SpringBootApplication
public class ServerstatsApplication {

    public static void main(String[] args) throws LoginException, InterruptedException {
        ApplicationContext context = SpringApplication.run(ServerstatsApplication.class, args);
        DiscordBotToken discordBotTokenEntity = context.getBean(DiscordBotToken.class);
        String discordBotToken = discordBotTokenEntity.getDiscordBotToken();
        BotServices botServices = new BotServices();

        JDA jda = JDABuilder.createDefault(discordBotToken)
                .setActivity(Activity.playing("서버 체크"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new BotCommands())
                .build();

        jda.upsertCommand("설정","인원을 채널에 표시할 수 있습니다.").setGuildOnly(true).queue();
        jda.upsertCommand("하이","반갑게 인사를 합니다.").setGuildOnly(true).queue();
        jda.upsertCommand("해제","인원을 채널에 표시했던 걸 해제할 수 있습니다.").setGuildOnly(true).queue();
    }

    @Component
    class DiscordBotToken {
        @Value("${discord.bot.token}")
        private String discordBotToken;

        public String getDiscordBotToken() {
            return discordBotToken;
        }
    }

}
