package space.itoncek.eventmaster.construction;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import space.itoncek.eventmaster.construction.commands.ConstructionCommand;
import space.itoncek.eventmaster.construction.commands.autofill.ConstructionAutofill;
import space.itoncek.eventmaster.construction.debug.ParticleRunnable;
import space.itoncek.eventmaster.construction.utils.TeamColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static space.itoncek.eventmaster.construction.config.ConfigManager.loadPlaces;
import static space.itoncek.eventmaster.construction.config.ConfigManager.savePlaces;

public final class Construction extends JavaPlugin {

    public static List<BuildPlace> buildPlaces;
    public static HashMap<SimpleLocation, BuildPlace> locationHash = new HashMap<>();
    public static ParticleRunnable particles = new ParticleRunnable();

    public static HashMap<TeamColor, TeamAssets> teams = new HashMap<>();
    @Override
    public void onEnable() {
        // Plugin startup logic
        buildPlaces = loadPlaces();
        //TODO: DEBUG STUFF, REMOVE BEFORE RELEASE!
        //getServer().getPluginManager().registerEvents(new MoveListener(), this);
        Objects.requireNonNull(getCommand("construction")).setExecutor(new ConstructionCommand());
        Objects.requireNonNull(getCommand("construction")).setTabCompleter(new ConstructionAutofill());
        particles.runTaskTimer(this, 5L, 5L);

        for (BuildPlace place : buildPlaces) {
            for (Location location : place.getLocations()) {
                locationHash.put(SimpleLocation.createSimpleLocation(location), place);
            }
        }

        for (BuildPlace buildPlace : buildPlaces) {
            if (!teams.containsKey(buildPlace.color))
                teams.put(buildPlace.color, new TeamAssets(null, new ArrayList<>()));

            if (buildPlace.display) {
                teams.get(buildPlace.color).setDisplay(buildPlace);
            } else {
                teams.get(buildPlace.color).addPlace(buildPlace);
            }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        savePlaces(buildPlaces);
    }
}
