package net.nocontainerlabels.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(HandledScreen.class)
public abstract class HandledScreenTitleMixin {
    @Unique
    private static final Set<String> VANILLA_CONTAINERS = Set.of(
            "crafting", "enchant", "anvil", "brewing", "loom", "grindstone",
            "cartography", "stonecutter", "dispenser", "shulker_box", "hopper",
            "furnace", "smoker", "blast_furnace", "dropper", "chest", "ender_chest",
            "barrel", "smithing"
    );

    @WrapOperation(
            method = "drawForeground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I",
                    ordinal = 0
            )
    )
    private int eraseContainerTitle(DrawContext instance, TextRenderer textRenderer, Text text,
                                    int x, int y, int color, boolean shadow,
                                    Operation<Integer> original) {
        try {
            ScreenHandler handler = ((HandledScreen<?>) (Object) this).getScreenHandler();
            ScreenHandlerType<?> type = handler.getType();

            if (!Registries.SCREEN_HANDLER.containsId(Registries.SCREEN_HANDLER.getId(type))) {
                return original.call(instance, textRenderer, text, x, y, color, shadow);
            }

            Identifier id = Registries.SCREEN_HANDLER.getId(type);
            if (id != null && "minecraft".equals(id.getNamespace())) {
                String path = id.getPath();
                boolean isListedContainer = VANILLA_CONTAINERS.stream().anyMatch(path::contains);
                boolean isVanillaChest = path.startsWith("generic_9x") || path.equals("chest");

                if (isListedContainer || isVanillaChest) {
                    return 0;
                }
            }
        } catch (Exception ignored) {
        }
        return original.call(instance, textRenderer, text, x, y, color, shadow);
    }
}