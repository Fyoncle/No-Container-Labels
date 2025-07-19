package net.nocontainerlabels.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {
    @Unique
    private static final Set<String> VANILLA_CONTAINERS = Set.of(
            "crafting", "enchant", "anvil", "brewing", "loom", "grindstone",
            "cartography", "stonecutter", "dispenser", "shulker_box", "hopper",
            "furnace", "smoker", "blast_furnace", "dropper", "chest", "ender_chest",
            "barrel", "smithing"
    );

    @Inject(
            method = "drawForeground",
            at = @At("HEAD"),
            cancellable = true
    )
    private void eraseVanillaLabels(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        ScreenHandler handler = ((HandledScreen<?>) (Object) this).getScreenHandler();
        Identifier id = Registries.SCREEN_HANDLER.getId(handler.getType());

        if (id != null && "minecraft".equals(id.getNamespace())) {
            String path = id.getPath();
            boolean isListedContainer = VANILLA_CONTAINERS.stream().anyMatch(path::contains);
            boolean isVanillaChest = path.startsWith("generic_9x") || path.equals("chest");

            if (isListedContainer || isVanillaChest) {
                ci.cancel();
            }
        }
    }
}