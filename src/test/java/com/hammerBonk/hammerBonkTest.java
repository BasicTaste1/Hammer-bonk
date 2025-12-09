package com.hammerBonk;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class hammerBonkTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(hammerBonkPlugin.class);
		RuneLite.main(args);
	}
}