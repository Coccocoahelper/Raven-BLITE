package keystrokesmod.command.commands;

import keystrokesmod.command.Command;
import keystrokesmod.main.Ravenbplus;

import static keystrokesmod.clickgui.raven.CommandLine.print;

public class Uwu extends Command {
    private static boolean u;
    public Uwu() {
        super("uwu", "hevex added this lol", 0, 0,  new String[] {},  new String[] {"hevex", "weeb", "torture", "noplsno"});
        u = false;
    }

    @Override
    public void onCall(String[] args){
        if (u) {
            return;
        }

        Ravenbplus.getExecutor().execute(() -> {
            u = true;

            for(int i = 0; i < 4; ++i) {
                if (i == 0) {
                    print("&e" + "nya", 1);
                } else if (i == 1) {
                    print("&a" + "ichi ni san", 0);
                } else if (i == 2) {
                    print("&e" + "nya", 0);
                } else {
                    print("&a" + "arigatou!", 0);
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException var2) {
                }
            }

            u = false;
        });
    }
}
