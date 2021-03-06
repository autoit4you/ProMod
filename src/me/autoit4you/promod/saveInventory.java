package me.autoit4you.promod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;

import net.minecraft.server.v1_5_R3.NBTBase;
import net.minecraft.server.v1_5_R3.NBTTagCompound;
import net.minecraft.server.v1_5_R3.NBTTagList;

import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/*
 * @author yukinoraru
 */
public class saveInventory {
	
	public String saveInventorytoString(Inventory inventory){
		ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(arrayStream);
		NBTTagList contentsList = new NBTTagList();
		
		for(int i=0; i<inventory.getSize(); i++){
			NBTTagCompound oObject = new NBTTagCompound();
			CraftItemStack craft = getItem(inventory.getItem(i));
			if(craft != null)
				CraftItemStack.asNMSCopy(craft).save(oObject);
			contentsList.add(oObject);
		}
		NBTBase.a(contentsList, dataStream);
		return new BigInteger(1, arrayStream.toByteArray()).toString(32);
	}
	
	public Inventory getInventoryfromString(String data){
		ByteArrayInputStream arrayStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
		NBTTagList contentsList = (NBTTagList) NBTBase.b(new DataInputStream(arrayStream));
		Inventory inventory = new CraftInventoryCustom(null, contentsList.size());
		
		for(int i=0; i<contentsList.size(); i++){
			NBTTagCompound iObject = (NBTTagCompound) contentsList.get(i);
			if(!iObject.isEmpty()){
				inventory.setItem(i, CraftItemStack.asCraftMirror(
						net.minecraft.server.v1_5_R3.ItemStack.createStack(iObject)));
			}
		}
		return inventory;
	}
	
	public CraftItemStack getItem(ItemStack stack){
		if(stack instanceof CraftItemStack)
			return (CraftItemStack) stack;
		else if(stack != null)
			return CraftItemStack.asCraftCopy(stack);
		else
			return null;
	}

	public String saveArmortoString(ItemStack head, ItemStack chest, ItemStack leggings, ItemStack boots) {
		ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(arrayStream);
		NBTTagList contentsList = new NBTTagList();
		
		
		NBTTagCompound oObject = new NBTTagCompound();
		CraftItemStack craft = getItem(head);
		if(craft != null)
			CraftItemStack.asNMSCopy(craft).save(oObject);
		contentsList.add(oObject);
		
		oObject = new NBTTagCompound();
		craft = getItem(chest);
		if(craft != null)
			CraftItemStack.asNMSCopy(craft).save(oObject);
		contentsList.add(oObject);
		
		oObject = new NBTTagCompound();
		craft = getItem(leggings);
		if(craft != null)
			CraftItemStack.asNMSCopy(craft).save(oObject);
		contentsList.add(oObject);
		
		oObject = new NBTTagCompound();
		craft = getItem(boots);
		if(craft != null)
			CraftItemStack.asNMSCopy(craft).save(oObject);
		contentsList.add(oObject);
		
		NBTBase.a(contentsList, dataStream);
		return new BigInteger(1, arrayStream.toByteArray()).toString(32);
	}

	public Inventory getArmorfromString(String data) {
		ByteArrayInputStream arrayStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
		NBTTagList contentsList = (NBTTagList) NBTBase.b(new DataInputStream(arrayStream));
		Inventory inventory = new CraftInventoryCustom(null, contentsList.size());
		
		for(int i=0; i<contentsList.size(); i++){
			NBTTagCompound iObject = (NBTTagCompound) contentsList.get(i);
			if(!iObject.isEmpty()){
				inventory.setItem(i, CraftItemStack.asCraftMirror(
						net.minecraft.server.v1_5_R3.ItemStack.createStack(iObject)));
			}
		}
		return inventory;
	}
	
	public String saveHealthtoString(Player player) {
		int[] result = new int[7];
		
		result[0] = player.getHealth();
		result[1] = (int) player.getExp();
		result[2] = player.getFireTicks();
		result[3] = player.getFoodLevel();
		result[4] = (int) player.getLocation().getX();
		result[5] = (int) player.getLocation().getY();
		result[6] = (int) player.getLocation().getZ();
		
		return result.toString();
	}

	public void restorePlayerfromString(String data) {
		// TODO Auto-generated method stub
		
	}
}
