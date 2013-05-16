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
}
