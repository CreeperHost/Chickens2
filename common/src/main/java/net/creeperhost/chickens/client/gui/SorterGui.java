package net.creeperhost.chickens.client.gui;

import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.chickens.blockentities.SorterBlockEntity;
import net.creeperhost.chickens.blockentities.data.TraitFilter;
import net.creeperhost.chickens.client.ChickenGuiTextures;
import net.creeperhost.chickens.config.Config;
import net.creeperhost.chickens.containers.SorterMenu;
import net.creeperhost.chickens.data.ChickenData;
import net.creeperhost.chickens.data.ChickenDataManager;
import net.creeperhost.chickens.data.ChickenVariant;
import net.creeperhost.chickens.init.ChickenTraits;
import net.creeperhost.chickens.item.ItemChicken;
import net.creeperhost.chickens.item.ItemChickenEgg;
import net.creeperhost.chickens.trait.Trait;
import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.ForegroundRender;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerGuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerScreenAccess;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Align;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Axis;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;
import static net.minecraft.util.TriState.*;

/**
 * Created by brandon3055 on 01/03/2024
 */
public class SorterGui extends ContainerGuiProvider<SorterMenu> {
    public static final int GUI_WIDTH = 176;
    public static final int GUI_HEIGHT = 232;
    private GuiElement<?> variantItemElement;
    private SorterMenu menu;
    private SorterBlockEntity tile;
    private List<ResourceLocation> variantCache = new ArrayList<>();

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(3).enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), ChickenGuiTextures.get("sorter"));
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<SorterMenu> screenAccess) {
        gui.initStandardGui(GUI_WIDTH, GUI_HEIGHT);
        gui.onTick(this::update);
        menu = screenAccess.getMenu();
        tile = menu.tile;
        gui.setGuiTitle(tile.getDisplayName());
        GuiElement<?> root = gui.getRoot();

        GuiText title = new GuiText(root, gui.getGuiTitle())
                .setTextColour(0xFF404040)
                .setShadow(false)
                .constrain(TOP, relative(root.get(TOP), 4))
                .constrain(HEIGHT, Constraint.literal(8))
                .constrain(LEFT, match(root.get(LEFT)))
                .constrain(RIGHT, match(root.get(RIGHT)));

        var playInv = GuiSlots.player(root, screenAccess, menu.main, menu.hotBar);
        Constraints.placeInside(playInv.container, root, Constraints.LayoutPos.BOTTOM_CENTER, 0, -7);
        GuiText invTitle = new GuiText(playInv.container, Component.translatable("container.inventory"))
                .setAlignment(Align.LEFT)
                .setTextColour(0xFF404040)
                .setShadow(false)
                .constrain(WIDTH, match(playInv.container.get(WIDTH)))
                .constrain(HEIGHT, literal(8));
        Constraints.placeInside(invTitle, playInv.container, Constraints.LayoutPos.TOP_LEFT, 0, -10);

        //Machine Slots
        GuiTexture slotTex = new GuiTexture(root, () -> ChickenGuiTextures.get("elements/sorter_slot" + (menu.scanning.get() ? "_active" : "")));
        Constraints.size(slotTex, 22, 22);
        Constraints.placeOutside(slotTex, playInv.container, Constraints.LayoutPos.TOP_CENTER, 0, -12);

        GuiSlots inputSlot = GuiSlots.singleSlot(slotTex, screenAccess, menu.input)
                .setSlotTexture(integer -> null);
        Constraints.center(inputSlot, slotTex);
        Constraints.bind(new Scanner(root, menu), slotTex, 1);

        GuiTexture greenArrow = new GuiTexture(root, ChickenGuiTextures.get("elements/green_arrow"))
                .setTooltipDelay(0)
                .setTooltip(Component.translatable("gui.chickens.sorter.pass"));
        Constraints.size(greenArrow, 24, 16);
        Constraints.placeOutside(greenArrow, inputSlot, Constraints.LayoutPos.MIDDLE_RIGHT, 4, 0);

        GuiSlots passSlot = GuiSlots.singleSlot(root, screenAccess, menu.viable);
        Constraints.placeOutside(passSlot, greenArrow, Constraints.LayoutPos.MIDDLE_RIGHT, 2, 0);

        GuiTexture redArrow = new GuiTexture(root, ChickenGuiTextures.get("elements/red_arrow"))
                .setTooltipDelay(0)
                .setTooltip(Component.translatable("gui.chickens.sorter.fail"));
        Constraints.size(redArrow, 24, 16);
        Constraints.placeOutside(redArrow, inputSlot, Constraints.LayoutPos.MIDDLE_LEFT, -4, 0);

        GuiSlots failSlot = GuiSlots.singleSlot(root, screenAccess, menu.nonViable);
        Constraints.placeOutside(failSlot, redArrow, Constraints.LayoutPos.MIDDLE_LEFT, -2, 0);

        //Filter
        createFilterList(gui, root, menu);


        if (Config.INSTANCE.enableEnergy) {
            GuiSlots energySlot = GuiSlots.singleSlot(root, screenAccess, menu.energySlot)
                    .setEmptyIcon(PolyTextures.get("slots/energy"))
                    .constrain(LEFT, match(playInv.container.get(LEFT)))
                    .constrain(BOTTOM, relative(invTitle.get(TOP), -2));

            var energyBar = GuiEnergyBar.simpleBar(root);
            energyBar.container
                    .constrain(TOP, relative(root.get(TOP), 5))
                    .constrain(BOTTOM, relative(energySlot.get(TOP), -1))
                    .constrain(LEFT, relative(energySlot.get(LEFT), 0))
                    .constrain(RIGHT, relative(energySlot.get(RIGHT), 0));
            energyBar.primary
                    .setCapacity(tile.energy::getMaxEnergyStored)
                    .setEnergy(menu.energy::get);
        }

        GuiButton rsButton = GuiButton.redstoneButton(root, tile);
        Constraints.placeInside(rsButton, root, Constraints.LayoutPos.TOP_RIGHT, -2, 2);
    }

    public void createFilterList(ModularGui gui, GuiElement<?> root, SorterMenu menu) {
        GuiRectangle listBg = GuiRectangle.vanillaSlot(root);
        Constraints.placeInside(listBg, root, Constraints.LayoutPos.TOP_CENTER, -4, 14);
        Constraints.size(listBg, (18 * 9) - 8, 98);

        GuiScrolling scroll = new GuiScrolling(listBg);
        Constraints.bind(scroll, listBg, 1);
        scroll.installContainerElement(new GuiElement<>(scroll));
        scroll.getContentElement()
                .constrain(WIDTH, null)
                .constrain(LEFT, match(scroll.get(LEFT)))
                .constrain(RIGHT, match(scroll.get(RIGHT)));

        var bar = GuiSlider.vanillaScrollBar(scroll, Axis.Y);
        bar.container
                .constrain(TOP, match(listBg.get(TOP)))
                .constrain(BOTTOM, match(listBg.get(BOTTOM)))
                .constrain(LEFT, relative(listBg.get(RIGHT), 0))
                .constrain(WIDTH, literal(8));
        bar.primary
                .setSliderState(scroll.scrollState(Axis.Y))
                .setScrollableElement(scroll);

        GuiElement<?> container = scroll.getContentElement();

        int offset = 0;
        GuiElement<?> fertileItem = new GuiRectangle(container)
                .fill(0x35000000)
                .constrain(HEIGHT, literal(17))
                .constrain(LEFT, match(container.get(LEFT)))
                .constrain(RIGHT, match(container.get(RIGHT)))
                .constrain(TOP, relative(container.get(TOP), offset));
        offset += (int) fertileItem.ySize() + 1;

        GuiButton fertileBtn = GuiButton.vanillaAnimated(fertileItem, () -> Component.translatable("gui.chickens.sorter.fertile_" + menu.fertilizedFilter.get().name().toLowerCase(Locale.ROOT)));
        fertileBtn.onClick(() -> menu.tile.sendDataValueToServer(menu.tile.fertilizedFilter, next(menu.fertilizedFilter.get())));
        Constraints.bind(fertileBtn, fertileItem, 1);

        GuiElement<?> viableItem = new GuiRectangle(container)
                .fill(0x35000000)
                .constrain(HEIGHT, literal(17))
                .constrain(LEFT, match(container.get(LEFT)))
                .constrain(RIGHT, match(container.get(RIGHT)))
                .constrain(TOP, relative(container.get(TOP), offset));
        offset += (int) viableItem.ySize() + 1;

        GuiButton viableBtn = GuiButton.vanillaAnimated(viableItem, () -> Component.translatable("gui.chickens.sorter.viable_" + menu.viableFilter.get().name().toLowerCase(Locale.ROOT)));
        viableBtn.onClick(() -> menu.tile.sendDataValueToServer(menu.tile.viableFilter, next(menu.viableFilter.get())));
        Constraints.bind(viableBtn, viableItem, 1);

        GuiElement<?> eggItem = new GuiRectangle(container)
                .fill(0x35000000)
                .constrain(HEIGHT, literal(17))
                .constrain(LEFT, match(container.get(LEFT)))
                .constrain(RIGHT, match(container.get(RIGHT)))
                .constrain(TOP, relative(container.get(TOP), offset));
        offset += (int) eggItem.ySize() + 1;

        GuiButton eggBtn = GuiButton.vanillaAnimated(eggItem, () -> Component.translatable("gui.chickens.sorter.egg_" + menu.eggFilter.get().name().toLowerCase(Locale.ROOT)));
        eggBtn.onClick(() -> menu.tile.sendDataValueToServer(menu.tile.eggFilter, next(menu.eggFilter.get())));
        Constraints.bind(eggBtn, eggItem, 1);

        for (RegistrySupplier<Trait> supplier : ChickenTraits.TRAITS) {
            GuiElement<?> item = new GuiRectangle(container)
                    .fill(0x35000000)
                    .constrain(HEIGHT, literal(28))
                    .constrain(LEFT, match(container.get(LEFT)))
                    .constrain(RIGHT, match(container.get(RIGHT)))
                    .constrain(TOP, relative(container.get(TOP), offset));
            offset += (int) item.ySize() + 1;

            Trait trait = supplier.get();
            Supplier<Boolean> isEnabled = () -> {
                for (TraitFilter filter : menu.traitFilter.get()) {
                    if (filter.trait().equals(trait)) return true;
                }
                return false;
            };

            GuiButton enable = GuiButton.vanillaAnimated(item, (Supplier<Component>) null);
            enable.setToggleMode(isEnabled);
            enable.onClick(() -> setFilter(menu, trait, isEnabled.get() ? null : new TraitFilter(trait, true, 0)));
            enable.setTooltip(Component.translatable("gui.chickens.sorter.enable_filter"));
            Constraints.size(enable, 50, 13);
            Constraints.placeInside(enable, item, Constraints.LayoutPos.TOP_LEFT, 1, 1);
            GuiRectangle rect = new GuiRectangle(enable)
                    .fill(0x3500FF00)
                    .setEnabled(isEnabled);
            Constraints.bind(rect, enable, 1);
            GuiText btnLabel = new GuiText(enable, Component.translatable("gui.chickens.sorter.enable"));
            Constraints.bind(btnLabel, enable);

            GuiText traitLabel = new GuiText(item, Component.translatable(trait.getDescriptionId()))
                    .constrain(LEFT, relative(enable.get(RIGHT), 2))
                    .constrain(RIGHT, relative(item.get(RIGHT), -2))
                    .constrain(TOP, relative(item.get(TOP), 4))
                    .constrain(HEIGHT, literal(8))
                    .setTextColour(() -> isEnabled.get() ? 0xFFFFFFFF : 0xFF606060)
                    .setAlignment(Align.MIN);

            GuiButton filterMode = GuiButton.vanillaAnimated(item, () -> Component.translatable("gui.chickens.sorter.greater_" + getFilterMode(menu, trait)));
            filterMode.setEnabled(isEnabled);
            filterMode.setTooltipSingle(() -> Component.translatable("gui.chickens.sorter.greater_" + getFilterMode(menu, trait) + ".info"));
            filterMode.onClick(() -> {
                TraitFilter filter = getFilter(menu, trait);
                if (filter != null) {
                    setFilter(menu, trait, new TraitFilter(filter.trait(), !filter.greater(), filter.value()));
                }
            });
            Constraints.size(filterMode, 50, 13);
            Constraints.placeInside(filterMode, item, Constraints.LayoutPos.BOTTOM_LEFT, 1, -1);

            GuiButton filterValue = GuiButton.vanillaAnimated(item, () -> Component.literal("" + getFilterValue(menu, trait)));
            filterValue.setEnabled(isEnabled);
            Constraints.size(filterValue, 40, 13);
            Constraints.placeOutside(filterValue, filterMode, Constraints.LayoutPos.MIDDLE_RIGHT, 1, 0);
            filterValue.onClick(() -> {
                TextInputDialog dialog = TextInputDialog.simpleDialog(root, Component.translatable("gui.chickens.sorter.value_input"));
                dialog.textField.setFilter(s -> s.isEmpty() || isNumber(s));
                dialog.setResultCallback(s -> {
                    TraitFilter filter = getFilter(menu, trait);
                    if (filter != null && (s.isEmpty() || isNumber(s))) {
                        setFilter(menu, trait, new TraitFilter(filter.trait(), filter.greater(), s.isEmpty() ? 0 : Double.parseDouble(s)));
                    }
                });
            });
        }

        variantItemElement = new GuiRectangle(container)
                .fill(0x35000000)
                .constrain(HEIGHT, literal(13))
                .constrain(LEFT, match(container.get(LEFT)))
                .constrain(RIGHT, match(container.get(RIGHT)))
                .constrain(TOP, relative(container.get(TOP), offset));

        GuiText filterLabel = new GuiText(container, Component.translatable("gui.chickens.sorter.varient_filter").append(":"))
                .setAlignment(Align.MIN)
                .constrain(LEFT, relative(variantItemElement.get(LEFT), 2))
                .constrain(TOP, relative(variantItemElement.get(TOP), 3))
                .constrain(RIGHT, relative(variantItemElement.get(RIGHT), -40))
                .constrain(HEIGHT, literal(8));

        GuiButton addVarient = GuiButton.vanillaAnimated(container, () -> Component.translatable("gui.chickens.sorter.add"));
        Constraints.size(addVarient, 50, 13);
        Constraints.placeInside(addVarient, variantItemElement, Constraints.LayoutPos.TOP_RIGHT, 0, 0);
        addVarient.onClick(() -> openVarientSelection(root));
    }

    public void openVarientSelection(GuiElement<?> root) {
        List<ResourceLocation> list = new ArrayList<>();
        for (ResourceLocation id : ChickenDataManager.getVariantIds()) {
            if (!menu.variantFilter.get().contains(id)) {
                list.add(id);
            }
        }
        new ItemSelectDialog<>(root, Component.translatable("gui.chickens.sorter.varient_select"), list, null, e -> {
            ChickenVariant variant = ChickenDataManager.getVariant(e);
            return Component.literal(variant == null ? e.toString() : variant.name());
        })
                .setOnItemSelected(e -> {
                    List<ResourceLocation> newList = new ArrayList<>(menu.variantFilter.get());
                    newList.add(e);
                    menu.tile.sendDataValueToServer(menu.tile.variantFilter, newList);
                });
    }

    public void updateVarientList() {
        List<ResourceLocation> list = menu.variantFilter.get();
        variantItemElement.constrain(HEIGHT, literal(13 + (variantCache.size() * 10)));
        variantItemElement.getChildren().forEach(variantItemElement::removeChild);

        int offset = 13;
        int i = 0;
        for (ResourceLocation location : list) {
            ChickenVariant variant = ChickenDataManager.getVariant(location);
            GuiRectangle back = new GuiRectangle(variantItemElement)
                    .constrain(HEIGHT, literal(10))
                    .constrain(LEFT, relative(variantItemElement.get(LEFT), 0))
                    .constrain(TOP, relative(variantItemElement.get(TOP), offset))
                    .constrain(RIGHT, relative(variantItemElement.get(RIGHT), 0))
                    .fill((i & 1) == 0 ? 0x25FFFFFF : 0x25000000);

            GuiText item = new GuiText(variantItemElement, Component.literal("- " + (variant == null ? location.toString() : variant.name())).withStyle(ChatFormatting.GOLD))
                    .setAlignment(Align.MIN);
            Constraints.bind(item, back, 0, 2, 0, 0);

            GuiButton delete = GuiButton.flatColourButton(back, () -> Component.literal("X").withStyle(ChatFormatting.RED), hover -> hover ? 0x50FFFFFF : 0x10FFFFFF);
            delete.setTooltip(Component.translatable("gui.chickens.sorter.delete"));
            delete.onClick(() -> {
                List<ResourceLocation> newList = new ArrayList<>(menu.variantFilter.get());
                newList.remove(location);
                menu.tile.sendDataValueToServer(menu.tile.variantFilter, newList);
            });
            Constraints.size(delete, 16, 10);
            Constraints.placeInside(delete, back, Constraints.LayoutPos.MIDDLE_RIGHT);

            offset += 10;
            i++;
        }
    }

    public void update() {
        List<ResourceLocation> list = menu.variantFilter.get();
        if (!list.equals(variantCache)) {
            variantCache.clear();
            list.forEach(e -> variantCache.add(ResourceLocation.fromNamespaceAndPath(e.getNamespace(), e.getPath())));
            updateVarientList();
        }
    }

    @Nullable
    public TraitFilter getFilter(SorterMenu menu, Trait trait) {
        for (TraitFilter filter : menu.traitFilter.get()) {
            if (filter.trait() == trait) {
                return filter;
            }
        }
        return null;
    }

    public TriState next(TriState value) {
        return value == TRUE ? FALSE : value == FALSE ? DEFAULT : TRUE;
    }

    public boolean getFilterMode(SorterMenu menu, Trait trait) {
        TraitFilter filter = getFilter(menu, trait);
        return filter != null && filter.greater();
    }

    public double getFilterValue(SorterMenu menu, Trait trait) {
        TraitFilter filter = getFilter(menu, trait);
        return filter == null ? 0 : filter.value();
    }

    public void setFilter(SorterMenu menu, Trait trait, @Nullable TraitFilter newFilter) {
        List<TraitFilter> newList = new ArrayList<>();
        boolean added = false;
        for (TraitFilter filter : getFilters(menu)) {
            if (filter.trait().equals(trait)) {
                if (newFilter != null) {
                    newList.add(newFilter);
                    added = true;
                }
            } else {
                newList.add(filter);
            }
        }
        if (newFilter != null && !added) {
            newList.add(newFilter);
        }
        setFilters(menu, newList);
    }

    public List<TraitFilter> getFilters(SorterMenu menu) {
        return menu.traitFilter.get();
    }

    public void setFilters(SorterMenu menu, List<TraitFilter> filters) {
        menu.tile.sendDataValueToServer(menu.tile.traitFilter, filters);
    }

    public static boolean isNumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static class Scanner extends GuiElement<Scanner> implements ForegroundRender {
        private static final Random randy = new Random();
        private final SorterMenu menu;

        public Scanner(@NotNull GuiParent<?> parent, SorterMenu menu) {
            super(parent);
            this.menu = menu;
        }

        @Override
        public void renderInFront(GuiRender render, double mouseX, double mouseY, float partialTicks) {
            ItemStack stack = menu.input.getSlot(0).getItem();
            if (stack.isEmpty() || (!(stack.getItem() instanceof ItemChickenEgg) && !(stack.getItem() instanceof ItemChicken)) || !menu.scanning.get()) {
                return;
            }
            ChickenData data = ChickenData.fromItem(stack);
            if (data == null) return;
            randy.setSeed(menu.scanCount.get());
            float progress = menu.progress.get() / (float) Config.INSTANCE.sorterProcessTime;

            float barWidth = (Math.min(1, progress * 8) - Math.max(0, (progress - (7 / 8F)) * 8)) * 1;
            float barPos = Math.min(Math.max(progress - (1 / 8F), 0), 6 / 8F) / 0.75F;
            barPos = (float) Math.sin(barPos * Math.PI) * 17F;

            render.pose().pushMatrix();
            render.pose().translate(0, barPos);
            render.fill(xCenter() - 9, yMin() + 1, xCenter() + 9, yMin() + 2, 0x00FF0000 | ((int) (barWidth * 0xFF) << 24));
            render.pose().popMatrix();
        }
    }


    public static class Screen extends ModularGuiContainer<SorterMenu> {
        public Screen(SorterMenu menu, Inventory inv, Component title) {
            super(menu, inv, new SorterGui());
            getModularGui().setGuiTitle(title);
        }
    }
}
