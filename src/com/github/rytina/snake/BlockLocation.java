package com.github.rytina.snake;


import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class BlockLocation {
	String world;
	int x;
	int y;
	int z;

	public BlockLocation(Block b) {
		this.world = b.getWorld().getName();
		this.x = b.getX();
		this.y = b.getY();
		this.z = b.getZ();
	}

	public BlockLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockLocation(int x, int y, int z, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	public Block getBlock() {
		return Bukkit.getWorld(this.world).getBlockAt(this.x, this.y, this.z);
	}

	public String getWorld() {
		return this.world;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public BlockLocation minus(BlockLocation toMinus) {
		return new BlockLocation(this.x - toMinus.x, this.y - toMinus.y, this.z - toMinus.z, this.world);
	}

	public BlockLocation add(BlockLocation toAdd) {
		return new BlockLocation(this.x + toAdd.x, this.y + toAdd.y, this.z + toAdd.z, this.world);
	}

	public void set(BlockLocation bL) {
		this.x = bL.x;
		this.y = bL.y;
		this.z = bL.z;
	}

	public BlockLocation invert() {
		return new BlockLocation(-this.x, -this.y, -this.z);
	}

	public Boolean equivalent(BlockLocation toTest) {
		return this.x == toTest.getX() && this.y == toTest.getY() && this.z == toTest.getZ() ? true : false;
	}

	public String toString() {
		return "World=" + this.world + "x=" + this.x + "y=" + this.y + "z=" + this.z;
	}
}