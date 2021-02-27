package net.runelite.client.plugins.hallowedhelper;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Rotation {
    public static ArrayList<ArrayList<Color>> floor4_rotations = new ArrayList<>();
    public static ArrayList<ArrayList<Color>> floor5_rotations = new ArrayList<>();
    public static ArrayList<ArrayList<Color>> floor5_2A_rotations = new ArrayList<>();
    public static ArrayList<ArrayList<Color>> floor5_3A_rotations = new ArrayList<>();
    public static ArrayList<ArrayList<Color>> floor5_4_rotations = new ArrayList<>();
    private static Color safe = Color.GREEN;
    private static Color danger = Color.RED;
    private static Color farsafe = Color.BLUE;
    private static Color nextsafe = new Color(255, 102, 0);
    public static Color blank = Color.BLACK;//For later shit.

    private static ArrayList<Color>selectedset = new ArrayList<>();
    public static void init(Color setsafe, Color setdanger, Color setfarsafe, Color setnextsafe)
    {
        safe = setsafe;
        danger = setdanger;
        farsafe = setfarsafe;
        nextsafe = setnextsafe;
        floor4_rotations.clear();
        floor5_rotations.clear();
        floor5_2A_rotations.clear();
        floor5_3A_rotations.clear();
        floor5_4_rotations.clear();
        init_floor4();
        init_floor5();
        init_floor5_2A();
        init_floor5_3A();
        init_floor5_4();
    }

    public static void clearrotation()
    {
        selectedset = new ArrayList<>();
    }

    public static void init_floor4()
    {
        add(safe);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(farsafe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(nextsafe);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        addrotationto(floor4_rotations);
        add(safe);
        add(safe);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(farsafe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(nextsafe);
        addrotationto(floor4_rotations);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(farsafe);
        addrotationto(floor4_rotations);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        addrotationto(floor4_rotations);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(nextsafe);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        addduplicatedrotationto(floor4_rotations);
    }
    public static void init_floor5()
    {
        add(safe);
        add(safe);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(safe);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(danger);
        addrotationto(floor5_rotations);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(safe);
        add(safe);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(danger);
        add(safe);
        add(safe);
        addrotationto(floor5_rotations);
    }
    public static void init_floor5_2A()
    {
        add(danger);
        add(danger);
        add(danger);
        add(safe);
        add(safe);
        add(nextsafe);
        add(nextsafe);
        add(danger);
        add(safe);
        add(safe);
        add(danger);
        add(nextsafe);
        add(nextsafe);
        add(nextsafe);
        addrotationto(floor5_2A_rotations);
        add(danger);
        add(danger);
        add(danger);
        add(nextsafe);
        add(nextsafe);
        add(safe);
        add(safe);
        add(danger);
        add(nextsafe);
        add(nextsafe);
        add(danger);
        add(safe);
        add(safe);
        add(safe);
        addrotationto(floor5_2A_rotations);
    }

    public static void init_floor5_3A()
    {
        add(danger);
        add(danger);
        add(danger);
        add(safe);
        add(danger);
        add(nextsafe);
        add(danger);
        add(danger);
        add(safe);
        add(danger);
        add(danger);
        add(nextsafe);
        add(nextsafe);
        add(danger);
        addrotationto(floor5_3A_rotations);
        add(danger);
        add(danger);
        add(danger);
        add(nextsafe);
        add(danger);
        add(safe);
        add(danger);
        add(danger);
        add(nextsafe);
        add(danger);
        add(danger);
        add(safe);
        add(safe);
        add(danger);
        addrotationto(floor5_3A_rotations);
    }

    public static void init_floor5_4()
    {
        //first row
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        //third row (we skipped second)
        blanks(14);
        add(danger);
        add(danger);
        //fourth row
        blanks(9);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(danger);
        //Second Rotation
        addrotationto(floor5_4_rotations);
        //first row
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        //third row (we skipped second)
        blanks(14);
        add(danger);
        add(safe);
        //fourth row
        blanks(9);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(safe);
        add(danger);
        add(danger);
        add(danger);
        addrotationto(floor5_4_rotations);
    }

    public static void add(Color c)
    {
        selectedset.add(c);
    }

    public static void blanks(int count)
    {
        for(int i = 0; i < count; ++i)
            add(blank);
    }

    public static void addrotationto(ArrayList<ArrayList<Color>> list)
    {
        list.add(selectedset);
        clearrotation();
    }

        public static void addduplicatedrotationto(ArrayList<ArrayList<Color>> list)
    {
        list.add(selectedset);
        list.add(selectedset);
        clearrotation();
    }
}