package fr.elowyr.core.utils.menu;

import fr.elowyr.core.utils.menu.menus.VirtualGUI;

import java.util.ArrayList;
import java.util.List;

public class BoardContainer {

    private final VirtualGUI virtualGUI;
    private final List<Integer> slots = new ArrayList<>();

    public BoardContainer(VirtualGUI virtualGUI, int slotFrom, int length, int width) {
        this.virtualGUI = virtualGUI;

        int perimeter = length*width;
        int size = 0;
        int l = 0;
        int w = 0;
        while (perimeter != size) {
            slots.add(slotFrom+w+l);
            l++;
            size++;
            if(l == length){
                l = 0;
                w += 9;
            }
        }
    }

    public BoardContainer addSlot(int... slots){
        for (int slot : slots) {
            this.slots.add(slot);
        }
        return this;
    }

    public BoardContainer fillOffSetY(int slotFrom, int length){
        for (int i = 0; i < length; i++) {
            this.slots.add(slotFrom+=9);
        }
        return this;
    }

    public BoardContainer fillOffSetX(int slotFrom, int length){
        for (int i = 0; i < length; i++) {
            this.slots.add(slotFrom+i);
        }
        return this;
    }

    public BoardContainer removeSlot(int... index){
        for (int i : index) {
            this.slots.remove(i);
        }
        return this;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public VirtualGUI getVirtualGUI() {
        return virtualGUI;
    }
}
