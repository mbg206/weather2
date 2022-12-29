package weather2.util;

import java.util.*;

import CoroUtil.util.CoroUtilCompatibility;
import biomesoplenty.api.block.BOPBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Loader;
import weather2.CommonProxy;
import weather2.config.ConfigTornado;

public class WeatherUtil {

	public static HashMap<Block, Boolean> blockIDToUseMapping = new HashMap<Block, Boolean>();
	
    public static boolean isPaused() {
    	if (FMLClientHandler.instance().getClient().isGamePaused()) return true;
    	return false;
    }
    
    public static boolean isPausedSideSafe(World world) {
    	//return false if server side because it cant be paused legit
    	if (!world.isRemote) return false;
    	return isPausedForClient();
    }
    
    public static boolean isPausedForClient() {
    	if (FMLClientHandler.instance().getClient().isGamePaused()) return true;
    	return false;
    }
    
    //Terrain grabbing
    public static boolean shouldGrabBlock(World parWorld, IBlockState state)
    {
        try
        {
        	ItemStack itemStr = new ItemStack(Items.DIAMOND_AXE);

            Block block = state.getBlock();
            
        	boolean result = true;
            
            if (ConfigTornado.Storm_Tornado_GrabCond_List)
            {
            	try {

                    if (!ConfigTornado.Storm_Tornado_GrabListBlacklistMode)
                    {
                        if (!((Boolean)blockIDToUseMapping.get(block)).booleanValue()) {
                        	result = false;
                        }
                    }
                    else
                    {
                        if (((Boolean)blockIDToUseMapping.get(block)).booleanValue()) {
                        	result = false;
                        }
                    }
				} catch (Exception e) {
					//sometimes NPEs, just assume false if so
					result = false;
				}
            } else {

                if (ConfigTornado.Storm_Tornado_GrabCond_StrengthGrabbing)
                {
                    float strMin = 0.0F;
                    float strMax = 0.99F; //0.74F;

                    if (block == null)
                    {
                    	result = false;
                    	return result; //force return false to prevent unchecked future code outside scope
                    } else {

                        IBlockState blockState = (block instanceof BlockLog) ? Blocks.SANDSTONE.getDefaultState() : block.getDefaultState();
    	                float strVsBlock = block.getBlockHardness(blockState, parWorld, new BlockPos(0, 0, 0)) - (((itemStr.getStrVsBlock(blockState) - 1) / 4F));

    	                //System.out.println(strVsBlock);
    	                if (/*block.getHardness() <= 10000.6*/ (strVsBlock <= strMax && strVsBlock >= strMin) ||
                                block.getMaterial(blockState) == Material.WOOD ||
                                block.getMaterial(blockState) == Material.CLOTH ||
                                block.getMaterial(blockState) == Material.PLANTS ||
                                block.getMaterial(blockState) == Material.VINE ||
                                block instanceof BlockTallGrass)
    	                {
    	                    /*if (block.blockMaterial == Material.water) {
    	                    	return false;
    	                    }*/
    	                    if (!safetyCheck(block))
    	                    {
    	                    	result = false;
    	                    }
                            /*else if (Math.random() < strVsBlock - 0.3) {
                                result = false;
                            }*/
    	                } else {
    	                	result = false;
    	                }
    	
    	                
                    }
                }
                
                if (ConfigTornado.Storm_Tornado_RefinedGrabRules) {
                    String registryName = block.getRegistryName().toString();
                	if (
                            block == Blocks.DIRT ||
                            block == Blocks.GRASS ||
                            block == Blocks.SAND ||
                            /*block == Blocks.GRAVEL ||*/
                            (Loader.isModLoaded("biomesoplenty") && (
                                block == BOPBlocks.white_sand ||
                                block == BOPBlocks.grass ||
                                block == BOPBlocks.dirt
                            )) ||
                            (Loader.isModLoaded("dynamictrees") ? block.getRegistryName().toString().matches("^dynamictrees[a-z]*:[a-z]+branchx?$") : block instanceof BlockLog)
                    ) {
                		result = false;
                	}
                	if (!CoroUtilCompatibility.canTornadoGrabBlockRefinedRules(state)) {
                	    result = false;
                    }
                }
            }
            
            if (block == CommonProxy.blockWeatherMachine) {
            	result = false;
            }
            
            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean safetyCheck(Block id)
    {
        if (id != Blocks.BEDROCK && (Loader.isModLoaded("dynamictrees") || id != Blocks.LOG) && id != Blocks.CHEST/* && id != Blocks.JUKEBOX && id != Block.waterMoving.blockID && id != Block.waterStill.blockID */)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public static boolean shouldRemoveBlock(Block blockID)
    {
        /*if (tryFinite)
        {
            try
            {
                if (Class.forName("BlockNWater").isInstance(Block.blocksList[blockID]))
                {
                    return false;
                }

                if (Class.forName("BlockNOcean").isInstance(Block.blocksList[blockID]))
                {
                    return false;
                }

                if (Class.forName("BlockNWater_Pressure").isInstance(Block.blocksList[blockID])) {
                    return false;
                }

                if (Class.forName("BlockNWater_Still").isInstance(Block.blocksList[blockID]))
                {
                    return true;
                }
            }
            catch (Exception exception)
            {
                tryFinite = false;
                return false;
            }
        }*/

        //water no
        if (blockID.getMaterial(blockID.getDefaultState()) == Material.WATER)
        {
            return false;
        }

        return true;
    }
    
    public static boolean isOceanBlock(Block blockID)
    {
        /*if (tryFinite)
        {
            try
            {
                if (Class.forName("BlockNOcean").isInstance(Block.blocksList[blockID]))
                {
                    return true;
                }
            }
            catch (Exception exception)
            {
                tryFinite = false;
                return false;
            }
        }*/

        return false;
    }
    
    public static boolean isSolidBlock(Block id)
    {
        return (id == Blocks.STONE ||
                id == Blocks.COBBLESTONE ||
                id == Blocks.SANDSTONE);	
    }
	
    public static void doBlockList()
    {
    	
    	//System.out.println("1.8 TODO: verify block list lookup matching for exact comparions");
    	
        blockIDToUseMapping.clear();
        //System.out.println("Blacklist: ");
        String[] splEnts = ConfigTornado.Storm_Tornado_GrabList.split(",");
        //int[] blocks = new int[splEnts.length];

        if (splEnts.length > 0) {
	        for (int i = 0; i < splEnts.length; i++)
	        {
	        	splEnts[i] = splEnts[i].trim();
	            //blocks[i] = Integer.valueOf(splEnts[i]);
	            //System.out.println(splEnts[i]);
	        }
        }
        
        boolean dbgShow = false;
        String dbg = "block list: ";

        //HashMap hashmap = null;
        //System.out.println("?!?!" + Block.blocksList.length);
        blockIDToUseMapping.put(Blocks.AIR, false);

        Set set = Block.REGISTRY.getKeys();
        Iterator it = set.iterator();
        while (it.hasNext()) {
        	Object obj = it.next();
        	//String tagName = (String) ((ResourceLocation)obj).toString();
        	ResourceLocation tagName = ((ResourceLocation)obj);
        	
        	
        	Block block = (Block) Block.REGISTRY.getObject(tagName);
        	//if (dbgShow) System.out.println("??? " + Block.REGISTRY.getNameForObject(block));
        	
        	if (block != null)
            {
                boolean foundEnt = false;

                for (int j = 0; j < splEnts.length; j++)
                {
                	if (ConfigTornado.Storm_Tornado_GrabCond_List_PartialMatches) {
                		if (tagName.toString().contains(splEnts[j])) {
                			dbg += Block.REGISTRY.getNameForObject(block) + ", ";
                			foundEnt = true;
                			break;
                		}
                	} else {
	                    Block blockEntry = (Block)Block.REGISTRY.getObject(new ResourceLocation(splEnts[j]));
	
	                    if (blockEntry != null && block == blockEntry)
	                    {
	                        foundEnt = true;
	                        dbg += Block.REGISTRY.getNameForObject(block) + ", ";
	                        //blackList.append(s + " ");
	                        //System.out.println("adding to list: " + blocks[j]);
	                        break;
	                    }
                	}
                }

                blockIDToUseMapping.put(block, foundEnt);
                
                //entList.append(s + " ");
                //if (foundEnt) {
                	//blockIDToUseMapping.put(block, foundEnt);
                //} else {
                	//blockIDToUseMapping.put(block, false);
                //}
            }
            else
            {
                //blockIDToUseMapping.put(block, false);
            }
        	
        	
        }
        
        if (dbgShow) {
        	System.out.println(dbg);
        }
    }

    public static boolean isAprilFoolsDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //test
        //return calendar.get(Calendar.MONTH) == Calendar.MARCH && calendar.get(Calendar.DAY_OF_MONTH) == 25;

        return calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }
    
    
}
