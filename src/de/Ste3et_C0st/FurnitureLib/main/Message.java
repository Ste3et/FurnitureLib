package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class Message {

	public String msg = "";
	
	public Message(String s){
		msg+=s;
	}
	
	public void sendMessage(Player p){
		try {
			PacketContainer container = new PacketContainer(PacketType.Play.Server.CHAT);
			container.getChatComponents().write(0, WrappedChatComponent.fromJson(msg));
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, container);
		} catch (Exception e) {e.printStackTrace();}
	}
	
}
