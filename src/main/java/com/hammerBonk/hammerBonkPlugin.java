/*
Copyright (c) 2025, BasicTaste

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


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
