function load(){
    manager.createCommand("size", "Change your player size", onCommand, "/size", ["scale"], "PlayerScale.cmd");
}
function onCommand(sender, args, label, manageCMD){
    if(!sender.isPlayer()){
        sender.sendMessage("§cYou must be in-game to run this command");
        return;
    }
    if(!sender.hasPermission("PlayerScale.cmd")){
        sender.sendMessage("§cYou no perm for Command!");
        return;
    }
    if(args.length < 1){
        sender.sendMessage("§l§cUsage: §r§a/size <set/ui>");
        return;
    }
    let player;
    switch(args[0]){
        case "ui":
            sender.showFormWindow(scaleForm());
        break;
        case "set":
            if(args.length < 2){
                sender.sendMessage("§l§cUsage: §r§a/size set <size> <player>");
                return;
            }
            if(args.length == 3 && server.getPlayer(args[2]) == null){
                sender.sendMessage("§l§cERROR: §r§aYou have entered an invalid Player Username.");
                return;
            }
            if(isNaN(parseFloat(args[1])) || parseFloat(args[1]) < 1 || parseFloat(args[1]) > 5){
                sender.sendMessage("§l§cERROR: §r§aYou have entered an invalid Size. Chose a size between 1-5.");
                return;
            }
            player = sender;
            if(args.length == 3){
                player = server.getPlayer(args[2]);
            }
            player.setScale(parseFloat(args[1]));
            if(player.getName() != sender.getName()){
                sender.sendMessage("§aYou have set "+player.getName()+"'s player size to "+args[1]);
                sender.sendMessage("§aYour player size has been set to "+args[1]);
            }else{
                sender.sendMessage("§aYou have set your size to "+args[1]);
            }
        break;
    }
}
function scaleForm(){
    let FormWindowCustom = Java.type("cn.nukkit.form.window.FormWindowCustom");
    let ElementDropdown = Java.type("cn.nukkit.form.element.ElementDropdown");
    let ElementSlider = Java.type("cn.nukkit.form.element.ElementSlider");
    let list = [];
    for each(let player in players){
        list[list.length] = player.getName();
    }
    return new FormWindowCustom("§lScale a Player", [
        new ElementDropdown("Select a Player", list),
        new ElementSlider("Select a Size", 1.0, 5.0, 0.5, 1.0)
    ]);
}
script.addEventListener("PlayerFormRespondedEvent", function(event){
    let submitter = event.getPlayer();
    let win = event.getWindow();
    let FormWindowCustom = Java.type("cn.nukkit.form.window.FormWindowCustom");
    if(win instanceof FormWindowCustom){
        if(event.getResponse() == null) return;
        if(event.wasClosed()) return;
        if(win.getTitle() != "§lScale a Player") return;
        let player = event.getResponse().getDropdownResponse(0).getElementContent();
        let scale = event.getResponse().getSliderResponse(1);
        if(server.getPlayerExact(player) == null){
            submitter.sendMessage("§l§cERROR: §r§aYou have selected an invalid Player.");
            return;
        }
        player = server.getPlayerExact(player);
        player.setScale(scale);
        if(player.getName() != submitter.getName()){
            submitter.sendMessage("§aYou have set "+player.getName()+"'s player size to "+scale);
            submitter.sendMessage("§aYour player size has been set to "+scale);
        }else{
            submitter.sendMessage("§aYou have set your size to "+scale);
        }
    }
});
module.exports = {
    onLoad: load
};
