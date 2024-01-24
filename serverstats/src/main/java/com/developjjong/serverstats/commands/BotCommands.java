package com.developjjong.serverstats.commands;

import com.developjjong.serverstats.service.BotServices;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        TextChannel channel = event.getChannel().asTextChannel();
        BotServices botServices = new BotServices();

        if(event.getName().equals("하이")){
            String user = event.getUser().getName();
            event.reply(user + "님 안녕하세요.").queue();
        }else if(event.getName().equals("설정")){
            botServices.autoUpdateChannels(event.getGuild());
            event.reply("서버 설정이 완료되었습니다.").queue();
        }else if(event.getName().equals("해제")){
            botServices.deleteVoiceChannels(event.getGuild());
            botServices.deleteCategory(event.getGuild());
            event.reply("삭제가 완료 되었습니다.").queue();
        }
    }
}
