/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.runecraftingtracker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

public class RunecraftingTrackerPanel extends PluginPanel
{
	// When there is nothing tracked, display this
	private final PluginErrorPanel errorPanel = new PluginErrorPanel();
	private JPanel container;
	private LinkedList<PanelItemData> runeTracker;

	RunecraftingTrackerPanel(LinkedList<PanelItemData> runeTracker)
	{

		this.runeTracker = runeTracker;
		setBorder(new EmptyBorder(1, 5, 5, 5));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());

		container = new JPanel();
		container.setBorder(new EmptyBorder(10, 0, 0, 0));
		container.setLayout(new GridLayout(0, 1, 0, 2));

		container.add(panelItem(
			new ImageIcon(ImageUtil.getResourceStreamFromClass(RunecraftingTrackerPlugin.class,
				"COIN.png")),
			null));

		add(container, BorderLayout.CENTER);

		// Error panel
		//errorPanel.setContent("Runecrafting Tracker", "You have not crafted any runes yet.");
		//add(errorPanel);

		pack();
	}

	protected void pack()
	{
		container.removeAll();

		AtomicInteger totalProfit = new AtomicInteger(0);

		runeTracker.forEach((temp) -> {
			totalProfit.addAndGet(temp.getCrafted() * temp.getCostPerRune());
		});

		container.add(panelItem(
			new ImageIcon(ImageUtil.getResourceStreamFromClass(RunecraftingTrackerPlugin.class,
				"COIN.png")),
			totalProfit));

		runeTracker.forEach((temp) -> {
			if (temp.isVisible())
			{
				container.add(panelItem(
					new ImageIcon(ImageUtil.getResourceStreamFromClass(RunecraftingTrackerPlugin.class,
						temp.getName() + ".png")),
					temp.getCrafted(),
					temp.getCrafted() * temp.getCostPerRune())
				);
			}
		});
	}

	protected void refresh()
	{
		revalidate();
	}

	protected LinkedList<PanelItemData> getRuneTracker()
	{
		return runeTracker;
	}

	private JPanel panelItem(ImageIcon icon, int textTop_crafted, int textBottom_profit)
	{
		JPanel container = new JPanel();
		container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		container.setLayout(new BorderLayout());
		container.setBorder(new EmptyBorder(2, 10, 2, 10));

		JLabel iconLabel = new JLabel(icon);
		container.add(iconLabel, BorderLayout.WEST);

		JPanel textContainer = new JPanel();
		textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		textContainer.setLayout(new GridLayout(2, 1));
		textContainer.setBorder(new EmptyBorder(5, 10, 5, 10));

		JLabel topLine = new JLabel("crafted: " + textTop_crafted);
		topLine.setForeground(Color.WHITE);
		topLine.setFont(FontManager.getRunescapeSmallFont());

		JLabel bottomLine = new JLabel("profit: " + textBottom_profit);
		bottomLine.setForeground(Color.WHITE);
		bottomLine.setFont(FontManager.getRunescapeSmallFont());

		textContainer.add(topLine);
		textContainer.add(bottomLine);

		container.add(textContainer, BorderLayout.CENTER);

		return container;
	}

	private JPanel panelItem(ImageIcon icon, AtomicInteger textMiddle)
	{
		JPanel container = new JPanel();
		container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		container.setLayout(new BorderLayout());
		container.setBorder(new EmptyBorder(2, 10, 2, 10));

		JLabel iconLabel = new JLabel(icon);
		container.add(iconLabel, BorderLayout.WEST);

		JPanel textContainer = new JPanel();
		textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		textContainer.setLayout(new GridLayout(1, 1));
		textContainer.setBorder(new EmptyBorder(5, 10, 5, 10));

		JLabel middleLine = new JLabel("x " + textMiddle);
		middleLine.setForeground(Color.WHITE);
		middleLine.setFont(FontManager.getRunescapeSmallFont());

		textContainer.add(middleLine);

		container.add(textContainer, BorderLayout.CENTER);

		return container;
	}
}