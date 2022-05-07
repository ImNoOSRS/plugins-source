/*
 * Copyright (c) 2019 Spudjb <https://github.com/spudjb>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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
package net.runelite.client.plugins.friendsearch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.lang.model.util.ElementScanner6;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.util.Text;
import net.runelite.api.vars.InterfaceTab;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.game.chatbox.ChatboxTextInput;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
		name = "Friend Searcher",
		description = "Adds searching and filtering to the quest list",
		tags = {"imno"}
)
public class FriendSearchPlugin extends Plugin
{
	private static final int ENTRY_PADDING = 8;
	private static final List<String> QUEST_HEADERS = List.of("Free Quests", "Members' Quests", "Miniquests");

	private static final String MENU_OPEN = "Open";
	private static final String MENU_CLOSE = "Close";

	private static final String MENU_TOGGLE = "Toggle";

	private static final String MENU_SEARCH = "Search";
	private static final String MENU_SHOW = "Show";

	@Inject
	private Client client;

	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	private ClientThread clientThread;

	private ChatboxTextInput searchInput;
	private Widget friendSearchButton;
	private Widget friendHideButton;

	private EnumMap<friendContainer, Collection<FriendWidget>> friendSet;


	@Override
	protected void startUp()
	{
		clientThread.invoke(this::addFriendSearchButton);
	}

	@Override
	protected void shutDown()
	{
		Widget header = client.getWidget(WidgetInfo.FRIENDS_LIST);
		if (header != null)
		{
			header.deleteAllChildren();
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged e)
	{

	}

	@Subscribe
	private void onGameTick(GameTick tick)
	{
		final int world = client.getWorld();
		final boolean isMember = client.getVar(VarPlayer.MEMBERSHIP_DAYS) > 0;

		final NameableContainer<Friend> friendContainer = client.getFriendContainer();
		final int friendCount = friendContainer.getCount();
		if (friendCount >= 0) {

			final String title = "Friends - W" +
					world +
					" (" +
					friendCount +
					")";

			setFriendsListTitle(title);
		}
	}

	private void setFriendsListTitle(final String title)
	{
		Widget friendListTitleWidget = client.getWidget(WidgetInfo.FRIEND_CHAT_TITLE);
		if (friendListTitleWidget != null)
		{
			friendListTitleWidget.setText(title);
		}
	}

	@Subscribe
	private void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() != ScriptID.FRIENDS_UPDATE)
		{
			return;
		}

		addFriendSearchButton();
	}

	private void addFriendSearchButton()
	{
		Widget header = client.getWidget(WidgetInfo.FRIENDS_LIST);
		if(header == null)
		{
			header = client.getWidget(WidgetInfo.IGNORE_LIST);
		}
		if (header != null)
		{
			header.deleteAllChildren();

			friendSearchButton = header.createChild(-1, WidgetType.GRAPHIC);
			friendSearchButton.setSpriteId(SpriteID.GE_SEARCH);
			friendSearchButton.setOriginalWidth(18);
			friendSearchButton.setOriginalHeight(17);
			friendSearchButton.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
			friendSearchButton.setOriginalX(18);
			friendSearchButton.setOriginalY(3);
			friendSearchButton.setHasListener(true);
			friendSearchButton.setAction(1, MENU_OPEN);
			friendSearchButton.setOnOpListener((JavaScriptCallback) e -> openSearch());
			friendSearchButton.setName(MENU_SEARCH);
			friendSearchButton.revalidate();

			friendSet = new EnumMap<>(friendContainer.class);

			updateFilter();
		}
	}

	@Subscribe
	private void onVarbitChanged(VarbitChanged varbitChanged)
	{
		if (isChatboxOpen() && isNotOnFriendsTab())
		{
			chatboxPanelManager.close();
		}
	}

	@Subscribe
	private void onVarClientIntChanged(VarClientIntChanged varClientIntChanged)
	{
		if (varClientIntChanged.getIndex() == VarClientInt.INVENTORY_TAB.getIndex() && isChatboxOpen() && isNotOnFriendsTab())
		{
			chatboxPanelManager.close();
		}
	}

	private boolean isNotOnFriendsTab()
	{
		return client.getVar(VarClientInt.INVENTORY_TAB) != 9 || client.getVar(VarClientInt.INVENTORY_TAB) != InterfaceTab.FRIENDS.getId();
	}

	private boolean isChatboxOpen()
	{
		return searchInput != null && chatboxPanelManager.getCurrentInput() == searchInput;
	}

	private void closeSearch()
	{
		updateFilter("");
		chatboxPanelManager.close();
		client.playSoundEffect(SoundEffectID.UI_BOOP);
	}

	private void openSearch()
	{
		updateFilter("");
		client.playSoundEffect(SoundEffectID.UI_BOOP);
		friendSearchButton.setAction(1, MENU_CLOSE);
		friendSearchButton.setOnOpListener((JavaScriptCallback) e -> closeSearch());
		searchInput = chatboxPanelManager.openTextInput("Search friend list")
				.onChanged(s -> clientThread.invokeLater(() -> updateFilter(s)))
				.onDone(s -> false)
				.onClose(() ->
				{
					clientThread.invokeLater(() -> updateFilter(""));
					friendSearchButton.setOnOpListener((JavaScriptCallback) e -> openSearch());
					friendSearchButton.setAction(1, MENU_OPEN);
				})
				.build();
	}

	private void updateFilter()
	{
		String filter = "";
		if (isChatboxOpen())
		{
			filter = searchInput.getValue();
		}

		updateFilter(filter);
	}

	private void updateFilter(String filter)
	{
		filter = filter.toLowerCase();
		final Widget container = client.getWidget(WidgetInfo.FRIEND_LIST_FULL_CONTAINER);

		final Widget FriendList = client.getWidget(friendContainer.FRIENDS.widgetInfo);

		if (container == null || FriendList == null)
		{
			return;
		}

		updateList(friendContainer.FRIENDS, filter);

	}

	private void updateList(friendContainer friendContainer, String filter)
	{
		Widget list = client.getWidget(friendContainer.widgetInfo);
		if (list == null)
		{
			return;
		}

		Collection<FriendWidget> friends = friendSet.get(friendContainer);

		if (friends != null &&
				// Check to make sure the list hasn't been rebuild since we were last her
				// Do this by making sure the list's dynamic children are the same as when we last saw them
				friends.stream().noneMatch(w ->
				{
					Widget codeWidget = w.getFriend();
					if (codeWidget == null)
					{
						return false;
					}
					return list.getChild(codeWidget.getIndex()) == codeWidget;
				}))
		{
			friends = null;
		}

		if (friends == null)
		{
			// Find all of the widgets that we care about, sorting by their Y value
			friends = Arrays.stream(list.getDynamicChildren())
					.sorted(Comparator.comparing(Widget::getRelativeY))
					.filter(w -> !QUEST_HEADERS.contains(w.getText()))
					.map(w -> new FriendWidget(w, Text.removeTags(w.getText()).toLowerCase()))
					.collect(Collectors.toList());
			friendSet.put(friendContainer, friends);
		}

		process_friends(friends, filter, list);
	}

	private enum FriendLoaderState {
		USER,
		PREVIOUS_NAME,
		LOCATION
	}

	public FriendLoaderState nextstate(FriendLoaderState s)
	{
		if(s == FriendLoaderState.USER)
		{
			return FriendLoaderState.PREVIOUS_NAME;
		}
		else if(s == FriendLoaderState.PREVIOUS_NAME)
		{
			return FriendLoaderState.LOCATION;
		}
		else {
			return FriendLoaderState.USER;
		}
	}
	
	public void process_friends(Collection<FriendWidget> friends, String filter, Widget container)
	{
		// offset because of header
		int y = -15;
		boolean hidden = false;

		FriendLoaderState currentstate = FriendLoaderState.USER;
		for (FriendWidget friendInfo : friends)
		{
			Widget friend = friendInfo.getFriend();

			//log.info("Processing: " + friendInfo.getTitle() + ", state: " + currentstate.toString());
			if(currentstate == FriendLoaderState.USER)
			{
				if(friendInfo.getTitle().contains(filter))
				{
					y += 15;
					hidden = false;
				}
				else
				{
					hidden = true;
				}
			}

			if(currentstate == FriendLoaderState.PREVIOUS_NAME)
			{
				if(hidden)
				{
					friend.setHidden(hidden);
				}
			}
			else
			{
				friend.setHidden(hidden);
			}
			friend.setOriginalY(y);
			friend.revalidate();
			currentstate = nextstate(currentstate);
		}

		container.setScrollHeight(y);
		container.revalidateScroll();
		int newHeight = 0;
        if (container.getScrollHeight() > 0)
        {
            newHeight = (container.getScrollY() * y) / container.getScrollHeight();
        }

        final int steadynewheight = newHeight;
		
		clientThread.invokeLater(() ->
		client.runScript(
			ScriptID.UPDATE_SCROLLBAR,
			WidgetInfo.FRIEND_LIST_SCROLL_BAR.getId(),
			container.getId(),
				steadynewheight
		));
		//list.setOriginalHeight((totalfriends * 15) - 15);
	}

	@AllArgsConstructor
	@Getter(AccessLevel.PRIVATE)
	private enum friendContainer
	{
		FRIENDS(WidgetInfo.FRIEND_LIST_NAMES_CONTAINER);
		private final WidgetInfo widgetInfo;
	}


	@Data
	@AllArgsConstructor
	private static class FriendWidget
	{
		private Widget friend;
		private String title;
	}
}