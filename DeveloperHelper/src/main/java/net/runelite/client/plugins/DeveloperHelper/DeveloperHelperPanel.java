/*
 * Copyright (c) 2017, Kronos <https://github.com/KronosDesign>
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
package net.runelite.client.plugins.DeveloperHelper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.swing.*;

import joptsimple.util.KeyValuePair;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.queries.WallObjectQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@Slf4j
class DeveloperHelperPanel extends PluginPanel
{
    @Inject
    private DeveloperHelperConfig config;

    @Inject
    private ClientThread clientThread;

    @Inject
    private Client client;


    private final Notifier notifier;
    private final DeveloperHelperPlugin plugin;

    private final InfoBoxManager infoBoxManager;
    private final ScheduledExecutorService scheduledExecutorService;


    @Inject
    private DeveloperHelperPanel(
            Client client,
            DeveloperHelperPlugin plugin,
            Notifier notifier,
            InfoBoxManager infoBoxManager,
            ScheduledExecutorService scheduledExecutorService)
    {
        super();
        this.client = client;
        this.plugin = plugin;
        this.notifier = notifier;
        this.infoBoxManager = infoBoxManager;
        this.scheduledExecutorService = scheduledExecutorService;
        build();
    }

    public void build()
    {
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        add(createHeader());
        options = createOptionsPanel();
        add(options);
    }

    public TitlePanel createHeader()
    {
        TitlePanel e = new TitlePanel();
        e.setContent("Developer Helper", "By ImNo: ImNo#0890");
        return e;
    }

    public JCheckBox LogMenuActions = new JCheckBox();
    public JCheckBox LogChatbox = new JCheckBox();
    public JCheckBox LogGameSateChanged = new JCheckBox();
    public JCheckBox LogMenuEntryAdded = new JCheckBox();
    public JCheckBox LogOnWidgetLoaded = new JCheckBox();
    public JCheckBox LogWidgetHiddenChanged = new JCheckBox();
    public JCheckBox LogGameObjectSpawned = new JCheckBox();
    public JCheckBox LogGameObjectDespawned = new JCheckBox();
    public JCheckBox LogGameObjectChanged = new JCheckBox();
    String[] HandleTypes = { "Log in console", "Log in chat", "Copy" };
    public JComboBox ActionHandleType = new JComboBox(HandleTypes);

    public JPanel options;
    public JComboBox plugins;

    private JPanel createOptionsPanel()
    {
        final JPanel container = new JPanel();
        container.setBackground(ColorScheme.DARK_GRAY_COLOR);
        container.setLayout(new GridLayout(0, 1, 3, 3));
        JButton CopyWidgets = new JButton("Copy All Widgets");
        container.add(CopyWidgets);
        CopyWidgets.addActionListener((ev) ->
        {
            plugin.copy_widgets_on_next_tick = true;
            Clipboard.store("Copying...");
        });

        JButton WidgetInspectorHotfix = new JButton("Widget Inspector Hotfix");
        container.add(WidgetInspectorHotfix);
        WidgetInspectorHotfix.addActionListener((ev) ->
        {
            plugin.widgethotfix = true;
        });

        JButton CopyStringStack = new JButton("Copy StringStack");
        container.add(CopyStringStack);
        CopyStringStack.addActionListener((ev) ->
        {
            String[] strings = client.getStringStack();
            String all = "";
            for(String string : strings)
            {
                all += string + "\n";
            }
            Clipboard.store(all.trim());
        });

        JButton CopyIntStack = new JButton("Copy Intstack");
        container.add(CopyIntStack);
        CopyIntStack.addActionListener((ev) ->
        {
            int[] strings = client.getIntStack();
            String all = "";
            for(int current : strings)
            {
                all += current + "\n";
            }
            Clipboard.store(all.trim());
        });

        JButton CopyVarbitps = new JButton("Copy All Varbitps");
        container.add(CopyVarbitps);
        CopyVarbitps.addActionListener((ev) ->
        {
            int[] strings = client.getVarps();
            String all = "";
            for(int current : strings)
            {
                all += current + "\n";
            }
            Clipboard.store(all.trim());
        });

        JButton CopygetVarcMap = new JButton("Copy All getVarcMap");
        container.add(CopygetVarcMap);
        CopygetVarcMap.addActionListener((ev) ->
        {
            Map<Integer, Object> strings = client.getVarcMap();
            String all = "";
            for (Map.Entry<Integer, Object> entry : strings.entrySet()) {
                all += entry.getKey() + ":" + entry.getValue().toString() + "\n";
            }
            Clipboard.store(all.trim());
        });

        JButton CopyWallObjects = new JButton("Copy All WallObjects");
        container.add(CopyWallObjects);
        CopyWallObjects.addActionListener(ev -> clientThread.invokeLater(() ->
        {
            String all = "";
            final LocatableQueryResults<WallObject> wallQueryResults = new WallObjectQuery().result(client);

            for (final WallObject wallObject : wallQueryResults) {
                //for(Field i : ObjectID.class.getFields())
                ObjectComposition od = client.getObjectDefinition(wallObject.getId());
                if (od == null) {
                    continue;
                }
                if (od.getImpostorIds() != null) {
                    if(od.getImpostor() != null)//Hotfix
                    {
                        od = od.getImpostor();
                    }
                }
                all += "Wallobject { id:" + od.getId() + ", name: " + od.getName() + "}\n";
            }
            Clipboard.store(all.trim());

        }));

        container.add(new JLabel("Logging"));
        container.add(ActionHandleType);
        container.add(new JLabel("Log MenuClicked"));
        container.add(LogMenuActions);

        container.add(new JLabel("Log Chatbox"));
        container.add(LogChatbox);

        container.add(new JLabel("Log GameStateChanged"));
        container.add(LogGameSateChanged);

        container.add(new JLabel("Log MenuEntryAdded"));
        container.add(LogMenuEntryAdded);

        container.add(new JLabel("Log WidgetHiddenChanged"));
        container.add(LogWidgetHiddenChanged);

        container.add(new JLabel("Log WidgetHiddenChanged"));
        container.add(LogOnWidgetLoaded);

        container.add(new JLabel("Log GameObjectSpawned"));
        container.add(LogGameObjectSpawned);

        container.add(new JLabel("Log GameObjectDespawned"));
        container.add(LogGameObjectDespawned);

        container.add(new JLabel("Log GameObjectChanged"));
        container.add(LogGameObjectChanged);

        /*JButton loadplugin = new JButton("Hotswap a plugin");
        container.add(loadplugin);
        loadplugin.addActionListener((ev) ->
        {
            Map<String, Map<String, String>> pluginInfos = externalPluginManager.getPluginsInfoMap();
            ArrayList<String> ar = new ArrayList<String>();
            for(Map.Entry<String, Map<String, String>> plugininfo : pluginInfos.entrySet())
            {
                log.info("PROCESSING:" + plugininfo.getKey());
                //ar.add(plugininfo.getKey());
                for (Map.Entry<String, String> key: plugininfo.getValue().entrySet()) {
                    if(key.getKey().equals("id"))
                    {
                        ar.add(key.getValue());
                    }
                    log.info("KEY:" + key.getKey());
                    log.info("VAL:" + key.getValue());
                }
            }

            plugins = new JComboBox(ar.toArray());
            options.add(plugins);
            JButton reload_plugin = new JButton("Reload This Plugin");
            options.add(reload_plugin);
            reload_plugin.addActionListener((ev2) ->
            {
                load_plugin((String)plugins.getSelectedItem());
            });
            options.revalidate();
            options.repaint();
        });*/

        //JComboBox PluginList = new JComboBox(ar.toArray());
        //container.add(PluginList);
        //TestJFilePicker();
        return container;
    }

    /*public void TestJFilePicker() {
        JFilePicker filePicker = new JFilePicker("Pick a file", "Browse...");
        filePicker.setMode(JFilePicker.MODE_OPEN);
        filePicker.addFileTypeFilter(".jar", "JAVA Files");

        JFileChooser fileChooser = filePicker.getFileChooser();
        log.info("PATH: " + RuneLite.EXTERNALPLUGIN_DIR.toString());
        fileChooser.setCurrentDirectory(RuneLite.EXTERNALPLUGIN_DIR);
        //add(filePicker);
        add(fileChooser);
        revalidate();
    }

    public void load_plugin(String plugin)
    {
        externalPluginManager.uninstall(plugin);
        JOptionPane.showMessageDialog(ClientUI.getFrame(), "Hotswapping: " + plugin + ", place a new .jar and then click OK.", "HOTSWAP", JOptionPane.INFORMATION_MESSAGE);
        externalPluginManager.reloadStart(plugin);
    }*/
}