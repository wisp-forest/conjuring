package com.glisco.conjuring.compat.rei;

import com.glisco.conjuring.util.SoulfireForgeScreenHandler;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.clean.InputCleanHandler;
import me.shedaniel.rei.api.common.transfer.info.simple.SimplePlayerInventoryMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;

import java.util.ArrayList;
import java.util.List;

public class SoulfireForgeMenuInfo implements SimplePlayerInventoryMenuInfo<SoulfireForgeScreenHandler, SoulfireForgeDisplay> {

    private final SoulfireForgeDisplay display;

    public SoulfireForgeMenuInfo(SoulfireForgeDisplay display) {
        this.display = display;
    }

    @Override
    public Iterable<SlotAccessor> getInputSlots(MenuInfoContext<SoulfireForgeScreenHandler, ?, SoulfireForgeDisplay> context) {
        SoulfireForgeScreenHandler handler = context.getMenu();

        List<SlotAccessor> list = new ArrayList<>(handler.getInventory().size());
        for (int i = 0; i < handler.getInventory().size(); i++) {
            list.add(SlotAccessor.fromContainer(handler.getInventory(), i));
        }

        return list;
    }

    @Override
    public SoulfireForgeDisplay getDisplay() {
        return this.display;
    }

    public InputCleanHandler<SoulfireForgeScreenHandler, SoulfireForgeDisplay> getInputCleanHandler() {
        return context -> {
            SoulfireForgeScreenHandler handler = context.getMenu();
            for (SlotAccessor gridStack : getInputSlots(context)) {
                InputCleanHandler.returnSlotsToPlayerInventory(context, getDumpHandler(), gridStack);
            }

            clearInputSlots(handler);
        };
    }

}