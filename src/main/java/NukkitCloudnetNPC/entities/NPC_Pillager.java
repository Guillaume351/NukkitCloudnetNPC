package NukkitCloudnetNPC.entities;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class NPC_Pillager extends NPC_Entity {

    public static final int NID = 114;

    public NPC_Pillager(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NID;
    }
}
