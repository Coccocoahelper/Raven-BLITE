package keystrokesmod.module.modules.combat;

import keystrokesmod.module.*;
import keystrokesmod.utils.Utils;
import keystrokesmod.module.modules.world.AntiBot;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.concurrent.ThreadLocalRandom;

public class BlockHit extends Module {
    public static ModuleSettingSlider range, eventType;
    public static ModuleDesc eventTypeDesc;
    public static ModuleSettingTick onlyPlayers, onRightMBHold;
    public static ModuleSettingDoubleSlider waitMs, hitPer;
    public static double comboLasts;
    public static boolean comboing, hitCoolDown, alreadyHit, safeGuard;
    public static int hitTimeout, hitsWaited;

    public BlockHit() {
        super("BlockHit", category.combat, 0);
        this.registerSetting(onlyPlayers = new ModuleSettingTick("Only combo players", true));
        this.registerSetting(onRightMBHold = new ModuleSettingTick("When holding down rmb", true));
        this.registerSetting(waitMs = new ModuleSettingDoubleSlider("Action Time (MS)", 110, 150, 1, 500, 1));
        this.registerSetting(hitPer = new ModuleSettingDoubleSlider("Once every ... hits", 1, 1, 1, 10, 1));
        this.registerSetting(range = new ModuleSettingSlider("Range: ", 3, 1, 6, 0.05));
        this.registerSetting(eventType = new ModuleSettingSlider("Value: ", 2, 1, 2, 1));
        this.registerSetting(eventTypeDesc = new ModuleDesc("Mode: POST"));
    }

    public void guiUpdate() {
        eventTypeDesc.setDesc(Utils.md + Utils.Modes.SprintResetTimings.values()[(int) eventType.getInput() - 1]);
    }


    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent e) {
        if(!Utils.Player.isPlayerInGame())
            return;

        if(onRightMBHold.isToggled() && !Utils.Player.tryingToCombo()){
            if(!safeGuard || Utils.Player.isPlayerHoldingWeapon() && Mouse.isButtonDown(0)) {
                safeGuard = true;
                finishCombo();
            }
            return;
        }

        if(comboing) {
            if(System.currentTimeMillis() >= comboLasts){
                comboing = false;
                finishCombo();
                return;
            }else {
                return;
            }
        }

        if(onRightMBHold.isToggled() && Utils.Player.tryingToCombo()) {
            if(mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null) {
                if(!safeGuard  || Utils.Player.isPlayerHoldingWeapon() && Mouse.isButtonDown(0)) {
                    safeGuard = true;
                    finishCombo();
                }
                return;
            } else {
                Entity target = mc.objectMouseOver.entityHit;
                //////////System.out.println(target.hurtResistantTime);
                if(target.isDead) {
                    if(!safeGuard  || Utils.Player.isPlayerHoldingWeapon() && Mouse.isButtonDown(0)) {
                        safeGuard = true;
                        finishCombo();
                    }
                    return;
                }
            }
        }

        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof Entity && Mouse.isButtonDown(0)) {
            Entity target = mc.objectMouseOver.entityHit;
            //////////System.out.println(target.hurtResistantTime);
            if(target.isDead) {
                if(onRightMBHold.isToggled() && Mouse.isButtonDown(1) && Mouse.isButtonDown(0)) {
                    if(!safeGuard  || Utils.Player.isPlayerHoldingWeapon() && Mouse.isButtonDown(0)) {
                        safeGuard = true;
                        finishCombo();
                    }
                }
                return;
            }

            if (mc.thePlayer.getDistanceToEntity(target) <= range.getInput()) {
                if ((target.hurtResistantTime >= 10 && Utils.Modes.SprintResetTimings.values()[(int) eventType.getInput() - 1] == Utils.Modes.SprintResetTimings.POST) || (target.hurtResistantTime <= 10 && Utils.Modes.SprintResetTimings.values()[(int) eventType.getInput() - 1] == Utils.Modes.SprintResetTimings.PRE)) {

                    if (onlyPlayers.isToggled()){
                        if (!(target instanceof EntityPlayer)){
                            return;
                        }
                    }

                    if(AntiBot.bot(target)){
                        return;
                    }


                    if (hitCoolDown && !alreadyHit) {
                        //////////System.out.println("coolDownCheck");
                        hitsWaited++;
                        if(hitsWaited >= hitTimeout){
                            //////////System.out.println("hiit cool down reached");
                            hitCoolDown = false;
                            hitsWaited = 0;
                        } else {
                            //////////System.out.println("still waiting for cooldown");
                            alreadyHit = true;
                            return;
                        }
                    }

                    //////////System.out.println("Continued");

                    if(!alreadyHit){
                        //////////System.out.println("Startring combo code");
                        guiUpdate();
                        if(hitPer.getInputMin() == hitPer.getInputMax()) {
                            hitTimeout =  (int) hitPer.getInputMin();
                        } else {

                            hitTimeout = ThreadLocalRandom.current().nextInt((int) hitPer.getInputMin(), (int) hitPer.getInputMax());
                        }
                        hitCoolDown = true;
                        hitsWaited = 0;

                        comboLasts = ThreadLocalRandom.current().nextDouble(waitMs.getInputMin(),  waitMs.getInputMax()+0.01) + System.currentTimeMillis();
                        comboing = true;
                        startCombo();
                        //////////System.out.println("Combo started");
                        alreadyHit = true;
                        if(safeGuard) safeGuard = false;
                    }
                } else {
                    if(alreadyHit){
                        //////////System.out.println("UnHit");
                    }
                    alreadyHit = false;
                    //////////System.out.println("REEEEEEE");
                    if(safeGuard) safeGuard = false;
                }
            }
        }
    }

    private static void finishCombo() {
        int key = mc.gameSettings.keyBindUseItem.getKeyCode();
        KeyBinding.setKeyBindState(key, false);
        Utils.Client.setMouseButtonState(1, false);
    }

    private static void startCombo() {
        if(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            int key = mc.gameSettings.keyBindUseItem.getKeyCode();
            KeyBinding.setKeyBindState(key, true);
            KeyBinding.onTick(key);
            Utils.Client.setMouseButtonState(1, true);
        }
    }
}
