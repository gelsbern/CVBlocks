package org.cubeville.cvblocks.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;

import org.cubeville.cvblocks.BlockToolUtil;
import org.cubeville.cvblocks.WeightedMaterial;

public class SaveRegion extends BaseCommand
{
    public SaveRegion() {
        super("saveregion");
        addBaseParameter(new CommandParameterString()); // world name
        addBaseParameter(new CommandParameterString()); // region name
        addBaseParameter(new CommandParameterString()); // filename
        addOptionalBaseParameter(new CommandParameterWeightedMaterialList());
        setPermission("cvtools.saveregion");
    }

    public CommandResponse execute(CommandSender commandSender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
        throws CommandExecutionException {

        World world = Bukkit.getWorld((String) baseParameters.get(0));
        if(world == null) throw new CommandExecutionException("World does not exist!");
        
        String regionName = (String) baseParameters.get(1);

        Set<Material> filteredMaterials = null;
        if(baseParameters.size() == 4) {
            filteredMaterials = new HashSet<>();
            for(WeightedMaterial wm: (List<WeightedMaterial>) baseParameters.get(3)) {
                filteredMaterials.add(wm.getMaterial());
            }
        }
        BlockToolUtil.saveRegionToFile(world, regionName, (String) baseParameters.get(2), filteredMaterials);
        return null;
    }
}
