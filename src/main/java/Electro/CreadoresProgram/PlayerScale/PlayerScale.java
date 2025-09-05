package Electro.CreadoresProgram.PlayerScale;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.response.FormResponseCustom;

import java.util.List;
import java.util.ArrayList;
public class PlayerScale extends PluginBase implements Listener {
    @Override
    public void onEnable() {
        this.getServer().getCommandMap().register("size", new EntryCommand("size", "Change your player size", "/size", new String[]{ "scale" }));
        this.getServer().getPluginManager().registerEvents(this, this);
    }
    public FormWindowCustom scaleForm(){
        List<String> list = new ArrayList<>();
        for(Player player : getServer().getOnlinePlayers().values()){
            list.add(player.getName());
        }
        List<Element> elements = new ArrayList<>();
        elements.add(new ElementDropdown("Select a Player", list));
        elements.add(new ElementInput("Select a Size", "1.0"));
        return new FormWindowCustom("§lScale a Player", elements);
    }
    @EventHandler
    public void onPlayerFormRespondedEvent(PlayerFormRespondedEvent event){
        Player sumitter = event.getPlayer();
        FormWindow win = event.getWindow();
        if(!(win instanceof FormWindowCustom)){
            return;
        }
        if(event.getResponse() == null){
            return;
        }
        if(event.wasClosed()){
            return;
        }
        FormWindowCustom custom = (FormWindowCustom) win;
        if(custom.getTitle() != "§lScale a Player"){
            return;
        }
        String playerName = ((FormResponseCustom) event.getResponse()).getDropdownResponse(0).getElementContent();
        if(getServer().getPlayerExact(playerName) == null){
            sumitter.sendMessage("§l§cERROR: §r§aYou have selected an invalid Player.");
            return;
        }
        float scale;
        try{
            scale = Float.parseFloat(((FormResponseCustom) event.getResponse()).getInputResponse(1));
            if(scale < 0.5f || scale > 5.0f){
                sumitter.sendMessage("§l§cERROR: §r§aYou have entered an invalid Size. Chose a size between 0.5-5.");
                return;
            }
        }catch (NumberFormatException e){
            sumitter.sendMessage("§l§cERROR: §r§aYou have entered an invalid Size. Chose a size between 0.5-5.");
            return;
        }
        Player player = getServer().getPlayerExact(playerName);
        player.setScale(scale);
        if(player.getName() != sumitter.getName()){
            sumitter.sendMessage("§aYou have set "+player.getName()+"'s player size to "+scale);
            player.sendMessage("§aYour player size has been set to "+scale);
        }else{
            sumitter.sendMessage("§aYou have set your size to "+scale);
        }
    }
    public class EntryCommand extends Command {
        public EntryCommand(String name, String description, String usageMessage, String[] aliases) {
            super(name, description, usageMessage, aliases);
            this.setPermission("PlayerScale.cmd");
        }
        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (!sender.isPlayer()) {
                sender.sendMessage("§cYou must be in-game to run this command");
                return true;
            }
            if(!sender.hasPermission("PlayerScale.cmd")){
                sender.sendMessage("§cYou no perm for Command!");
                return true;
            }
            if(args.length < 1){
                sender.sendMessage("§l§cUsage: §r§a/size <set/ui>");
                return true;
            }
            Player player = (Player) sender;
            switch(args[0]){
                case "set":
                    if(args.length < 2){
                        sender.sendMessage("§l§cUsage: §r§a/size set <size> <player>");
                        return true;
                    }
                    if(args.length == 3 && getServer().getPlayer(args[2]) == null){
                        sender.sendMessage("§l§cERROR: §r§aYou have entered an invalid Player Username.");
                        return true;
                    }
                    float size;
                    try{
                        size = Float.parseFloat(args[1]);
                        if(size < 0.5f || size > 5.0f){
                            sender.sendMessage("§l§cERROR: §r§aYou have entered an invalid Size. Chose a size between 0.5-5.");
                            return true;
                        }
                    }catch (NumberFormatException e){
                        sender.sendMessage("§l§cERROR: §r§aYou have entered an invalid Size. Chose a size between 0.5-5.");
                        return true;
                    }
                    if(args.length == 3){
                        player = getServer().getPlayer(args[2]);
                    }
                    player.setScale(size);
                    if(player.getName() != sender.getName()){
                        sender.sendMessage("§aYou have set "+player.getName()+"'s player size to "+args[1]);
                        player.sendMessage("§aYour player size has been set to "+args[1]);
                    }else{
                        sender.sendMessage("§aYou have set your size to "+args[1]);
                    }
                    break;
                case "ui":
                    player.showFormWindow(scaleForm());
                    break;
                default:
                    sender.sendMessage("§l§cUsage: §r§a/size <set/ui>");
            }
            return true;
        }
    }
}