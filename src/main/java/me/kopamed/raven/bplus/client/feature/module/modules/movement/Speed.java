//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.8.9"!

package me.kopamed.raven.bplus.client.feature.module.modules.movement;

import me.kopamed.raven.bplus.client.feature.module.Module;
import me.kopamed.raven.bplus.client.feature.module.ModuleCategory;
import me.kopamed.raven.bplus.client.feature.setting.settings.Description;
import me.kopamed.raven.bplus.client.feature.setting.settings.Tick;
import me.kopamed.raven.bplus.client.feature.setting.settings.Slider;
import me.kopamed.raven.bplus.helper.utils.Utils;
import org.lwjgl.input.Keyboard;

public class Speed extends Module {
   public static Description dc;
   public static Slider a;
   public static Tick b;

   public Speed() {
      super("Speed", ModuleCategory.Movement, 0);
      this.registerSetting(dc = new Description("Hypixel max: 1.13"));
      this.registerSetting(a = new Slider("Speed", 1.2D, 1.0D, 1.5D, 0.01D));
      this.registerSetting(b = new Tick("Strafe only", false));
   }

   public void update() {
      double csp = Utils.Player.pythagorasMovement();
      if (csp != 0.0D) {
         if (mc.thePlayer.onGround && !mc.thePlayer.capabilities.isFlying) {
            if (!b.isToggled() || mc.thePlayer.moveStrafing != 0.0F) {
               if (mc.thePlayer.hurtTime != mc.thePlayer.maxHurtTime || mc.thePlayer.maxHurtTime <= 0) {
                  if (!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                     double val = a.getInput() - (a.getInput() - 1.0D) * 0.5D;
                     Utils.Player.fixMovementSpeed(csp * val, true);
                  }
               }
            }
         }
      }
   }
}