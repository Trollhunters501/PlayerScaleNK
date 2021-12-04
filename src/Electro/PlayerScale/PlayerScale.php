<?php

namespace Electro\PlayerScale;

use dktapps\pmforms\CustomForm;
use dktapps\pmforms\element\Dropdown;
use dktapps\pmforms\element\Slider;
use dktapps\pmforms\CustomFormResponse;
use pocketmine\plugin\PluginBase;
use pocketmine\player\Player;
use pocketmine\event\Listener;
use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\utils\Config;

class PlayerScale extends PluginBase implements Listener{

    public function onCommand(CommandSender $sender, Command $cmd, string $label, array $args): bool {
        if (!$sender instanceof Player)
        {
            $sender->sendMessage("§cYou must be in-game to run this command");
            return true;
        }
        switch($cmd->getName()) {
            case "size":
                if(!$sender instanceof Player) {
                    $sender->sendMessage("§l§cERROR: §r§aYou must be in-game to execute this command");
                    return true;
                }
                if (!isset($args[0])) {
                    $sender->sendMessage("§l§cUsage: §r§a/size <set/ui>");
                    return true;
                }

                switch ($args[0])
                {
                    case "ui":
                        $sender->sendForm($this->scaleForm());
                        break;
                    case "set":
                        if (!isset($args[1]))
                        {
                            $sender->sendMessage("§l§cUsage: §r§a/size set <size> <player>");
                            return true;
                        }
                        if (isset($args[2]) && !$this->getServer()->getPlayerByPrefix($args[2]) instanceof Player) {
                            $sender->sendMessage("§l§cERROR: §r§aYou have entered an invalid Player Username.");
                            return true;
                        }
                        if (!is_numeric($args[1]) || $args[1] < 1 || $args[1] > 5)
                        {
                            $sender->sendMessage("§l§cERROR: §r§aYou have entered an invalid Size. Chose a size between 1-5.");
                            return true;
                        }
                        $player = $sender;
                        if (isset($args[2]))
                        {
                            $player = $this->getServer()->getPlayerByPrefix($args[2]);
                        }
                        $player->setScale($args[1]);
                        if (isset($args[2]) && $sender->getName() !== $player->getName())
                        {
                            $sender->sendMessage("§aYou have set " . $player->getName() . "'s player size to " . $args[1]);
                            $player->sendMessage("§aYour player size has been set to " . $args[1]);
                        }
                        else
                        {
                            $player->sendMessage("§aYou have set your size to " . $args[1]);
                        }
                        break;
                }
                break;
        }
        return true;
    }

    private function scaleForm() : CustomForm{
        $list = [];
        foreach ($this->getServer()->getOnlinePlayers() as $player)
        {
            $list[] = $player->getName();
        }
        return new CustomForm(
            "§lScale a Player",
            [
                new Dropdown("player", "Select a Player", $list),
                new Slider("scale", "Select a Size", 1.0, 5.0, 0.1, 1.0),
            ],
            function(Player $submitter, CustomFormResponse $response) use ($list) : void{
                $player = $response->getInt("player");
                $scale = $response->getFloat("scale");

                if (!is_numeric($player)){
                    $submitter->sendMessage("§l§cERROR: §r§aYou selected an invalid Player");
                    return;
                }
                $playerName = $list[$response->getInt("player")];

                if (!$this->getServer()->getPlayerExact($playerName) instanceof Player) {
                    $submitter->sendMessage("§l§cERROR: §r§aYou have selected an invalid Player.");
                    return;
                }
                $player = $this->getServer()->getPlayerByPrefix($playerName);
                $player->setScale($scale);
                if ($player->getName() !== $submitter->getName())
                {
                    $submitter->sendMessage("§aYou have set " . $player->getName() . "'s player size to " . $scale);
                    $player->sendMessage("§aYour player size has been set to " . $scale);
                }
                else
                {
                    $player->sendMessage("§aYou have set your size to " . $scale);
                }
            },
        );
    }
}
