package net.labymod.serverapi.bungee.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.labymod.serverapi.Addon;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.util.List;

/**
 * Class created by qlow | Jan
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LabyModPlayerJoinEvent extends Event {

    private ProxiedPlayer player;
    private String modVersion;
    private boolean chunkCachingEnabled;
    private List<Addon> addons;

}
