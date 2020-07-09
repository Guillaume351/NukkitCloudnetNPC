package main.java;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.network.protocol.ScriptCustomEventPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceInfoUpdateEvent;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import main.java.entities.NPC_Enderman;
import main.java.entities.NPC_IronGolem;
import main.java.entities.NPC_Villager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class Main extends PluginBase implements Listener {



    String fillingMb;
    String fillingBb;
    String fillingSw;

    NPC_IronGolem mbNpc;
    NPC_Villager bbNpc;
    NPC_Enderman swNpc;

    int mbCounter = 0;
    int bbCounter = 0;
    int swCounter = 0;
    int lobbyCounter = 0;


    @Override
    public void onEnable() {
        super.onEnable();

        // Registering the listeners
        //   this.getServer().getPluginManager().registerEvent(CloudServiceInfoUpdateEvent);
        this.getServer().getPluginManager().registerEvents(this, this);
        // this.getServer().getDefaultLevel().loadChunk(206,170);
        CloudNetDriver.getInstance().getEventManager().registerListener(this);

        this.getServer().getDefaultLevel().loadChunk(166, 170);
        // mbNpc = new NPC_Human(this.getServer().getDefaultLevel().getChunk(206,170), this.createNBT("MicroBattle", this.getSkin("microbattle"), new Vector3(206.5,56,170.5), Item.get(Item.WOODEN_SWORD)));
        //       // bbNpc = new NPC_Human(this.getServer().getDefaultLevel().getChunk(206,170), this.createNBT("BuildBattle", this.getSkin("buildbattle"), new Vector3(206.5,56,164.5), Item.get(Item.PLANK)));
        mbNpc = new NPC_IronGolem(this.getServer().getDefaultLevel().getChunk(166, 170), this.createNBT("MicroBattle", this.getSkin("microbattle"), new Vector3(170.5, 57, 166.5), Item.get(Item.WOODEN_SWORD)));
        bbNpc = new NPC_Villager(this.getServer().getDefaultLevel().getChunk(166, 170), this.createNBT("BuildBattle", this.getSkin("buildbattle"), new Vector3(172.5, 57, 176.5), Item.get(Item.PLANK)));
        bbNpc.setNameTagAlwaysVisible(true);
        mbNpc.setNameTagAlwaysVisible(true);
        mbNpc.spawnToAll();
        bbNpc.spawnToAll();

        swNpc = new NPC_Enderman(this.getServer().getDefaultLevel().getChunk(166, 170), this.createNBT("SkyWars", this.getSkin("buildbattle"), new Vector3(168.5, 57, 176.5), Item.get(Item.ENDER_PEARL)));
        swNpc.setNameTagAlwaysVisible(true);
        swNpc.spawnToAll();
    }

    public CompoundTag createNBT(String name, Skin skin, Vector3 p, Item itemInHand){

            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<>("Pos")
                            .add(new DoubleTag("", p.x))
                            .add(new DoubleTag("", p.y))
                            .add(new DoubleTag("", p.z)))
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("", (float) 90))
                            .add(new FloatTag("", (float) 0)))
                    .putBoolean("Invulnerable", true)
                    .putString("NameTag", name)
                    .putList(new ListTag<StringTag>("Commands"))
                    .putList(new ListTag<StringTag>("PlayerCommands"))
                    .putBoolean("npc", true)
                    .putFloat("scale", (float) 1);
            if (false) {//TODO : remove if
                CompoundTag skinTag = new CompoundTag()
                        .putByteArray("Data", skin.getSkinData().data)
                        .putInt("SkinImageWidth", skin.getSkinData().width)
                        .putInt("SkinImageHeight", skin.getSkinData().height)
                        .putString("ModelId", skin.getSkinId())
                        .putString("CapeId", skin.getCapeId())
                        .putByteArray("CapeData", skin.getCapeData().data)
                        .putInt("CapeImageWidth", skin.getCapeData().width)
                        .putInt("CapeImageHeight", skin.getCapeData().height)
                        .putByteArray("SkinResourcePatch", skin.getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                        .putByteArray("GeometryData", skin.getGeometryData().getBytes(StandardCharsets.UTF_8))
                        .putByteArray("AnimationData", skin.getAnimationData().getBytes(StandardCharsets.UTF_8))
                        .putBoolean("PremiumSkin", skin.isPremium())
                        .putBoolean("PersonaSkin", skin.isPersona())
                        .putBoolean("CapeOnClassicSkin", skin.isCapeOnClassic());
                nbt.putCompound("Skin", skinTag);
                nbt.putBoolean("ishuman", true);
                nbt.putString("Item", itemInHand.getName());
               // nbt.putString("Helmet", p.getInventory().getHelmet().getName());
               // nbt.putString("Chestplate", p.getInventory().getChestplate().getName());
               // nbt.putString("Leggings", p.getInventory().getLeggings().getName());
                //nbt.putString("Boots", p.getInventory().getBoots().getName());*/
            }
            return nbt;

    }

    public void refreshMbNpc() {

        String fillingInfo = "No game available...";
        ServiceInfoSnapshot snapshot = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByName(this.fillingMb);
        if (snapshot != null) {
            fillingInfo = this.fillingMb + " " + snapshot.getProperty(BridgeServiceProperty.STATE).orElse("") + " " + snapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0) + "/" + snapshot.getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(0);
        } else {
            this.fillingBb = null;
        }

        mbNpc.setNameTag(TextFormat.BOLD.toString() + TextFormat.AQUA + "Micro" + TextFormat.LIGHT_PURPLE + "Battle" + TextFormat.RESET + "\n"
                + TextFormat.GREEN + mbCounter + " players online" + "\n" + TextFormat.BOLD + "TAP TO JOIN!" +
                TextFormat.RESET + "\n" + fillingInfo);
    }

    public void refreshBbNpc(){
        String fillingInfo = "No game available...";

        ServiceInfoSnapshot snapshot = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByName(this.fillingBb);
        if(snapshot != null){
            fillingInfo = this.fillingBb + " " + snapshot.getProperty(BridgeServiceProperty.STATE).orElse("") +" " + snapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0) + "/"+snapshot.getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(0);
        }else{
            this.fillingBb = null;
        }

        bbNpc.setNameTag(TextFormat.BOLD.toString() + TextFormat.AQUA + "Build" + TextFormat.LIGHT_PURPLE + "Battle" + TextFormat.RESET + "\n"
                + TextFormat.GREEN + bbCounter + " players online" + "\n" + TextFormat.BOLD + "TAP TO JOIN!" +
                TextFormat.RESET + "\n" + fillingInfo);
    }


    public void refreshSwNpc() {
        String fillingInfo = "No game available...";

        ServiceInfoSnapshot snapshot = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByName(this.fillingSw);
        if (snapshot != null) {
            fillingInfo = this.fillingSw + " " + snapshot.getProperty(BridgeServiceProperty.STATE).orElse("") + " " + snapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0) + "/" + snapshot.getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(0);
        } else {
            this.fillingSw = null;
        }

        swNpc.setNameTag(TextFormat.BOLD.toString() + TextFormat.DARK_BLUE + "Sky" + TextFormat.LIGHT_PURPLE + "Wars" + TextFormat.GREEN + " BETA" + TextFormat.RESET + "\n"
                + TextFormat.GREEN + swCounter + " players online" + "\n" + TextFormat.BOLD + "TAP TO JOIN!" +
                TextFormat.RESET + "\n" + fillingInfo);
    }


    @EventHandler
    public void onPlayerInteractWithEntity(PlayerInteractEntityEvent event) {
        if (event.getEntity() == this.swNpc) {
            event.setCancelled();
            if (fillingSw != null) {
                ((Player) event.getPlayer()).sendMessage(TextFormat.GREEN + "> Transferring...");
                this.proxyTransfer((Player) event.getPlayer(), this.fillingSw);
            } else {
                ((Player) event.getPlayer()).sendMessage(TextFormat.GREEN + "> No game found! Please wait or contact @Guillaume351 on Twitter if the problem still persist!");
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player && event.getEntity() == this.mbNpc) {
                event.setCancelled();
                if (fillingMb != null) {
                    ((Player) ((EntityDamageByEntityEvent) event).getDamager()).sendMessage(TextFormat.GREEN + "> Transferring...");
                    this.proxyTransfer((Player) ((EntityDamageByEntityEvent) event).getDamager(), this.fillingMb);
                } else {
                    ((Player) ((EntityDamageByEntityEvent) event).getDamager()).sendMessage(TextFormat.GREEN + "> No game found! Please wait or contact @Guillaume351 on Twitter if the problem still persist!");
                }
            }
            if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player && event.getEntity() == this.bbNpc) {
                event.setCancelled();
                if (fillingBb != null) {
                    ((Player) ((EntityDamageByEntityEvent) event).getDamager()).sendMessage(TextFormat.GREEN + "> Transferring...");
                    this.proxyTransfer((Player) ((EntityDamageByEntityEvent) event).getDamager(), this.fillingBb);
                } else {
                    ((Player) ((EntityDamageByEntityEvent) event).getDamager()).sendMessage(TextFormat.GREEN + "> No game found! Please wait or contact @Guillaume351 on Twitter if the problem still persist!");
                }

            }

            if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player && event.getEntity() == this.swNpc) {
                event.setCancelled();
                if (fillingSw != null) {
                    ((Player) ((EntityDamageByEntityEvent) event).getDamager()).sendMessage(TextFormat.GREEN + "> Transferring...");
                    this.proxyTransfer((Player) ((EntityDamageByEntityEvent) event).getDamager(), this.fillingSw);
                } else {
                    ((Player) ((EntityDamageByEntityEvent) event).getDamager()).sendMessage(TextFormat.GREEN + "> No game found! Please wait or contact @Guillaume351 on Twitter if the problem still persist!");
                }

            }

        }

    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (event.getChunk() == this.swNpc.getChunk() || event.getChunk() == this.mbNpc.getChunk()) {
            event.setCancelled();
        }
    }


    public boolean proxyTransfer(Player p, String destination) {
        ScriptCustomEventPacket pk = new ScriptCustomEventPacket();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream a = new DataOutputStream(out);
        try {
            a.writeUTF("Connect");
            a.writeUTF(destination);
            pk.eventName = "bungeecord:main";
            pk.eventData = out.toByteArray();
            p.dataPacket(pk);
        } catch (Exception e) {
            this.getServer().getLogger().warning("Error while transferring ( PLAYER: " + this.getName() + " | DEST: " + destination + " )");
            this.getServer().getLogger().logException(e);
            return false;
        }
        return true;
    }



    public Skin getSkin(String name){
        Skin skin = new Skin();

       Path skinFolderPath = getDataFolder().toPath().resolve(name);
       Path skinGeometryPath = skinFolderPath.resolve("geometry.json");
        Path skinPath = skinFolderPath.resolve("skin.png");

        if (Files.notExists(skinFolderPath) || !Files.isDirectory(skinFolderPath) ||
                Files.notExists(skinGeometryPath) || !Files.isRegularFile(skinGeometryPath) ||
                Files.notExists(skinPath) || !Files.isRegularFile(skinPath)) {
           // throw new SkinChangeException("Skin does not exist");
            this.getServer().getLogger().error("Skin not found ! " + name);
        }

        String geometry;
        BufferedImage skinData;
        try {
            geometry = new String(Files.readAllBytes(skinGeometryPath), StandardCharsets.UTF_8);
            skinData = ImageIO.read(skinPath.toFile());
            skin.setGeometryData(geometry);
            skin.setGeometryName("geometry." + name);
            skin.setSkinData(skinData);
            skin.setSkinId(name);
            skin.setPremium(true);



            return skin;
        } catch (IOException e) {
        //    throw new SkinChangeException("Error loading data", e);
            this.getServer().getLogger().error("Skin not loaded ! " + name);
        }


        return null;
      //  Skin oldSkin = player.getSkin();

/*        player.setSkin(skin);

        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.skin = skin;
        packet.newSkinName = name;
        packet.oldSkinName = oldSkin.getSkinId();
        packet.uuid = player.getUniqueId();

        Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), packet);*/
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            switch (command.getName()) {
                case "joinmb":
                    if (fillingMb != null) {
                        this.proxyTransfer((Player) sender, this.fillingMb);
                    } else {
                        ((Player) sender).sendMessage(TextFormat.GREEN + "> No game found! Please wait or contact @Guillaume351 on Twitter if the problem still persist!");
                    }
                    return true;


                case "joinbb":
                    if (fillingBb != null) {
                        this.proxyTransfer((Player) sender, this.fillingBb);
                    } else {
                        ((Player) sender).sendMessage(TextFormat.GREEN + "> No game found! Please wait or contact @Guillaume351 on Twitter if the problem still persist!");
                    }
                    return true;

                case "joinsw":
                    if (fillingSw != null) {
                        this.proxyTransfer((Player) sender, this.fillingSw);
                    } else {
                        ((Player) sender).sendMessage(TextFormat.GREEN + "> No game found! Please wait or contact @Guillaume351 on Twitter if the problem still persist!");
                    }
                    return true;

                default:
                    break;
            }
        }


        return super.onCommand(sender, command, label, args);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().distance(this.mbNpc.getPosition()) < 32) {

        }
    }

    @EventHandler
    public void onItemPickup(InventoryPickupItemEvent event) {
        event.setCancelled();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // this.mbNpc.spawnTo(event.getPlayer());
        this.swNpc.spawnTo(event.getPlayer());
        //   event.getPlayer().sendAllInventories();
    }

    @EventListener
    public void onCloudServiceInfoUpdateEvent(CloudServiceInfoUpdateEvent event) {
        Collection<ServiceInfoSnapshot> services = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices();
        if (CloudNetDriver.getInstance() != null && services != null) {
            String name = event.getServiceInfo().getName();

            if (name.equals(fillingMb) && (!event.getServiceInfo().isConnected() || !event.getServiceInfo().getProperty(BridgeServiceProperty.STATE).orElse("").contains("OPEN"))) {
                findNewMb();
            }

            if (name.equals(fillingBb) && (!event.getServiceInfo().isConnected() || !event.getServiceInfo().getProperty(BridgeServiceProperty.STATE).orElse("").contains("OPEN"))) {
                findNewBb();
            }

            if (name.equals(fillingSw) && (!event.getServiceInfo().isConnected() || !event.getServiceInfo().getProperty(BridgeServiceProperty.STATE).orElse("").contains("OPEN"))) {
                findNewSw();
            }

            if (fillingMb == null) {
                findNewMb();
            }

            if (fillingBb == null) {
                findNewBb();
            }

            if (fillingSw == null) {
                findNewSw();
            }


            bbCounter = 0;
            for (ServiceInfoSnapshot serviceInfoSnapshot : services) {
                if (serviceInfoSnapshot.getName().contains("BuildBattle")) {
                    bbCounter += serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0);
                }
            }

            swCounter = 0;
            for (ServiceInfoSnapshot serviceInfoSnapshot : services) {
                if (serviceInfoSnapshot.getName().contains("Skywars")) {
                    swCounter += serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0);
                }
            }

            mbCounter = 0;
            for (ServiceInfoSnapshot serviceInfoSnapshot : services) {
                if (serviceInfoSnapshot.getName().contains("MicroBattle")) {
                    mbCounter += serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0);
                }
            }

            refreshBbNpc();
            refreshMbNpc();
            refreshSwNpc();

           /*
           lobbyCounter = 0;
           for (ServiceInfoSnapshot serviceInfoSnapshot : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices("Lobby")) {
               lobbyCounter += serviceInfoSnapshot.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0);
           }*/
        }
    }

    public void findNewMb() {

        this.fillingMb = null;
        Collection<ServiceInfoSnapshot> services = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices();
        if (CloudNetDriver.getInstance() != null && services != null) {
            for (ServiceInfoSnapshot serviceInfoSnapshot : services) {
                if (serviceInfoSnapshot.isConnected() && serviceInfoSnapshot.getName().contains("MicroBattle")) {
                    if (serviceInfoSnapshot.getProperty(BridgeServiceProperty.STATE).orElse("").contains("OPEN")) {
                        fillingMb = serviceInfoSnapshot.getName();
                    }
                }
            }
        }



    }

    public void findNewBb() {
        this.fillingBb = null;
        Collection<ServiceInfoSnapshot> services = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices();
        if (CloudNetDriver.getInstance() != null && services != null) {
            for (ServiceInfoSnapshot serviceInfoSnapshot : services) {
                if (serviceInfoSnapshot.isConnected() && serviceInfoSnapshot.getName().contains("BuildBattle")) {
                    if (serviceInfoSnapshot.getProperty(BridgeServiceProperty.STATE).orElse("").contains("OPEN")) {
                        fillingBb = serviceInfoSnapshot.getName();
                    }
                }
            }
        }
    }


    public void findNewSw() {
        this.fillingSw = null;
        Collection<ServiceInfoSnapshot> services = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices();
        if (CloudNetDriver.getInstance() != null && services != null) {
            for (ServiceInfoSnapshot serviceInfoSnapshot : services) {
                if (serviceInfoSnapshot.isConnected() && serviceInfoSnapshot.getName().contains("Skywars")) {
                    if (serviceInfoSnapshot.getProperty(BridgeServiceProperty.STATE).orElse("").contains("OPEN")) {
                        fillingSw = serviceInfoSnapshot.getName();
                    }
                }
            }
        }
    }


}
