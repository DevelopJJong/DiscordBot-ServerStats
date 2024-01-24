package com.developjjong.serverstats.service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.EnumSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class BotServices {
    String emoji0 = "\uD83D\uDCCA";
    private static final int UPDATE_INTERVAL = 15 * 60 * 1000; // 15분마다 업데이트

    public void autoUpdateChannels(Guild guild){
        log.info("autoUpdateChannel 실행");
        guild.loadMembers().onSuccess(members -> {
            int botCount = 0;
            int memberCount = 0;
            for (Member member : members) {
                if (member.getUser().isBot()) {
                    botCount++;
                }else memberCount++;
            }

            String total = "서버 총 인원 수: " + guild.getMemberCount();
            String member = "현재 인원: " + memberCount;
            String bot = "봇: " + botCount;

            Category category = getOrCreateCategory(guild, emoji0 + "SERVERSTATS" + emoji0);

            createVoiceChannel(total, category);
            createVoiceChannel(member, category);
            createVoiceChannel(bot, category);
            startAutoUpdate(total, category);

            category.getManager().setPosition(0).queue(); // 맨 위로 올리는 코드

        });
    }

    public void startAutoUpdate(String channelName, Category category) {
        Timer timer = new Timer();
        TimerTask ttask = new TimerTask() {
            @Override
            public void run() {
                log.info("1분 타이머 실행");
                updateVoiceChannel(channelName, category);
            }
        };
        timer.schedule(ttask, UPDATE_INTERVAL);
    }

    public void updateVoiceChannel(String channelName, Category category) {
        log.info("update 실행");
        List<VoiceChannel> voiceChannels = category.getGuild().getVoiceChannelsByName(channelName, true);

        String total = "서버 총 인원 수: " + category.getGuild().getMemberCount();


        if (voiceChannels.isEmpty()) {
            // 음성 채널이 존재하지 않으면 생성 및 설정
            createVoiceChannel(channelName, category);
        } else {
            // 음성 채널이 존재하는 경우
            boolean needUpdate = true;
            for (VoiceChannel voiceChannel : voiceChannels) {
                if (voiceChannel.getName().equals(total)) {
                    log.info("total = " + total + ", channelName = " + channelName);
                    log.info("업데이트 필요없음");
                    startAutoUpdate(total,category);
                    needUpdate = false;
                    break;
                }
            }

            if (needUpdate) {
                log.info("업데이트 필요함");
                deleteVoiceChannels(category.getGuild());
                autoUpdateChannels(category.getGuild());
            }
        }
    }

    public void createVoiceChannel(String channelName, Category category) {
            category.createVoiceChannel(channelName)
                    .addPermissionOverride(category.getGuild().getPublicRole(), EnumSet.of(Permission.VIEW_CHANNEL), EnumSet.of(Permission.VOICE_CONNECT))
                    .queue(voiceChannel -> {
                        // 작업이 완료된 후에 실행될 코드
                        try {
                            Thread.sleep(3000); // 적절한 대기 시간을 설정
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
    }

    public Category getOrCreateCategory(Guild guild, String categoryName) {
        List<Category> categories = guild.getCategoriesByName(categoryName, true);
        return categories.isEmpty() ? guild.createCategory(categoryName).complete() : categories.get(0);
    }

    public void deleteVoiceChannels(Guild guild) {

            for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                try {
                    voiceChannel.delete().queue();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.error("VoiceChannel 삭제 중 오류 발생:", e);
                }
            }
        log.info("게시글 삭제");
    }
    public void deleteCategory(Guild guild) {
        Category category = getOrCreateCategory(guild, emoji0 + "SERVERSTATS" + emoji0);
        category.delete().queue();
        log.info("카테고리 삭제");
    }

}
