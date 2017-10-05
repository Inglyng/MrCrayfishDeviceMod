package com.mrcrayfish.device.tileentity;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.util.TileEntityUtil;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLaptop extends TileEntity implements ITickable
{
	public float rotation = 0;
	public float prevRotation = 0;
	public boolean open = false;
	
	private NBTTagCompound data;
	private FileSystem fileSystem;
	
	public TileEntityLaptop() 
	{
		this.data = new NBTTagCompound();
	}
	
	public NBTTagCompound getAppData() 
	{
		return data;
	}
	
	public void setAppData(NBTTagCompound data) 
	{
		this.data = data;
		markDirty();
		TileEntityUtil.markBlockForUpdate(world, pos);
	}
	
	public void openClose()
	{
		open = !open;
		TileEntityUtil.markBlockForUpdate(world, pos);
	}
	
	@Override
	public void update() 
	{
		prevRotation = rotation;
		if(!open)
		{
			if(rotation > 0)
			{
				rotation -= 10F;
			}
		}
		else
		{
			if(rotation < 110)
			{
				rotation += 10F;
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) 
	{
		super.readFromNBT(compound);
		this.open = compound.getBoolean("open");
		this.data = compound.getCompoundTag("data");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		super.writeToNBT(compound);
		compound.setBoolean("open", open);
		compound.setTag("data", data);
		return compound;
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), this.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public NBTTagCompound getUpdateTag() 
	{
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public double getMaxRenderDistanceSquared() 
	{
		return 16384;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}

    public NBTTagCompound getApplicationData()
    {
        return createAndGetTag("application");
    }

	public NBTTagCompound getSystemData()
	{
		return createAndGetTag("system");
	}

	public FileSystem getFileSystem()
	{
		if(fileSystem == null)
		{
			fileSystem = FileSystem.fromTag(createAndGetTag("file_system"));
		}
		return fileSystem;
	}

	public void setApplicationData(String appId, NBTTagCompound appData)
	{
		createAndGetTag("application").setTag(appId, appData);
		markDirty();
	}

	public void setSystemData(NBTTagCompound systemData)
	{
		data.setTag("system", systemData);
		markDirty();
	}

	public void setFileSystemData(NBTTagCompound fileSystemData)
	{
		data.setTag("file_system", fileSystemData);
		markDirty();
	}

	private NBTTagCompound createAndGetTag(String name)
	{
		if(!data.hasKey(name))
		{
			data.setTag(name, new NBTTagCompound());
			markDirty();
		}
		return data.getCompoundTag(name);
	}
}
