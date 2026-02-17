package yagen.waitmydawn.pack_up.gui.loot_storage;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import yagen.waitmydawn.pack_up.network.PacketChangePage;
import yagen.waitmydawn.pack_up.network.PacketDeletePage;
import yagen.waitmydawn.pack_up.network.PacketExtractPage;
import yagen.waitmydawn.pack_up.network.PacketJumpToPage;

public class LootStorageScreen extends AbstractContainerScreen<LootStorageMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/gui/container/shulker_box.png");

    private EditBox pageEditBox;
    private boolean isEditingPage = false;

    public LootStorageScreen(LootStorageMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (Screen.hasShiftDown())
                PacketDistributor.sendToServer(new PacketJumpToPage(0));
            else
                PacketDistributor.sendToServer(new PacketChangePage(-1));
        }).bounds(this.leftPos - 25, this.topPos, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if (Screen.hasShiftDown())
                PacketDistributor.sendToServer(new PacketJumpToPage(-1));
            else
                PacketDistributor.sendToServer(new PacketChangePage(1));
        }).bounds(this.leftPos + this.imageWidth + 5, this.topPos, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("D"), button -> {
                    boolean isShiftDown = Screen.hasShiftDown();
                    PacketDistributor.sendToServer(new PacketDeletePage(isShiftDown));
                })
                .bounds(this.leftPos + this.imageWidth + 5, this.topPos + 22, 20, 20)
                .tooltip(Tooltip.create(Component.translatable("ui.pack_up.delete_tooltip")))
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("E"), button -> {
                    boolean isShiftDown = Screen.hasShiftDown();
                    PacketDistributor.sendToServer(new PacketExtractPage(isShiftDown));
                })
                .bounds(this.leftPos - 25, this.topPos + 22, 20, 20)
                .tooltip(Tooltip.create(Component.translatable("ui.pack_up.extract_tooltip")))
                .build());

        int editBoxWidth = 30;
        int editBoxX = this.leftPos + (this.imageWidth - editBoxWidth) / 2;
        int editBoxY = this.topPos - 14;

        this.pageEditBox = new EditBox(this.font, editBoxX, editBoxY, editBoxWidth, 12, Component.literal("Page"));
        this.pageEditBox.setMaxLength(6);
        this.pageEditBox.setVisible(false);
        this.pageEditBox.setBordered(true);
        this.pageEditBox.setFilter(s -> s.matches("\\d*"));
        this.addRenderableWidget(this.pageEditBox);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if (!isEditingPage) {
            String text = (this.menu.getCurrentPage() + 1) + " / " + Math.max(1, this.menu.getTotalPages());
            int textWidth = this.font.width(text);
            int textX = this.leftPos + (imageWidth - textWidth) / 2;
            int textY = this.topPos - 10;

            guiGraphics.drawString(this.font, text, textX, textY, 0xFFFFFFFF, true);

            if (mouseX >= textX && mouseX <= textX + textWidth && mouseY >= textY && mouseY <= textY + 9) {
                guiGraphics.fill(textX, textY + 9, textX + textWidth, textY + 10, 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isEditingPage && button == 0) {
            String text = (this.menu.getCurrentPage() + 1) + " / " + Math.max(1, this.menu.getTotalPages());
            int textWidth = this.font.width(text);
            int textX = this.leftPos + (imageWidth - textWidth) / 2;
            int textY = this.topPos - 10;

            if (mouseX >= textX && mouseX <= textX + textWidth && mouseY >= textY && mouseY <= textY + 10) {
                startEditing();
                return true;
            }
        }

        if (isEditingPage && !pageEditBox.isMouseOver(mouseX, mouseY)) {
            stopEditing(true);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isEditingPage) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                stopEditing(true);
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                stopEditing(false);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void startEditing() {
        this.isEditingPage = true;
        this.pageEditBox.setVisible(true);
        this.pageEditBox.setValue(String.valueOf(this.menu.getCurrentPage() + 1));
        this.pageEditBox.setFocused(true);
        this.setFocused(this.pageEditBox);
    }

    private void stopEditing(boolean apply) {
        this.isEditingPage = false;
        this.pageEditBox.setVisible(false);
        this.setFocused(null);

        if (apply) {
            try {
                String value = this.pageEditBox.getValue();
                if (!value.isEmpty()) {
                    int page = Integer.parseInt(value);
                    page = Math.max(1, page) - 1;
                    PacketDistributor.sendToServer(new PacketJumpToPage(page));
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
