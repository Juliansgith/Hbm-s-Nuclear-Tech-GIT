package com.hbm.inventory.container;

//import com.hbm.handler.ArmorModHandler; // Removed
//import com.hbm.items.armor.ItemArmorMod; // Removed

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ContainerArmorTable extends Container {
	
	public InventoryBasic upgrades = new InventoryBasic("Upgrades", false, 8);
	public IInventory armor = new InventoryCraftResult();

	public ContainerArmorTable(InventoryPlayer inventory) {
		
		// ArmorModHandler fields were removed, using placeholder indices (0-7) for now.
		// This GUI will likely be non-functional or behave unexpectedly.
		this.addSlotToContainer(new UpgradeSlot(upgrades, 0, 26, 27));	// helmet only
		this.addSlotToContainer(new UpgradeSlot(upgrades, 1, 62, 27));		// chestplate only
		this.addSlotToContainer(new UpgradeSlot(upgrades, 2, 98, 27));		// leggins only
		this.addSlotToContainer(new UpgradeSlot(upgrades, 3, 134, 45));	// boots only
		this.addSlotToContainer(new UpgradeSlot(upgrades, 4, 134, 81));		//servos/frame
		this.addSlotToContainer(new UpgradeSlot(upgrades, 5, 98, 99));		//radiation cladding
		this.addSlotToContainer(new UpgradeSlot(upgrades, 6, 62, 99));			//kevlar/sapi/(ERA? :) )
		this.addSlotToContainer(new UpgradeSlot(upgrades, 7, 26, 99));			//special parts

		this.addSlotToContainer(new Slot(armor, 0, 44, 63) {

			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() instanceof ItemArmor;
			}

			@Override
			public void putStack(ItemStack stack) {
				
				//when inserting a new armor piece, unload all mods to display
				if(stack != null && !stack.isEmpty()) {
					// ItemStack[] mods = ArmorModHandler.pryMods(stack); // ArmorModHandler.pryMods deleted
					
					// for(int i = 0; i < 8; i++) {
					//
					// 	if(mods != null)
					// 		upgrades.setInventorySlotContents(i, mods[i]);
					// }
					// Simplified: clear upgrade slots if ArmorModHandler system is gone
					for(int i = 0; i < 8; i++) {
						upgrades.setInventorySlotContents(i, ItemStack.EMPTY);
					}
				}
				
				super.putStack(stack);
			}

			@Override
			public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
				//if the armor piece is taken, absorb all armor pieces
				
				for(int i = 0; i < 8; i++) {
					
					ItemStack mod = upgrades.getStackInSlot(i);
					
					//ideally, this should always return true so long as the mod slot is not null due to the insert restriction
					// if(ArmorModHandler.isApplicable(stack, mod)) { // ArmorModHandler.isApplicable deleted
					// 	upgrades.setInventorySlotContents(i, ItemStack.EMPTY);
					// }
				}
				return super.onTake(thePlayer, stack);
			}
		});
		
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 56));
			}
		}
		
		for(int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142 + 56));
		}
		
		this.onCraftMatrixChanged(this.upgrades);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int par2) {
		ItemStack var3 = ItemStack.EMPTY;
		Slot var4 = (Slot) this.inventorySlots.get(par2);

		if(var4 != null && var4.getHasStack()) {
			ItemStack var5 = var4.getStack();
			var3 = var5.copy();

			if(par2 <= 8) {
				ItemStack copy = var5.copy();
				if(!this.mergeItemStack(var5, 9, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				} else {
					var4.onTake(p_82846_1_, copy);
				}
			} else {
				if(var5.getItem() instanceof ItemArmor) {
					if(!this.mergeItemStack(var5, 8, 9, false))
						return ItemStack.EMPTY;
				// } else if(this.inventorySlots.get(8) != null && var5.getItem() instanceof ItemArmorMod) { // ItemArmorMod logic removed
				// 	ItemArmorMod mod = (ItemArmorMod)var5.getItem();
				// 	int slot = mod.type;
				//
				// 	if(((Slot) this.inventorySlots.get(slot)).isItemValid(var5)) {
				// 		if(!this.mergeItemStack(var5, slot, slot + 1, false))
				// 			return ItemStack.EMPTY;
				// 	} else {
				// 		return ItemStack.EMPTY;
				// 	}
				} else {
					return ItemStack.EMPTY;
				}
			}

			if(var5.isEmpty()) {
				var4.putStack(ItemStack.EMPTY);
			} else {
				var4.onSlotChanged();
			}
		}

		return var3;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);

		if(!player.world.isRemote) {
			for(int i = 0; i < this.upgrades.getSizeInventory(); ++i) {
				ItemStack itemstack = this.upgrades.getStackInSlot(i);

				if(itemstack != null) {
					player.dropItem(itemstack, false);
					// ArmorModHandler.removeMod(armor.getStackInSlot(0), i); // ArmorModHandler.removeMod deleted
				}
			}
			
			ItemStack itemstack = this.armor.getStackInSlot(0);
			
			if(itemstack != null) {
				player.dropItem(itemstack, false);
			}
		}
	}
	
	public class UpgradeSlot extends Slot {

		public UpgradeSlot(IInventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			//return armor.getStackInSlot(0) != null && ArmorModHandler.isApplicable(armor.getStackInSlot(0), stack) && ((ItemArmorMod)stack.getItem()).type == this.slotNumber; // ArmorModHandler & ItemArmorMod removed
			return false;
		}
		
		@Override
		public void putStack(ItemStack stack) {
			super.putStack(stack);
			
			//if(stack != null) { // ArmorModHandler removed
			//	if(ArmorModHandler.isApplicable(armor.getStackInSlot(0), stack)) // ArmorModHandler removed
			//		ArmorModHandler.applyMod(armor.getStackInSlot(0), stack); // ArmorModHandler removed
			//}
		}

		@Override
		public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
			//ArmorModHandler.removeMod(armor.getStackInSlot(0), this.slotNumber); // ArmorModHandler removed
			return super.onTake(thePlayer, stack);
		}
		
	}
}