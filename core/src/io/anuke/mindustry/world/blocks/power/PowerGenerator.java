package io.anuke.mindustry.world.blocks.power;

import io.anuke.arc.Core;
import io.anuke.arc.collection.EnumSet;
import io.anuke.arc.util.Strings;
import io.anuke.mindustry.entities.type.TileEntity;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.ui.Bar;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.consumers.ConsumePower;
import io.anuke.mindustry.world.meta.BlockFlag;
import io.anuke.mindustry.world.meta.BlockStat;
import io.anuke.mindustry.world.meta.StatUnit;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PowerGenerator extends PowerDistributor{
    /** The amount of power produced per tick in case of an efficiency of 1.0, which represents 100%. */
    protected float powerProduction;
    public BlockStat generationType = BlockStat.basePowerGeneration;

    public PowerGenerator(String name){
        super(name);
        baseExplosiveness = 5f;
        flags = EnumSet.of(BlockFlag.producer);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond);
    }

    @Override
    public void setBars(){
        super.setBars();

        if(hasPower && outputsPower && !consumes.has(ConsumePower.class)){
            bars.add("power", entity -> new Bar(() ->
            Core.bundle.format("blocks.poweroutput",
            Strings.toFixed(entity.tile.block().getPowerProduction(entity.tile)*60 * entity.timeScale, 1)),
            () -> Pal.powerBar,
            () -> ((GeneratorEntity)entity).productionEfficiency));
        }
    }

    @Override
    public float getPowerProduction(Tile tile){
        return powerProduction * tile.<GeneratorEntity>entity().productionEfficiency;
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public TileEntity newEntity(){
        return new GeneratorEntity();
    }

    public static class GeneratorEntity extends TileEntity{
        public float generateTime;
        /** The efficiency of the producer. An efficiency of 1.0 means 100% */
        public float productionEfficiency = 0.0f;

        @Override
        public void write(DataOutput stream) throws IOException{
            stream.writeFloat(productionEfficiency);
        }

        @Override
        public void read(DataInput stream) throws IOException{
            productionEfficiency = stream.readFloat();
        }
    }
}
