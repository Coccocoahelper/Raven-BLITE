package keystrokesmod.client.module;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;

import keystrokesmod.client.clickgui.raven.Component;
import keystrokesmod.client.clickgui.raven.components.ModuleComponent;
import keystrokesmod.client.module.setting.Setting;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class Module {
   protected ArrayList<Setting> settings;
   private final String moduleName;
   private final ModuleCategory moduleCategory;
   private boolean hasBind = true;
   private boolean clientConfig = false;
   protected boolean enabled = false;
   protected boolean defaultEnabled = enabled;
   protected int keycode = 0;
   protected int defualtKeyCode = keycode;
   
   
   protected ModuleComponent component;
   
   protected static Minecraft mc;
   private boolean isToggled = false;

   private String description = "";



   public Module(String name, ModuleCategory moduleCategory) {
      this.moduleName = name;
      this.moduleCategory = moduleCategory;
      this.settings = new ArrayList<>();
      mc = Minecraft.getMinecraft();
   }

   protected <E extends Module> E withKeycode(int i){
      this.keycode = i;
      this.defualtKeyCode = i;
      return (E) this;
   }

   protected  <E extends Module> E withEnabled(boolean i){
      this.enabled = i;
      this.defaultEnabled = i;
      try{
         setToggled(i);
      } catch (Exception e){}
      return (E) this;
   }

   public <E extends Module> E withDescription(String i){
      this.description = i;
      return (E) this;
   }
   

   public JsonObject getConfigAsJson(){
      JsonObject settings = new JsonObject();

      for(Setting setting : this.settings){
    	  if(setting != null) {
    		  JsonObject settingData = setting.getConfigAsJson();
              settings.add(setting.settingName, settingData);
    	  }
      }

      JsonObject data = new JsonObject();
      data.addProperty("enabled", enabled);
      data.addProperty("keycode", keycode);
      data.add("settings", settings);

      return data;
   }

   public void applyConfigFromJson(JsonObject data){
      try {
         this.keycode = data.get("keycode").getAsInt();
         setToggled(data.get("enabled").getAsBoolean());
         JsonObject settingsData = data.get("settings").getAsJsonObject();
         for (Setting setting : getSettings()) {
            if (settingsData.has(setting.getName())) {
               setting.applyConfigFromJson(
                       settingsData.get(setting.getName()).getAsJsonObject()
               );
            }
         }
      } catch (NullPointerException ignored){

      }
   }


   public void keybind() {
      if (this.keycode != 0 && this.canBeEnabled()) {
         if (!this.isToggled && Keyboard.isKeyDown(this.keycode)) {
            this.toggle();
            this.isToggled = true;
         } else if (!Keyboard.isKeyDown(this.keycode)) {
            this.isToggled = false;
         }
      }
   }

   
   public boolean canBeEnabled() {
      return true;
   }

   public void enable() {
      this.enabled = true;
      this.onEnable();
      MinecraftForge.EVENT_BUS.register(this);
   }

   public void disable() {
      this.enabled = false;
      this.onDisable();
      MinecraftForge.EVENT_BUS.unregister(this);
   }

   public void setToggled(boolean enabled) {
      if(enabled){
         enable();
      } else{
         disable();
      }
   }

   public String getName() {
      return this.moduleName;
   }

   public ArrayList<Setting> getSettings() {
      return this.settings;
   }

   public Setting getSettingByName(String name) {
      for (Setting setting : this.settings) {
         if (setting.getName().equalsIgnoreCase(name))
            return setting;
      }
      return null;
   }

   public void registerSetting(Setting Setting) {
      this.settings.add(Setting);
   }
   

   public ModuleCategory moduleCategory() {
      return this.moduleCategory;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void toggle() {
      if (this.enabled) {
         this.disable();
      } else {
         this.enable();
      }
   }

   public void update() {
   }

   public void guiUpdate() {
   }

   public void guiButtonToggled(TickSetting b) {
   }
   
   public void guiButtonToggled(ComboSetting b) {
   }
   
   public void guiButtonToggled(TickSetting b, Component c) {
   }
   
   public void preClickGuiLoad() {
   }

   public int getKeycode() {
      return this.keycode;
   }
 

   public void setbind(int keybind) {
      this.keycode = keybind;
   }

   public void resetToDefaults() {
      this.keycode = defualtKeyCode;
      this.setToggled(defaultEnabled);

      for(Setting setting : this.settings){
         setting.resetToDefaults();
      }
   }
   
   public void setModuleComponent(ModuleComponent component) {
	   this.component = component;
   }

   public void onGuiClose() {

   }

   public String getBindAsString(){
      return keycode == 0 ? "None" : Keyboard.getKeyName(keycode);
   }

   public void clearBinds() {
      this.keycode = 0;
   }

   public enum ModuleCategory {
      combat,
      movement,
      player,
      world,
      render,
      minigames,
      other,
      client,
      hotkey
   }
}
