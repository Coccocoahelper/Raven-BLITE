//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.8.9"!

package keystrokesmod.tweaker;

import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.modules.combat.AutoClicker;
import keystrokesmod.module.modules.combat.Reach;
import keystrokesmod.module.modules.movement.KeepSprint;
import keystrokesmod.module.modules.movement.NoSlow;
import keystrokesmod.module.modules.other.NameHider;
import keystrokesmod.module.modules.other.StringEncrypt;
import keystrokesmod.module.modules.player.SafeWalk;
import keystrokesmod.module.modules.render.AntiShuffle;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

public class ASMEventHandler {
   private static final Minecraft mc = Minecraft.getMinecraft();

   /**
    * called when Minecraft format text
    */
   public static String getUnformattedTextForChat(String s) {
      if (ModuleManager.initialized) {
         if (ModuleManager.nameHider.isEnabled()) {
            s = NameHider.getUnformattedTextForChat(s);
         }

         if (ModuleManager.antiShuffle.isEnabled()) {
            s = AntiShuffle.getUnformattedTextForChat(s);
         }

         if (ModuleManager.stringEncrypt.isEnabled()) {
            s = StringEncrypt.getUnformattedTextForChat(s);
         }
      }

      return s;
   }


   /**
    * called when an entity moves
    */
   public static boolean onEntityMove(Entity entity) {
      if (entity == mc.thePlayer && mc.thePlayer.onGround) {
         if (ModuleManager.safeWalk.isEnabled() && !SafeWalk.doShift.isToggled()) {
            if (SafeWalk.blocksOnly.isToggled()) {
               ItemStack i = mc.thePlayer.getHeldItem();
               if (i == null || !(i.getItem() instanceof ItemBlock)) {
                  return mc.thePlayer.isSneaking();
               }
            }

            return true;
         } else {
            return mc.thePlayer.isSneaking();
         }
      } else {
         return false;
      }
   }

   public String getModName()
   {
      return "lunarclient:db2533c";
   }

   /**
    * called when a player is using an item (aka right-click)
    */
   public static void onLivingUpdate() {
      if (ModuleManager.noSlow.isEnabled()) {
         NoSlow.sl();
      } else {
         mc.thePlayer.movementInput.moveStrafe *= 0.2F;
         mc.thePlayer.movementInput.moveForward *= 0.2F;
      }
   }

   /**
    * called when a player is moving and hits another one
    */
   public static void onAttackTargetEntityWithCurrentItem(Entity en) {
      if (ModuleManager.keepSprint.isEnabled()) {
         KeepSprint.sl(en);
      } else {
         mc.thePlayer.motionX *= 0.6D;
         mc.thePlayer.motionZ *= 0.6D;
      }
   }

   /**
    * called every ticks
    */
   public static void onTick() {
      if (!ModuleManager.autoClicker.isEnabled() || !AutoClicker.leftClick.isToggled() || !Mouse.isButtonDown(0) || !Reach.call()) {
         mc.entityRenderer.getMouseOver(1.0F);
      }
   }
}
