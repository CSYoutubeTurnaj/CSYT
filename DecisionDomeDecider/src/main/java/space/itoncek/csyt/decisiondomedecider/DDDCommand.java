/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt.decisiondomedecider;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static space.itoncek.csyt.decisiondomedecider.DecisionDomeDecider.*;

public class DDDCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            switch (args[0]) {
                case "startAuto" -> {
                    if (currentManager != null) currentManager.destroy();
                    currentManager = new DDDManager(true);
                    currentManager.start();
                    taskList.add(new BukkitRunnable() {
                        @Override
                        public void run() {
                            currentManager.end();
                        }
                    }.runTaskLater(ddd, 30 * 20));
                }
                case "abortAuto" -> {
                    taskList.forEach(BukkitTask::cancel);
                }
                case "startManual" -> {
                    if (currentManager != null) currentManager.destroy();
                    currentManager = new DDDManager(false);
                    currentManager.start();
                }
                case "endManual" -> {
                    currentManager.end();
                }
                case "fillManual" -> {
                    currentManager.fill();
                }
                case "sendManual" -> {
                    currentManager.startMinigame();
                }
                default -> {
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp())
            return List.of("startAuto", "abortAuto", "startManual", "endManual", "sendManual", "fillManual");
        return List.of();
    }
}
