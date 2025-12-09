package com.hammerBonk;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import net.runelite.api.*;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.Player;
import net.runelite.api.events.SoundEffectPlayed;

import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import net.runelite.client.audio.AudioPlayer;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@PluginDescriptor(
        name = "Hammer Bonk"
)
public class hammerBonkPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private hammerBonkConfig config;

    @Inject
    private AudioPlayer audioPlayer;


    private ExecutorService executorService;

    @Provides
    hammerBonkConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(hammerBonkConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void shutDown() throws Exception
    {
        executorService.shutdown();
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        if(!config.overheadTextEnabler() && !config.soundPlayer()) {
            return;
        }
        Actor actor = event.getActor();
        if (!(actor instanceof Player)){
            return;
        }
        int currAnimation = actor.getAnimation();
        // Check for Maul/dwh regular attack animations
        if (currAnimation == 401 || currAnimation == 7516) {
            if (config.overheadTextEnabler()){
                executorService.submit(() -> bonkOverhead((Player) actor));
            }
            if (config.soundPlayer())
            {
                executorService.submit(this::playBonkSound);
            }
        }
    }

    @Subscribe
    public void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed)
    {
        if(!config.soundPlayer()){
            return;
        }
        int soundId = soundEffectPlayed.getSoundId();
        if (soundId==3454 || soundId==7516)
        {
            soundEffectPlayed.consume();
        }
    }
    public void bonkOverhead(Player player)
    {
        player.setOverheadText(config.overheadText());
        player.setOverheadCycle(config.duration()*10);
    }

    public void playBonkSound() {
        try
        {
            audioPlayer.play(this.getClass(), "/squeak_toy.wav", config.gainDB()-35);
        } catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
    }
}
