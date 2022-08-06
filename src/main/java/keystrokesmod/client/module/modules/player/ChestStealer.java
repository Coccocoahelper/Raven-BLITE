package keystrokesmod.client.module.modules.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

import keystrokesmod.client.module.Module;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.CoolDown;
import keystrokesmod.client.utils.Utils;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ChestStealer extends Module {

	private DoubleSliderSetting firstDelay, delay, closeDelay;
	private TickSetting autoClose;
	private boolean inChest, isEmpty;
	private CoolDown delayTimer = new CoolDown(0), closeTimer = new CoolDown(0);
	private ArrayList<Slot> sortedSlots;
	private ContainerChest chest;

	public ChestStealer() {
		super("ChestStealer", ModuleCategory.player);
		this.registerSetting(firstDelay = new DoubleSliderSetting("Open delay", 250, 450, 0, 1000, 1));
		this.registerSetting(delay = new DoubleSliderSetting("Delay", 150, 250, 0, 1000, 1));
		this.registerSetting(autoClose = new TickSetting("Auto Close", false));
		this.registerSetting(closeDelay = new DoubleSliderSetting("Close delay", 150, 250, 0, 1000, 1));
	}

	public void preClickGuiLoad() {
		guiButtonToggled(autoClose);
	}

	public void guiButtonToggled(TickSetting s) {
		if(s == autoClose) {
			closeDelay.isVisable = autoClose.isToggled();
			component.updateSettings();
		}
	}

	@SubscribeEvent
	public void openChest(TickEvent.RenderTickEvent e) {
		if(!Utils.Player.isPlayerInGame()) 
			return;
		if(mc != null && mc.currentScreen != null && mc.thePlayer != null) {
			if( mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
				if(!inChest) {
					chest = (ContainerChest) mc.thePlayer.openContainer;
					delayTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(firstDelay.getInputMin(), firstDelay.getInputMax()+0.01));
					delayTimer.start();
					generatePath(chest);
					inChest = true;
				}
				if(inChest && !sortedSlots.isEmpty()) {
						if(delayTimer.hasFinished()) {
							if(sortedSlots.get(0).s != -1) {
								mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, sortedSlots.get(0).s, 0, 1, mc.thePlayer);
								delayTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(delay.getInputMin(), delay.getInputMax()+0.01));
								delayTimer.start();
								sortedSlots.remove(0);
								//System.out.println(sortedSlots);
							} else {
								isEmpty = true;
							}
					} 
					if(isEmpty && autoClose.isToggled() && delayTimer.hasFinished()) {
						if(closeTimer.firstFinish()) {
							mc.thePlayer.closeScreen();
							isEmpty = false;
							inChest = false;
						} else {
							closeTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(closeDelay.getInputMin(), closeDelay.getInputMax()+0.01));
							closeTimer.start();
						}
					}
				}
			}
		} else {
			isEmpty = false;
			inChest = false;
		}
	}

	public void generatePath(ContainerChest chest) {
		ArrayList<Slot> slots = new ArrayList<Slot>();
		for(int i = 0;i < chest.getLowerChestInventory().getSizeInventory(); i++) {
			if(chest.getInventory().get(i) != null)
				slots.add(new Slot(i));
		}
		Slot[] ss = sort(slots.toArray(new Slot[slots.size()]));
		ArrayList<Slot> newSlots = new ArrayList<Slot>();
		Collections.addAll(newSlots, ss);
		this.sortedSlots = newSlots;
	}
	
	public static Slot[] sort(Slot[] in) {
        if (in == null || in.length == 0) {
            return in;
        }
        Slot[] out = new Slot[in.length];
        Slot current = in[ThreadLocalRandom.current().nextInt(0, in.length)];
        for (int i = 0; i < in.length; i++) {
            if (i == in.length -1) {
                out[in.length -1] = Arrays.stream(in).filter(p -> !p.visited).findAny().orElseGet(null);
                break;
            }
            final Slot finalCurrent = current;     
            out[i] = finalCurrent;
            finalCurrent.visit();
            Slot next = Arrays.stream(in).filter(p -> !p.visited).min(Comparator.comparingDouble(p -> p.getDistance(finalCurrent))).get();
            current = next;
        }
        return out;
    }
	
	public Slot getClosestSlot(Slot slot, ArrayList<Slot> slots) {
		slots.remove(slot);
		if(!slots.isEmpty()) {
			Slot closestSlot = null;
			for(Slot s : slots) {
				if(closestSlot == null) {
					closestSlot = s;
				} if((Math.abs(slot.x - s.x) + Math.abs(slot.y - s.y) < Math.abs(slot.x - closestSlot.x) + Math.abs(slot.y - closestSlot.y)) && (Math.abs(s.x - slot.x) + Math.abs(s.y - slot.y) != 0)){
					closestSlot = slot;
				}
			}
			
			return closestSlot;
		}
		
		return new Slot(-1);
	}
	
	public class Slot {
		final int x;
		final int y;
		final int s;
		boolean visited;
		
		public Slot(int s) {
			this.x = (s + 1) % 10;
			this.y = s / 9;
			this.s = s;
		}
		
	    public double getDistance(Slot s) {
	        return Math.abs(this.x-s.x) + Math.abs(this.y - s.y);
	    }
	    
	    public void visit() {
	        visited = true;
	    }
	}
}
