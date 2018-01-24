package net.labymod.serverapi.bungee.event;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

/**
 * Class created by qlow | Jan
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MessageReceiveEvent extends Event {

    private ProxiedPlayer player;
    private String messageKey;
    private JsonElement jsonElement;

}
