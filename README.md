#BCM (BetterCommandManager)
I created this command manager one night after I needed a Better Command Manager to replace my old annotation based command manager.
This was created as a private project, but after I seen that some people liked my command manager I released it as a Open Source Project.

**Features**
- Is universal. That means that you can use it in whatever plataform you use.
- Is easy to use.
- Two bolts doesn't look good
- Yeah, four bolts look better
- But five bolts, it's even better!
- It's annotation based :0

**Usage**
Here a example of usage in bukkit
```
    @Command(names = "fly")
    public boolean flyCommand(CommandSender sender, @Optional("self") String targetName) {
        if (player.equalsIgnoreCase("self") && !(sender instanceof Player)) {
            return false;
        }
        
        Player target;
    
        if(targetName.equals("self")) {
            target = (Player) sender;             
        } else {
            target = Bukkit.getPlayer(targetName);
            
            if(target == null) {
                sender.sendMessage(ChatColor.RED + "The specified target can't be found");
                return true;        
            }            
        }
        
        target.setAllowFlight(!target.getAllowFlight());
        sender.sendMessage(ChatColor.GREEN + "Fly mode of " + target.getName() + ": " + target.getAllowFlight());
        
        return true;    
    }
```