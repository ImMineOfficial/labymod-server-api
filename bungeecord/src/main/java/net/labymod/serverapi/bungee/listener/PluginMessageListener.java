package net.labymod.serverapi.bungee.listener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import net.labymod.serverapi.Addon;
import net.labymod.serverapi.bungee.LabyModPlugin;
import net.labymod.serverapi.bungee.event.LabyModPlayerJoinEvent;
import net.labymod.serverapi.bungee.event.MessageReceiveEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Class created by qlow | Jan
 */
public class PluginMessageListener implements Listener {

    @Getter
    private final static JsonParser jsonParser = new JsonParser();

    @EventHandler
    public void onPluginMessage( PluginMessageEvent event ) {
        if ( !(event.getSender() instanceof ProxiedPlayer) )
            return;

        final ProxiedPlayer player = ( ProxiedPlayer ) event.getSender();

        // The LABYMOD plugin channel is higly deprecated and shouldn't be used - we just listen to it to retrieve old labymod clients.
        if ( event.getTag().equals( "LABYMOD" ) ) {
            // Converting the byte array into a byte buffer
            ByteBuf buf = Unpooled.wrappedBuffer( event.getData() );

            try {
                // Reading the version from the buffer
                final String version = LabyModPlugin.getInstance().getApi().readString( buf, Short.MAX_VALUE );

                // Calling the event synchronously
                ProxyServer.getInstance().getScheduler().schedule( LabyModPlugin.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        // Calling the LabyModPlayerJoinEvent
                        ProxyServer.getInstance().getPluginManager().callEvent( new LabyModPlayerJoinEvent( player, version, false, new ArrayList<Addon>() ) );
                    }
                }, 0L, TimeUnit.SECONDS );
            } catch ( RuntimeException ex ) {
                ex.printStackTrace();
            }
        }

        if ( event.getTag().equals( "LMC" ) ) {
            // Converting the byte array into a byte buffer
            ByteBuf buf = Unpooled.wrappedBuffer( event.getData() );

            try {
                // Reading the message key
                final String messageKey = LabyModPlugin.getInstance().getApi().readString( buf, Short.MAX_VALUE );
                final String messageContents = LabyModPlugin.getInstance().getApi().readString( buf, Short.MAX_VALUE );
                final JsonElement jsonMessage = jsonParser.parse( messageContents );

                // Calling the event synchronously
                ProxyServer.getInstance().getScheduler().schedule( LabyModPlugin.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        // Listening to the INFO (join) message
                        if ( messageKey.equals( "INFO" ) && jsonMessage.isJsonObject() ) {
                            JsonObject jsonObject = jsonMessage.getAsJsonObject();
                            String version = jsonObject.has( "version" )
                                    && jsonObject.get( "version" ).isJsonPrimitive()
                                    && jsonObject.get( "version" ).getAsJsonPrimitive().isString() ? jsonObject.get( "version" ).getAsString() : "Unknown";

                            ProxyServer.getInstance().getPluginManager().callEvent( new LabyModPlayerJoinEvent( player, version,
                                    jsonObject.has( "ccp" ) && jsonObject.get( "ccp" ).isJsonPrimitive()
                                            && jsonObject.get( "ccp" ).getAsJsonPrimitive().isBoolean()
                                            && jsonObject.get( "ccp" ).getAsBoolean(), Addon.getAddons( jsonObject ) ) );
                            return;
                        }

                        // Calling the LabyModPlayerJoinEvent
                        ProxyServer.getInstance().getPluginManager().callEvent( new MessageReceiveEvent( player, messageKey, jsonMessage ) );
                    }
                }, 0L, TimeUnit.SECONDS );
            } catch ( RuntimeException ex ) {
                ex.printStackTrace();
            }
        }
    }

}
