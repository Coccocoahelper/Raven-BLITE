package keystrokesmod.client.module.modules.combat;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.modules.combat.WTap.EventType;
import keystrokesmod.client.module.modules.combat.WTap.WtapState;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.CoolDown;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class STap extends Module {
	public ComboSetting eventType;
    public SliderSetting range, chance, tapMultiplier;
    public DescriptionSetting eventTypeDesc;
    public TickSetting onlyPlayers, onlySword, autoCfg, dynamic;
    public DoubleSliderSetting waitMs,actionMs, hitPer, postDelay;
    public int hits, rhit;
    public boolean call, p;
    public long s;
    private StapState state = StapState.NONE;
    private CoolDown timer = new CoolDown(0);
    private Entity target;
   
    public Random r = new Random();

    public STap(){
        super("STap", ModuleCategory.combat);
        
        this.registerSetting(eventType = new ComboSetting("Event:", WTap.EventType.Attack));
        this.registerSetting(onlyPlayers = new TickSetting("Only combo players", true));
        this.registerSetting(onlySword = new TickSetting("Only sword", false));
        this.registerSetting(waitMs = new DoubleSliderSetting("Press s for ... ms", 30, 40, 1, 300, 1));
        this.registerSetting(actionMs = new DoubleSliderSetting("STap after ... ms", 20, 30, 1, 300, 1));
        this.registerSetting(chance =  new SliderSetting("Chance %", 100, 0, 100, 1));
        this.registerSetting(hitPer = new DoubleSliderSetting("Once every ... hits", 1, 1, 1, 10, 1)); 
        this.registerSetting(range = new SliderSetting("Range: ", 3, 1, 6, 0.05));
        this.registerSetting(dynamic = new TickSetting("Dynamic tap time", false));
        this.registerSetting(tapMultiplier = new SliderSetting("wait time sensitivity", 1F, 0F, 5F, 0.1F));
    }

    @SubscribeEvent
    public void wTapUpdate(TickEvent.RenderTickEvent e) {
    	if(state == StapState.NONE)
    		return;
    	if(state == StapState.WAITINGTOTAP && timer.hasFinished()) {
    		startCombo();
    	} else if (state == StapState.TAPPING && timer.hasFinished()) {
    		finishCombo();
    	}
    }
    
    @SubscribeEvent
    public void event(AttackEntityEvent e) {
    	target = e.target;
    	if(isSecondCall() && eventType.getMode() == EventType.Attack) 
    		sTap();
    }
    
    @SubscribeEvent
    public void event(LivingUpdateEvent e) {
    	if(eventType.getMode() == EventType.Hurt && e.entityLiving.hurtTime > 0 && e.entityLiving.hurtTime == e.entityLiving.maxHurtTime && e.entity == this.target)
    		sTap();
    }
    
    public void sTap() {
    	if(state != StapState.NONE)
    		return;
    	if(!(Math.random() <= chance.getInput() / 100)) {
    		hits++;
    	}
    	if(mc.thePlayer.getDistanceToEntity(target) > range.getInput()
    			|| (onlyPlayers.isToggled() && !(target instanceof EntityPlayer))
    			|| (onlySword.isToggled() && !Utils.Player.isPlayerHoldingSword())
    			|| !(rhit >= hits))
    		return;
    	trystartCombo();
    }


    public void finishCombo() {
    	state = StapState.NONE;
    	KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
    	hits = 0;
		int easports = (int) (hitPer.getInputMax() - hitPer.getInputMin() + 1);
		rhit = ThreadLocalRandom.current().nextInt((easports));
		rhit += (int) hitPer.getInputMin();
    }

    public void startCombo() {
        state = StapState.TAPPING;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
        double cd = (double) ThreadLocalRandom.current().nextDouble(waitMs.getInputMin(), waitMs.getInputMax()+0.01);
        if (dynamic.isToggled()) {
        	cd = 3 - mc.thePlayer.getDistanceToEntity(target) < 3 ? (cd + (3 - mc.thePlayer.getDistanceToEntity(target) * tapMultiplier.getInput() * 10)) : cd; 
        }
        
        timer.setCooldown((long) cd);
    	timer.start();
    }
    
    
    public void trystartCombo() {
    	state = StapState.WAITINGTOTAP;
    	timer.setCooldown((long)ThreadLocalRandom.current().nextDouble(actionMs.getInputMin(),  actionMs.getInputMax()+0.01));
    	timer.start();
    }
    
    public void guiButtonToggled(TickSetting b) {

    } 

    private boolean isSecondCall() {
    	if(call) {
    		call = false;
    		return true;
    	} else {
    		call = true;
    		return false;
    	}
    }
    
    public enum StapState {
    	NONE,
    	WAITINGTOTAP,
    	TAPPING,
    }
}
