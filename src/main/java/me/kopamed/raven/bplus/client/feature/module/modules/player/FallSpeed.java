//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.8.9"!

package me.kopamed.raven.bplus.client.feature.module.modules.player;

import me.kopamed.raven.bplus.client.feature.module.Module;
import me.kopamed.raven.bplus.client.feature.module.ModuleCategory;
import me.kopamed.raven.bplus.helper.manager.ModuleManager;
import me.kopamed.raven.bplus.client.feature.setting.settings.Description;
import me.kopamed.raven.bplus.client.feature.setting.settings.Slider;
import me.kopamed.raven.bplus.client.feature.setting.settings.Tick;

public class FallSpeed extends Module {
   public static Description dc;
   public static Slider a;
   public static Tick b;

   public FallSpeed() {
      super("FallSpeed", ModuleCategory.Player, 0);
      this.registerSetting(dc = new Description("Vanilla max: 3.92"));
      this.registerSetting(a = new Slider("Motion", 5.0D, 0.0D, 8.0D, 0.1D));
      this.registerSetting(b = new Tick("Disable XZ motion", true));
   }

   public void update() {
      if ((double)mc.thePlayer.fallDistance >= 2.5D) {
         if (ModuleManager.fly.isEnabled() || ModuleManager.noFall.isEnabled()) {
            return;
         }

         if (mc.thePlayer.capabilities.isCreativeMode || mc.thePlayer.capabilities.isFlying) {
            return;
         }

         if (mc.thePlayer.isOnLadder() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) {
            return;
         }

         mc.thePlayer.motionY = -a.getInput();
         if (b.isToggled()) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0D;
         }
      }

   }
}