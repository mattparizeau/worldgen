package com.matt.mods.worldgen.worldtypes;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

import com.matt.mods.worldgen.world.ChunkGeneratorCubes;
import com.matt.mods.worldgen.world.ChunkManagerCubes;

public class WorldTypeCubes extends WorldType {

	public WorldTypeCubes(String name) {
		super(name);
	}
	
	// Chunk Manager
	@Override
	public WorldChunkManager getChunkManager(World world) {
		return new ChunkManagerCubes(world);
	}
	
	// World Generator
	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
		return new ChunkGeneratorCubes(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled());
	}
	
	// Size of spawn area
	@Override
	public int getSpawnFuzz() {
		return 2;
	}
	
	@Override
	public float getCloudHeight() {
		return 256;
	}
	
	@Override
	public double getHorizon(World world) {
		return 0.0D;
	}
	
	@Override
	public double voidFadeMagnitude() {
		return 1.0D;
	}
	
}
