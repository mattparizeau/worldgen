package com.matt.mods.worldgen.world;

import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.*;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import com.matt.mods.worldgen.util.MinMax;

public class ChunkGeneratorCubes implements IChunkProvider {

    private NoiseGeneratorPerlin noiseGenPerlin;
    private double[] stoneNoise;
	private Random rand;
	private World world;
	private BiomeGenBase[] biomes;
	
	public ChunkGeneratorCubes(World world, long seed, boolean mapFeaturesEnabled)
	{
        this.stoneNoise = new double[256];
		this.world = world;
		this.rand = new Random(seed);
        this.noiseGenPerlin = new NoiseGeneratorPerlin(this.rand, 4);
	}
	
	@Override
	public boolean chunkExists(int x, int y) {
		return true;
	}
	
	public MinMax generate(int x, int y, ChunkPrimer primer)
	{
		boolean genChunk = this.rand.nextInt(8) == 0;
		
		int minY = rand.nextInt(224);
		int maxY = minY + 14;
		int topY = maxY + 14;
		
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 256; j++)
			{
				for (int k = 0; k < 16; k++)
				{
					if (genChunk)
					{
						if (i != 0 && i != 15 && k != 0 && k != 15 && j != minY)
						{
							if (j > minY && j < maxY)
							{
								primer.setBlockState(i, j, k, Blocks.stone.getDefaultState());
							}
						} else if (j == minY || (j > minY && j < maxY))
						{
							primer.setBlockState(i, j, k, Blocks.stonebrick.getDefaultState());
						}// else if ((j == topY) || ((j < topY && j >= maxY) && (i == 0 || i == 15 || k == 0 || k == 15)))
						//{
						//	primer.setBlockState(i, j, k, Blocks.glass.getDefaultState());
						//}
					}
				}
			}
		}
		return new MinMax(minY, maxY);
	}
	
	public final void genBiome(BiomeGenBase biome, ChunkPrimer primer, int x, int y, double noise, MinMax minMax)
    {
        boolean flag = true;
        
        IBlockState iblockstate;
        IBlockState iblockstate1;
        if (biome.biomeID == BiomeGenBase.ocean.biomeID)
    	{
    		iblockstate = Blocks.water.getDefaultState();
    		iblockstate1 = Blocks.water.getDefaultState();
    	} else {
            iblockstate = biome.topBlock;
            iblockstate1 = biome.fillerBlock;
    	}
        
        int k = -1;
        int l = (int)(noise / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
        int i1 = x & 15;
        int j1 = y & 15;

        for (int k1 = 255; k1 >= 0; --k1)
        {
            IBlockState iblockstate2 = primer.getBlockState(j1, k1, i1);

            if (iblockstate2.getBlock().getMaterial() == Material.air)
            {
                k = -1;
            }
            else if (iblockstate2.getBlock() == Blocks.stone)
            {
                if (k == -1)
                {
                    if (l <= 0)
                    {
                        iblockstate = null;
                        iblockstate1 = Blocks.stone.getDefaultState();
                    }
                    else if (k1 >= minMax.getMax() - 5 && k1 <= minMax.getMax())
                    {
                    	if (biome.biomeID == BiomeGenBase.ocean.biomeID)
                    	{
                    		iblockstate = Blocks.water.getDefaultState();
                    		iblockstate1 = Blocks.water.getDefaultState();
                    	} else {
	                        iblockstate = biome.topBlock;
	                        iblockstate1 = biome.fillerBlock;
                    	}
                    }

                    if (k1 < minMax.getMax() - 1 && (iblockstate == null || iblockstate.getBlock().getMaterial() == Material.air))
                    {
                        if (biome.temperature < 0.15F)
                        {
                            iblockstate = Blocks.ice.getDefaultState();
                        }
                        else
                        {
                            iblockstate = Blocks.water.getDefaultState();
                        }
                    }

                    k = l;

                    if (k1 >= minMax.getMax() - 2)
                    {
                        primer.setBlockState(j1, k1, i1, iblockstate);
                    }
                    else if (k1 < minMax.getMax() - 8 - l)
                    {
                        iblockstate = null;
                        iblockstate1 = Blocks.stone.getDefaultState();
                        primer.setBlockState(j1, k1, i1, Blocks.gravel.getDefaultState());
                    }
                    else
                    {
                        primer.setBlockState(j1, k1, i1, iblockstate1);
                    }
                }
                else if (k > 0)
                {
                    --k;
                    primer.setBlockState(j1, k1, i1, iblockstate1);

                    if (k == 0 && iblockstate1.getBlock() == Blocks.sand)
                    {
                        k = this.rand.nextInt(4) + Math.max(0, k1 - minMax.getMax() - 1);
                        iblockstate1 = iblockstate1.getValue(BlockSand.VARIANT_PROP) == BlockSand.EnumType.RED_SAND ? Blocks.red_sandstone.getDefaultState() : Blocks.sandstone.getDefaultState();
                    }
                }
            }
        }
    }
	
	public void replaceBiomeBlocks(int x, int y, ChunkPrimer primer, BiomeGenBase[] biomes, MinMax minMax)
	{
		ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, x, y, primer, this.world);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Result.DENY) return;
        
        double d0 = 0.03125D;
        this.stoneNoise = this.noiseGenPerlin.func_151599_a(this.stoneNoise, (double)(x * 16), (double)(y * 16), 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);
        
        for (int i = 0; i < 16; i++)
        {
    		for (int j = 0; j < 16; j++)
    		{
    			BiomeGenBase biome = biomes[i + j * 16];
    			genBiome(biome, primer, x * 16 + i, y * 16 + j, this.stoneNoise[i + j * 16], minMax);
        	}
        }
	}
	
	/**
	 * Sets the biome at the current location
	 * @param biome list
	 * @param x - x position
	 * @param y - y position
	 * @param biome id
	 */
	public void setBiome(int x, int y, BiomeGenBase biome)
	{
		this.biomes[x + y * 16] = biome;
	}
	
	public void setChunkBiome(int x, int y, BiomeGenBase biome)
	{
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				this.setBiome(i, j, biome);
			}
		}
	}
	
	public BiomeGenBase getRandomBiome()
	{
		int r = this.rand.nextInt(8);
		
		switch (r)
		{
		case 0:
			return BiomeGenBase.forest;
		case 1:
			return BiomeGenBase.swampland;
		case 2:
			return BiomeGenBase.desert;
		case 3:
			return BiomeGenBase.mushroomIsland;
		case 4:
			return BiomeGenBase.mesa;
		case 5:
			return BiomeGenBase.birchForest;
		case 6:
			return BiomeGenBase.ocean;
		}
		
		return BiomeGenBase.plains;
	}
	
	public void setRandomBiome(int x, int y)
	{
		setChunkBiome(x, y, getRandomBiome());
	}

	@Override
	public Chunk provideChunk(int x, int y) {
		this.rand.setSeed((long)x * 341873128712L + (long)y * 132897987541L);
		ChunkPrimer primer = new ChunkPrimer();
		MinMax minMax = this.generate(x, y, primer);
		this.biomes = this.world.getWorldChunkManager().loadBlockGeneratorData(this.biomes, x * 16, y * 16, 16, 16);
		this.setRandomBiome(x, y);
		this.replaceBiomeBlocks(x, y, primer, this.biomes, minMax);
		
		Chunk chunk = new Chunk(this.world, primer, x, y);
		byte[] abyte = chunk.getBiomeArray();
		
		for (int i = 0; i < abyte.length; ++i)
		{
			abyte[i] = (byte)this.biomes[i].biomeID;
		}
		
		chunk.generateSkylightMap();
		return chunk;
	}

	// Get Chunk
	@Override
	public Chunk func_177459_a(BlockPos pos) {
		return this.provideChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	@Override
	public void populate(IChunkProvider provider, int x, int y) {
        BlockFalling.fallInstantly = true;
        int x2 = x * 16;
        int y2 = y * 16;
        BlockPos pos = new BlockPos(x2, 0, y2);
        BiomeGenBase biome = this.world.getBiomeGenForCoords(pos.add(16, 0, 16));
        this.rand.setSeed(this.world.getSeed());
        long s1 = rand.nextLong() / 2L * 2L + 1L;
        long s2 = rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)x * s1 + (long)y * s2 ^ this.world.getSeed());
        boolean spawnAnimals = false;
        ChunkCoordIntPair chunkCoordPair = new ChunkCoordIntPair(x, y);
        
        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(provider, this.world, this.rand, x, y, spawnAnimals));
        
        biome.func_180624_a(this.world, this.rand, new BlockPos(x2, 0, y2));
        if (TerrainGen.populate(provider, this.world, this.rand, x, y, spawnAnimals, ANIMALS))
        {
        	SpawnerAnimals.performWorldGenSpawning(this.world, biome, x2 + 8, y2 + 8, 16, 16, this.rand);
        }
        pos = pos.add(8, 0, 8);
        
        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(provider, this.world, this.rand, x, y, spawnAnimals));
        
        BlockFalling.fallInstantly = false;
	}

	// Get Structure
	@Override
	public boolean func_177460_a(IChunkProvider provider, Chunk chunk, int x, int y) {
		return false;
	}

	@Override
	public boolean saveChunks(boolean flag, IProgressUpdate progressUpdate) {
		return true;
	}

	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public String makeString() {
		return "RandomLevelSourceCustom";
	}
	
	// Do Mob Spawning
	@SuppressWarnings("rawtypes")
	@Override
	public List func_177458_a(EnumCreatureType type, BlockPos pos) {
		BiomeGenBase biome = this.world.getBiomeGenForCoords(pos);
		
		return biome.getSpawnableList(type);
	}

	// Get Structure Location
	@Override
	public BlockPos func_180513_a(World world, String structureName, BlockPos pos) {
		return null;
	}

	@Override
	public int getLoadedChunkCount() {
		return 0;
	}

	// Generate Structures
	@Override
	public void func_180514_a(Chunk chunk, int x, int y) {

	}

	@Override
	public void saveExtraData() {}

}
