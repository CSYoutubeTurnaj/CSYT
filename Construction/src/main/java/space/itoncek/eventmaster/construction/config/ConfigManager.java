package space.itoncek.eventmaster.construction.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.json.JSONArray;
import org.json.JSONObject;
import space.itoncek.eventmaster.construction.BuildPlace;
import space.itoncek.eventmaster.construction.Pattern;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

public class ConfigManager {
    private static final File placesConfig = new File("./plugins/construction/places.json");
    private static final File patternFolder = new File("./plugins/construction/patterns/");

    public static List<BuildPlace> loadPlaces() {
        long start = System.currentTimeMillis();

        folderStuff();

        StringBuilder sb = new StringBuilder();

        try (Scanner sc = new Scanner(placesConfig)) {
            while (sc.hasNextLine()) sb.append(sc.nextLine()).append("\n");
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().throwing("ConfigManager", "loadPlaces()", e);
        }

        List<BuildPlace> out = BuildPlace.deserialize(new JSONArray(sb.toString()));
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
        try (Scanner sc = new Scanner(new URL("https://cdn.itoncek.space/patterns/index.json").openStream())) {
            StringJoiner sj = new StringJoiner("\n");
            while (sc.hasNextLine()) sj.add(sc.nextLine());
            object = new JSONArray(sj.toString());
            for (Object o : object) {
                JSONObject raw = (JSONObject) o;

                List<List<Material>> pattern = new ArrayList<>();
                for (Object row : raw.getJSONArray("pattern")) {
                    List<Material> rows = new ArrayList<>();
                    for (Object s : ((JSONArray) row)) {
                        rows.add(Material.valueOf((String) s));
                    }
                    pattern.add(rows);
                }
                List<Material> materials = new ArrayList<>();
                for (Object mat : raw.getJSONArray("materials")) {
                    materials.add(Material.valueOf((String) mat));
                }

                output.add(new Pattern(raw.getInt("id"), pattern, materials));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        if (!patternFolder.exists()) patternFolder.mkdirs();
    }
}
