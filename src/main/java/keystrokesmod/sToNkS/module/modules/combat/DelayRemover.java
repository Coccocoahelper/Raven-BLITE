package keystrokesmod.sToNkS.module.modules.combat;

import keystrokesmod.sToNkS.module.Module;
import keystrokesmod.sToNkS.module.ModuleDesc;
import keystrokesmod.sToNkS.utils.Utils;
import net.minecraft.client.Minecraft;
import keystrokesmod.sToNkS.lib.fr.jmraich.rax.event.FMLEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class DelayRemover extends Module {
   public static ModuleDesc desc;

   private final Field leftClickCounterField;

   public DelayRemover() {
      super("Delay Remover", Module.category.combat, 0);
      this.registerSetting(desc = new ModuleDesc("Gives you 1.7 hitreg."));

      this.leftClickCounterField = ReflectionHelper.findField(Minecraft.class, "field_71429_W", "leftClickCounter");
      if (this.leftClickCounterField != null) this.leftClickCounterField.setAccessible(true);
   }

   @Override
   public boolean canBeEnabled() {
      return this.leftClickCounterField != null;
   }

   @FMLEvent
   public void playerTickEvent(PlayerTickEvent event) {
      if (Utils.Player.isPlayerInGame() && this.leftClickCounterField != null) {
         if (!mc.inGameHasFocus || mc.thePlayer.capabilities.isCreativeMode) {
            return;
         }

         try {
            this.leftClickCounterField.set(mc, 0);
         } catch (IllegalAccessException | IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            this.disable();
         }
      }
   }
}
