package com.hbm.packet;

//import com.hbm.handler.JetpackHandler; // JetpackHandler removed
//import com.hbm.handler.JetpackHandler.JetpackInfo; // JetpackInfo removed

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JetpackSyncPacket implements IMessage {

	int playerId;
	//JetpackInfo info; // JetpackInfo removed

	public JetpackSyncPacket() {
	}

	public JetpackSyncPacket(EntityPlayer player) {
		playerId = player.getEntityId();
		//info = JetpackHandler.get(player); // JetpackHandler removed
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		playerId = buf.readInt();
		//info = new JetpackInfo(false); // JetpackInfo removed
		//info.read(buf); // JetpackInfo removed
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(playerId);
		//info.write(buf); // JetpackInfo removed
	}

	public static class Handler implements IMessageHandler<JetpackSyncPacket, IMessage> {

		@Override
		public IMessage onMessage(JetpackSyncPacket message, MessageContext ctx) {
			if(ctx.side == Side.SERVER) {
				ctx.getServerHandler().player.server.addScheduledTask(() -> {
					//EntityPlayer player = ctx.getServerHandler().player;
					//JetpackInfo info = JetpackHandler.get(player); // JetpackHandler removed
					//if(info == null) { // JetpackHandler removed
					//	JetpackHandler.put(player, info = new JetpackInfo(false)); // JetpackHandler removed
					//} // JetpackHandler removed
					//JetpackHandler.put(player, message.info); // JetpackHandler removed
				});
			} else {
				handleMessageClient(message, ctx);
			}
			return null;
		}

		@SideOnly(Side.CLIENT)
		public void handleMessageClient(JetpackSyncPacket m, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				World world = Minecraft.getMinecraft().world;
				Entity ent = world.getEntityByID(m.playerId);
				if(ent instanceof EntityPlayer) {
					//EntityPlayer player = (EntityPlayer) ent;
					//JetpackInfo info = JetpackHandler.get(player); // JetpackHandler removed
					//if(info == null) { // JetpackHandler removed
					//	info = new JetpackInfo(true); // JetpackInfo removed
					//	JetpackHandler.put(player, info); // JetpackHandler removed
					//} // JetpackHandler removed
					//info.setFromServer(m.info); // JetpackInfo removed
				}
			});
		}
	}
}
