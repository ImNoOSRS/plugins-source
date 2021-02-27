/*
 * Copyright (c) 2020 ImNoOSRS <https://github.com/ImNoOSRS>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *	list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *	this list of conditions and the following disclaimer in the documentation
 *	and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.PlayerLogger;

import net.runelite.client.config.*;

@ConfigGroup("PlayerLogger")

public interface PlayerLoggerConfig extends Config {

	@ConfigItem(
			name = "Player clickboxes",
			description = "Show player clickboxes",
			position = 1,
			keyName = "PlayerLoggerClickboxes"
	)
	default boolean PlayerClickboxes()
	{
		return true;
	}

	@ConfigItem(
			name = "Player Infobox",
			description = "Shows infobox with detection information.",
			position = 2,
			keyName = "PlayerLoggerInfobox"
	)
	default boolean PlayerInfobox()
	{
		return true;
	}

	@ConfigItem(
			keyName = "ResetDataPlayerLogger",
			name = "Reset Data",
			description = "Resets the collected info.",
			position = 3
	)
	default Button ResetData()
	{
		return new Button();
	}
	
}
