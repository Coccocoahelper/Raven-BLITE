//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.8.9"!

package keystrokesmod.module.modules.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleSettingTick;

public class StopMotion extends Module {
   public static ModuleSettingTick a;
   public static ModuleSettingTick b;
   public static ModuleSettingTick c;

   public StopMotion() {
      super("Stop Motion", Module.category.movement, 0);
      this.registerSetting(a = new ModuleSettingTick("Stop X", true));
      this.registerSetting(b = new ModuleSettingTick("Stop Y", true));
      this.registerSetting(c = new ModuleSettingTick("Stop Z", true));
   }

   public void onEnable() {
       //System.out.println("No fuck you");

      this.disable();
   }
}
