package net.halalaboos.huzuni.mc;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.event.PlayerMoveEvent;
import net.halalaboos.huzuni.api.event.UpdateEvent;
import net.halalaboos.huzuni.mod.movement.Freecam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;

/**
 * PlayerControllerMP.func_147493_a replaced with our hook
 * */
public class HuzuniEntityPlayer extends EntityPlayerSP {
	
	private static final Huzuni huzuni = Huzuni.INSTANCE;
	
	private static final UpdateEvent preMotionUpdateEvent = new UpdateEvent(UpdateEvent.Type.PRE),
			postMotionUpdateEvent = new UpdateEvent(UpdateEvent.Type.POST);
	
	private static final PlayerMoveEvent playerMoveEvent = new PlayerMoveEvent(0D, 0D, 0D);

	public HuzuniEntityPlayer(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatisticsManager statFile) {
		super(mcIn, worldIn, netHandler, statFile);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	@Override
	public void move(double x, double y, double z) {
		playerMoveEvent.setMotionX(x);
		playerMoveEvent.setMotionY(y);
		playerMoveEvent.setMotionZ(z);
		huzuni.eventManager.invoke(playerMoveEvent);
		super.move(playerMoveEvent.getMotionX(), playerMoveEvent.getMotionY(), playerMoveEvent.getMotionZ());
	}

	@Override
	public void onUpdateWalkingPlayer() {
		// reset the event data
		preMotionUpdateEvent.setCancelled(false);
		postMotionUpdateEvent.setCancelled(false);

		huzuni.eventManager.invoke(preMotionUpdateEvent);
		if (preMotionUpdateEvent.isCancelled()) {
			huzuni.lookManager.cancelTask();
			return;
		}

		huzuni.clickManager.onUpdate(preMotionUpdateEvent);
		huzuni.hotbarManager.onUpdate(preMotionUpdateEvent);
		huzuni.lookManager.onUpdate(preMotionUpdateEvent);
		if (!Freecam.INSTANCE.isEnabled()) {
			super.onUpdateWalkingPlayer();
		}
		huzuni.lookManager.onUpdate(postMotionUpdateEvent);
		huzuni.hotbarManager.onUpdate(postMotionUpdateEvent);
		huzuni.clickManager.onUpdate(postMotionUpdateEvent);

		huzuni.eventManager.invoke(postMotionUpdateEvent);
	}

	@Override
	public void sendChatMessage(String message) {
		if (message.startsWith(huzuni.commandManager.getCommandPrefix())) {
			huzuni.commandManager.processCommand(message.substring(huzuni.commandManager.getCommandPrefix().length()));
		} else {
			super.sendChatMessage(message);
		}
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		return !Freecam.INSTANCE.isEnabled() && super.isEntityInsideOpaqueBlock();
	}

	@Override
	public boolean isSpectator() {
		return super.isSpectator();
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
	}

	@Override
	protected boolean pushOutOfBlocks(double par1, double par3, double par5) {
		return !Freecam.INSTANCE.isEnabled() && super.pushOutOfBlocks(par1, par3, par5);
	}

	@Override
	public boolean isPushedByWater() {
		return super.isPushedByWater();
	}
	
	@Override
	public void applyEntityCollision(Entity entity) {
		super.applyEntityCollision(entity);
    }
}
