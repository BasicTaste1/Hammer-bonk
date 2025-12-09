package com.hammerBonk;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("Hammer Bonk")
public interface hammerBonkConfig extends Config
{
    @ConfigSection(
            name = "Text",
            description = "Overhead text settings",
            position = 1
    )
    String textsection = "textSection";

    @ConfigSection(
            name = "Sound",
            description = "Sound settings",
            position = 2
    )
    String soundsection = "soundSection";

    @ConfigItem(
            keyName = "overheadTextEnabler",
            name = "Enable Overhead Text",
            description = "Display funny overhead text",
            position = 1,
            section = textsection
    )
    default boolean overheadTextEnabler()
    {
        return true;
    }

    @ConfigItem(
            keyName = "overheadText",
            name = "Overhead Text",
            description = "Specify the text you wish to be displayed.",
            position = 2,
            section = textsection
    )
    default String overheadText()
    {
        return "I am a silly hammer bonker!";
    }
    @ConfigItem(
            keyName = "duration",
            name = "Duration of overhead text",
            description = "Adjust duration of overhead text",
            position = 3,
            section = textsection
    )
    default int duration() {
        return 15;
    }

    @ConfigItem(
            keyName = "soundPlayer",
            name = "Squeaky Sound",
            description = "Play a squeaky sound when hammer bonks",
            position = 1,
            section = soundsection
    )
    default boolean soundPlayer()
    {
        return false;
    }


    @ConfigItem(
            keyName = "gain DB",
            name = "Gain adjustor",
            description = "adjust volume gain",
            position = 2,
            section = soundsection
    )
    default int gainDB() {
        return 5;
    }

}
