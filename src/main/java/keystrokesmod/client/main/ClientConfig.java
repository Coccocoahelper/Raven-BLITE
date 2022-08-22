package keystrokesmod.client.main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import keystrokesmod.client.clickgui.raven.components.CategoryComponent;
import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.HUD;
import keystrokesmod.client.utils.Utils;
import keystrokesmod.keystroke.KeyStroke;
import net.minecraft.client.Minecraft;

public class ClientConfig {
   private static final Minecraft mc = Minecraft.getMinecraft();
   private final File cfgDir = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "keystrokes");
   private final File cfgFile;
   private final String fileName = "clientconfig.kv";
   private JsonObject config;
   private boolean applying;
   //when you are coding the config manager and life be like
   //public static String ip_token_discord_webhook_logger_spyware_malware_minecraft_block_hacker_sigma_miner_100_percent_haram_no_cap_m8_Kopamed_is_sexy = "https://imgur.com/a/hYd1023";
   //dude wtf bro i was not expecting that i opened that up on my school bus
   
   public ClientConfig(){
      if(!cfgDir.exists()){
    	  cfgDir.mkdir();
      }
      cfgFile = new File(cfgDir, fileName);
      if(!cfgFile.exists()){
         try {
        	 cfgFile.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      
      JsonParser jsonParser = new JsonParser();
      try (FileReader reader = new FileReader(cfgFile))
      {
          Object obj = jsonParser.parse(reader);
          config = (JsonObject) obj;
      } catch (JsonSyntaxException | ClassCastException | IOException e) {
          e.printStackTrace();
      }
   }
   
   private JsonObject getKeystrokeAsJson() {
	   JsonObject data = new JsonObject();
	   data.addProperty("x", KeyStroke.x);
	   data.addProperty("y", KeyStroke.y);
	   data.addProperty("enabled",  KeyStroke.enabled);
	   data.addProperty("mbEnabled", KeyStroke.showMouseButtons);
	   data.addProperty("color",  KeyStroke.currentColorNumber);
	   data.addProperty("outline", KeyStroke.outline);
	   return data;
   }
   
   private JsonObject getHudAsJson() {
	   JsonObject data = new JsonObject();
	   data.addProperty("hudX", HUD.getHudX());
	   data.addProperty("hudY", HUD.getHudY());
	   return data;
   }
   
   public JsonObject getClickGuiPosAsJson() {
	  JsonObject data = new JsonObject();
      for (CategoryComponent cat : Raven.clickGui.getCategoryList()) {
    	 JsonObject catData = new JsonObject();
    	 catData.addProperty("X", cat.getX());
    	 catData.addProperty("Y", cat.getY());
         catData.addProperty("opened", cat.isOpened());
         data.add(cat.categoryName.name(), catData);
      }
      return data;
   }
   
   private JsonObject getClickGuiAsJson() {
	   JsonObject data = new JsonObject();
	   data.add("catPos", getClickGuiPosAsJson());
	   data.addProperty("terminalX", Raven.clickGui.terminal.getX());
	   data.addProperty("terminalY", Raven.clickGui.terminal.getY());
	   data.addProperty("width", Raven.clickGui.terminal.getWidth());
	   data.addProperty("height", Raven.clickGui.terminal.getHeight());
	   data.addProperty("hidden", Raven.clickGui.terminal.hidden); //lmao what cant u just check if the module is tuned on
	   data.addProperty("opened", Raven.clickGui.terminal.opened);
	   return data;
   }
   
   private JsonObject getModulesAsJson() {
	   JsonObject data = new JsonObject();
	   for(Module m : Raven.moduleManager.getClientConfigModules()) {
		   data.add(m.getName(), m.getConfigAsJson());
	   }
	   return data;
   }
   
   public JsonObject getConfigAsJson() {
	   JsonObject data = new JsonObject();
	  
	   data.addProperty("apikey", Utils.URLS.hypixelApiKey);
	   data.addProperty("pastekey", Utils.URLS.pasteApiKey);
	   data.addProperty("currentconfig", Raven.configManager.getConfig().getName());
       data.add("keystrokes", getKeystrokeAsJson());
       data.add("hud", getHudAsJson());
       data.add("clickgui", getClickGuiAsJson());
       data.add("modules", getModulesAsJson());

       return data;
   }
   
   public void saveConfig() {
	   if(applying) return;
	   this.config = getConfigAsJson();

	   try (PrintWriter out = new PrintWriter(new FileWriter(cfgFile))) {
           out.write(config.toString());
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   
   public void updateKeyStrokesSettings() {
	   config.add("keystrokes", getKeystrokeAsJson());
	   saveConfig();
   }

   public void applyKeyStrokeSettingsFromConfigFile() {
	   try {
		   JsonObject data = config.get("keystrokes").getAsJsonObject();
		   KeyStroke.x = data.get("x").getAsInt();
		   KeyStroke.y = data.get("y").getAsInt();
		   KeyStroke.enabled = data.get("enabled").getAsBoolean();
		   KeyStroke.showMouseButtons = data.get("mbEnabled").getAsBoolean();
		   KeyStroke.currentColorNumber = data.get("color").getAsInt();
		   KeyStroke.outline = data.get("outline").getAsBoolean();
	   } catch (Throwable var4) {
		   var4.printStackTrace();
	   }
   } 

   public void applyConfig(){
	   applying = true;
	   try {
		   Utils.URLS.hypixelApiKey = config.get("apikey").getAsString();
		   Utils.URLS.pasteApiKey = config.get("pastekey").getAsString();
		   loadClickGuiCoords(config.get("clickgui").getAsJsonObject().get("catPos").getAsJsonObject());
		   Raven.configManager.loadConfigByName(config.get("currentconfig").getAsString());
		   loadHudCoords(config.get("hud").getAsJsonObject());
		   loadTerminalCoords(config.get("clickgui").getAsJsonObject());
		   loadModules(config.get("modules").getAsJsonObject());
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   applying = false;
   }


   private void loadHudCoords(JsonObject data) {
	   HUD.setHudX(data.get("hudX").getAsInt());
	   HUD.setHudY(data.get("hudY").getAsInt());
   }

   private void loadClickGuiCoords(JsonObject data) {
	   for (CategoryComponent cat : Raven.clickGui.getCategoryList()) {
		   JsonObject catData = data.get(cat.categoryName.name()).getAsJsonObject();
		   cat.setX(catData.get("X").getAsInt());
		   cat.setY(catData.get("Y").getAsInt());
		   cat.setOpened(catData.get("opened").getAsBoolean());
	   }
   } 

   private void loadTerminalCoords(JsonObject data) {
	   Raven.clickGui.terminal.setLocation(data.get("terminalX").getAsInt(), data.get("terminalY").getAsInt());
	   Raven.clickGui.terminal.setSize(data.get("width").getAsInt(), data.get("height").getAsInt());
	   Raven.clickGui.terminal.opened = data.get("opened").getAsBoolean();
	   Raven.clickGui.terminal.hidden = data.get("hidden").getAsBoolean();
   }
   
   private void loadModules(JsonObject data) {
	   List<Module> knownModules = new ArrayList<>(Raven.moduleManager.getClientConfigModules());
	   for(Module module : knownModules){
		   if(data.has(module.getName())){
			   module.applyConfigFromJson(data.get(module.getName()).getAsJsonObject());
			   System.out.println(module.getName());
		   } else {
			   module.resetToDefaults();
		   }
	   }
   }
}
