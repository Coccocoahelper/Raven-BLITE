package me.kopamed.raven.bplus.client.feature.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kopamed.raven.bplus.client.visual.clickgui.plus.component.Component;
import me.kopamed.raven.bplus.client.visual.clickgui.plus.component.components.ModuleComponent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class Setting {
   private boolean visible = true;
   private final SettingType settingType;
   private final String settingName;
   private final String description;
   private final ArrayList<SelectorRunnable> selectors = new ArrayList<>();

   public Setting(String name, SettingType settingType) {
      this(name, "", settingType);
   }

   public Setting(String name, String description, SettingType settingType) {
      this.settingName = name;
      this.description = description;
      this.settingType = settingType;
   }

   public String getName() {
      return settingName;
   }

   public boolean canShow(){
      if(selectors.isEmpty())
         return true;

      for(SelectorRunnable selectorRunnable : selectors){
         if(!selectorRunnable.showOnlyIf())
            return false;
      }
      return true;
   }

   public void addSelector(SelectorRunnable selectorRunnable){
      this.selectors.add(selectorRunnable);
   }

   public JsonObject getSettingsAsJson(){
      //todo
      return new JsonObject();
   }

    public Component createComponent(ModuleComponent moduleComponent) {
      return new Component() {
      };
    }

   public JsonObject getConfigAsJson() {
      return null;
   }

   public SettingType getSettingType() {
      return settingType;
   }

   public boolean isVisible() {
      return visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public String getDescription() {
      return description;
   }
}