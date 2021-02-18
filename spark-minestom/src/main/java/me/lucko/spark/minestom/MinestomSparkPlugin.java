/*
 * This file is part of spark.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lucko.spark.minestom;

import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.common.platform.PlatformInfo;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandProcessor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import net.minestom.server.extensions.Extension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class MinestomSparkPlugin extends Extension implements SparkPlugin, CommandProcessor {

    private SparkPlatform platform;

    public MinestomSparkPlugin() {

    }

    @Override
    public void initialize() {
        this.platform = new SparkPlatform(this);
        this.platform.enable();
        MinecraftServer.getCommandManager().register(this);
    }

    @Override
    public void terminate() {
        this.platform.disable();
    }

    @Override
    public String getVersion() {
        return "${pluginVersion}";
    }

    @Override
    public Path getPluginDirectory() {
        return Paths.get("./extensions/spark");
    }

    @NotNull
    @Override
    public String getCommandName() {
        return "spark";
    }

    @Nullable
    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean process(@NotNull CommandSender sender, @NotNull String command, @NotNull String[] args) {
        this.platform.executeCommand(new MinestomCommandSender(sender), args);

        return true;
    }

    @Override
    public boolean hasAccess(@NotNull Player player) {
        return false;
    }

    @Override
    public boolean enableWritingTracking() {
        return true;
    }

    @Nullable
    @Override
    public String[] onWrite(@NotNull CommandSender sender, String text) {
        return this.platform.tabCompleteCommand(new MinestomCommandSender(sender), text.split(" ")).toArray(new String[0]);
    }

    @Override
    public Stream<MinestomCommandSender> getSendersWithPermission(String permission) {
        return Stream.concat(
                MinecraftServer.getConnectionManager().getOnlinePlayers().stream().filter(player -> player.hasPermission(permission)),
                Stream.of(MinecraftServer.getCommandManager().getConsoleSender())
        ).map(MinestomCommandSender::new);
    }

    @Override
    public void executeAsync(Runnable task) {
        MinecraftServer.getSchedulerManager().buildTask(task).schedule();
    }

    @Override
    public PlatformInfo getPlatformInfo() {
        return new MinestomPlatformInfo();
    }
}
