/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.construction.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.csyt.UpdateLib;
import space.itoncek.csyt.construction.BuildPlace;
import space.itoncek.csyt.construction.Pattern;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static final File placesConfig = new File("./plugins/construction/places.json");

    public static List<BuildPlace> loadPlaces() {
        long start = System.currentTimeMillis();

        String sb = UpdateLib.getFile("places.json");

        //Bukkit.getLogger().info(sb.toString());
        List<BuildPlace> out = BuildPlace.deserialize(new JSONArray(sb));
        Bukkit.getLogger().info("Places config loaded in " + (System.currentTimeMillis() - start) + "ms");
        return out;
    }

    public static long savePlaces(List<BuildPlace> places) {
        long start = System.currentTimeMillis();
        JSONArray array = BuildPlace.serialize(places);

        folderStuff();

        try (FileWriter fw = new FileWriter(placesConfig)) {
            fw.write(array.toString(4));
        } catch (IOException e) {
            Bukkit.getLogger().throwing("ConfigManager", "savePlaces()", e);
        } finally {
            Bukkit.getLogger().info("Places config saved in " + (System.currentTimeMillis() - start) + "ms");
        }
        return (System.currentTimeMillis() - start);
    }

    public static List<Pattern> loadPatterns() {
        long start = System.currentTimeMillis();
        List<Pattern> output = new ArrayList<>();
        folderStuff();
        JSONArray object;
        String sb = UpdateLib.getFile("index.json");
        object = new JSONArray(sb);
        for (Object o : object) {
            JSONObject raw = (JSONObject) o;

            Material[][] pattern = new Material[5][5];
            int x = 0;
            for (Object row : raw.getJSONArray("pattern")) {
                int z = 0;
                for (Object s : ((JSONArray) row)) {
                    pattern[x][z] = Material.valueOf((String) s);
                    z++;
                }
                x++;
            }
            List<Material> materials = new ArrayList<>();
            for (Object mat : raw.getJSONArray("materials")) {
                materials.add(Material.valueOf((String) mat));
            }

            output.add(new Pattern(raw.getInt("id"), pattern, materials));
        }
        Bukkit.getLogger().info("Patterns loaded in " + (System.currentTimeMillis() - start) + "ms");
        return output;
    }

    private static void folderStuff() {
        if (!placesConfig.getParentFile().exists()) placesConfig.getParentFile().mkdirs();
        if (!placesConfig.exists()) {
            try {
                placesConfig.createNewFile();
                savePlaces(List.of());
            } catch (IOException e) {
                Bukkit.getLogger().throwing("ConfigManager", "folderStuff()", e);
            }
        }
    }
}
