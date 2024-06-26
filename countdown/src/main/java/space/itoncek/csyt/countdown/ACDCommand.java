/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.countdown;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import static space.itoncek.csyt.countdown.Countdown.*;

public class ACDCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp() && args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "settime" -> {
                    if (args.length != 2) return true;
                    try {
                        Countdown.setRemain(Integer.parseInt(args[1]));
                    } catch (NumberFormatException ignored) {
                    }
                }
                case "auto" -> {
                    if (args.length != 2) return true;
                    try {
                        //EffectManager manager = new EffectManager();
                        //manager.scheduleEvents();
                        if (acdR != null && !acdR.isCancelled()) acdR.cancel();
                        acdR = generateACDR(Integer.parseInt(args[1]));
                        acdR.runTaskTimer(pl, 20L, 20L);
                    } catch (NumberFormatException ignored) {
                        sender.sendMessage(Component.text("Nope"));
                    }
                }
                case "config" -> {
                    if (sender instanceof Player) {
                        if (args[1].matches("loc[1-4]")) {
                            JSONObject loc = new JSONObject();
                            loc.put("x", ((Player) sender).getLocation().getBlockX());
                            loc.put("y", ((Player) sender).getLocation().getBlockY());
                            loc.put("z", ((Player) sender).getLocation().getBlockZ());
                            config.put(args[1], loc);
                            try {
                                pl.reloadConfig(config);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (args[1].equals("world")) {
                            config.put("world", ((Player) sender).getLocation().getWorld().getName());
                            try {
                                pl.reloadConfig(config);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
