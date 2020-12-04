package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper;

import java.util.Objects;

import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import com.comphenix.protocol.wrappers.EnumWrappers.ChatType;

public class ChatComponentWrapper {

	private static boolean packetMethod = false;
	
	public static void sendChatComponent(Player player, BaseComponent... components) {
		sendChatComponent(player, ChatMessageType.CHAT, components);
	}
	
	public static void sendChatComponent(Player player, ChatMessageType type, BaseComponent... components) {
		if(Objects.isNull(type)) type = ChatMessageType.CHAT;
		if(Objects.isNull(components)) return;
		
		try {
			if(packetMethod) {
				sendMessageOverPacket(player, ChatType.valueOf(type.name()), components);
			}else {
				player.spigot().sendMessage(type, components);
			}
		}catch (NoSuchMethodError e) {
			packetMethod = true;
			sendMessageOverPacket(player, ChatType.valueOf(type.name()), components);
		}
	}
	
	private static void sendMessageOverPacket(Player player, ChatType type, BaseComponent... components) {
		for(BaseComponent component : components) {
			String jsonString = ComponentSerializer.toString(component);
			WrappedChatComponent wrappedChatComponent = WrappedChatComponent.fromJson(jsonString);
			try {
				WrapperPlayServerChat chat = new WrapperPlayServerChat();
				chat.setChatType(type);
				chat.setMessage(wrappedChatComponent);
				chat.sendPacket(player);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
