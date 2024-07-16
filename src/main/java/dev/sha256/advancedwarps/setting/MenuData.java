package dev.sha256.advancedwarps.setting;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.conversation.SimpleStringPrompt;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.*;

@Getter
public class MenuData extends YamlConfig {

    private final static ConfigItems<MenuData> menus = ConfigItems.fromFile("", "menus.yml", MenuData.class);

    private final String name;

    private String title;
    private List<String> info;
    private int size;
    private List<ButtonData> buttons;

    private static final HashMap<UUID, PlayerData.Warp> selectedWarps = new HashMap<>();



    private Player player;

    //TODO Needs to know previous menu to go back to it;
    //TODO Need to make it so we have different types of buttons StringPrompt, IntPrompt;


    private MenuData(String name) {
        this.name = name;


        this.setPathPrefix(name);
        this.loadConfiguration(NO_DEFAULT, "menus.yml");
    }

    @Override
    protected void onLoad() {
        this.title = this.getString("Title");
        this.info = this.getStringList("Info");
        this.size = (int) MathUtil.calculate(this.getString("Size"));
        this.buttons = this.loadButtons();
    }

    private List<ButtonData> loadButtons() {
        List<ButtonData> buttons = new ArrayList<>();

        for (Map.Entry<String, Object> entry : this.getMap("Buttons", String.class, Object.class).entrySet()) {
            String buttonName = entry.getKey();
            SerializedMap buttonSettings = SerializedMap.of(entry.getValue());

            buttons.add(ButtonData.deserialize(buttonSettings, buttonName));
        }

        return buttons;
    }

    public void displayTo(Player player) {
        this.player = player;

        this.toMenu().displayTo(player);
    }

    public Menu toMenu() {
        return this.toMenu(null);
    }

    public MenuPagged<PlayerData.Warp> toMenuWarp(Menu parent, List<PlayerData.Warp> warps) {

        for(ButtonData data : this.buttons) {
            return new MenuPagged<PlayerData.Warp>(parent, warps) {

                {
                    this.setTitle(title);
                    this.setSize(size);
                }

                @Override
                protected ItemStack convertToItemStack(PlayerData.Warp warp) {
                    CompMaterial material = warp.getMaterial() != null ? CompMaterial.fromMaterial(warp.getMaterial()) : data.getMaterial();
                    String title = warp.getName() != null ? warp.getName() : data.getTitle();
                    List<String> lore = data.getLore();

                    return ItemCreator.of(material, title, lore).make();
                }

                @Override
                protected void onPageClick(Player player, PlayerData.Warp warp, ClickType click) {
                    Common.log(parent.getTitle());
                    if (data.getMenuToOpen() != null) {
                        MenuData otherMenu = MenuData.findMenu(data.getMenuToOpen());

                        if (otherMenu == null)
                            parent.animateTitle("Invalid menu: " + data.getMenuToOpen());
                        else {
                            otherMenu.toMenu(parent).displayTo(player);
                            selectedWarps.put(player.getUniqueId(), warp);
                            Common.log("Warp Selected: " + selectedWarps.get(player.getUniqueId()).getId());
                        }
                    }
                }

                @Override
                public Menu newInstance() {
                    return toMenuWarp(parent, warps);
                }
            };
        }

        return null;
    }

    public Menu toMenu(Menu parent) {
        Map<Integer, Button> buttons = this.getButtons();

        return new Menu(parent) {

            {
                this.setTitle(title);
                this.setSize(size);
            }

            @Override
            protected List<Button> getButtonsToAutoRegister() {
                return new ArrayList<>(buttons.values());
            }

            @Override
            public ItemStack getItemAt(int slot) {

                if (buttons.containsKey(slot))
                    return buttons.get(slot).getItem();

                return NO_ITEM;
            }

            @Override
            protected String[] getInfo() {
                return Valid.isNullOrEmpty(info) ? null : Common.toArray(info);
            }

            @Override
            public Menu newInstance() {
                return toMenu(parent);
            }
        };
    }

    // Gets all the buttons from the menus
    public Map<Integer, Button> getButtons() {
        Map<Integer, Button> buttons = new HashMap<>();

        for (ButtonData data : this.buttons) {

            // Put the button in the map from the YML
            buttons.put(data.getSlot(), new Button() {



                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType click) {

                    if (data.getPlayerCommand() != null)
                        player.chat(data.getPlayerCommand());

                    if (data.getAnimation() != null)
                        menu.animateTitle(data.getAnimation());

                    if (data.isCopyItem())
                        player.getInventory().addItem(this.getItem());

                    if (data.getMenuToOpen() != null) {
                        MenuData otherMenu = MenuData.findMenu(data.getMenuToOpen());

                        if (otherMenu == null)
                            menu.animateTitle("Invalid menu: " + data.getMenuToOpen());
                        else {

                            //Checks if we need to load data.
                            if(data.getLoadData() != null) {
                                //Checks if the LoadData name is valid
                                if(data.getLoadData().equalsIgnoreCase("warps")) {
                                    PlayerData playerData = PlayerData.from(player.getUniqueId());
                                    List<PlayerData.Warp> warps = playerData.getWarps();
                                    otherMenu.toMenuWarp(menu, warps).displayTo(player);
                                } else {
                                    menu.animateTitle("Invalid LoadData: " + data.getLoadData());
                                }

                            } else {
                                // Load normal menu if no LoadData is found.
                                otherMenu.toMenu(menu).displayTo(player);
                            }
                        }
                    }

                    if(data.getTextPrompt() != null) {
                        Common.log("Text Prompt Selected Warp: " + selectedWarps.get(player.getUniqueId()).getId());
                        new SimpleStringPrompt(data.getTextPrompt()) {
                            @Override
                            protected void onValidatedInput(ConversationContext context, String input) {
                                PlayerData playerData = PlayerData.from(player.getUniqueId());
                                playerData.getWarp(selectedWarps.get(player.getUniqueId()).getId()).setName(input);
                                playerData.saveWarp();
                            }
                        }.show(player);
                    }
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(data.getMaterial(), data.getTitle(), data.getLore()).make();
                }
            });
        }

        return buttons;
    }

    @Getter
    @ToString
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ButtonData implements ConfigSerializable {

        private final String name;

        private int slot;
        private CompMaterial material;
        private String title;
        private List<String> lore;

        private String playerCommand;
        private String animation;
        private String menuToOpen;
        private boolean copyItem;
        private String textPrompt;
        private String loadData;

        @Override
        public SerializedMap serialize() {
            return null;
        }

        public static ButtonData deserialize(SerializedMap map, String buttonName) {
            ButtonData button = new ButtonData(buttonName);

            map.setRemoveOnGet(true);

            button.slot = map.containsKey("Slot") ? (int) MathUtil.calculate(map.getString("Slot")) : -1;
            Valid.checkBoolean(button.slot != -1, "Missing 'Slot' key from button: " + map);

            button.material = map.getMaterial("Material");
            Valid.checkNotNull(button.material, "Missing 'Material' key from button: " + map);

            button.title = map.getString("Title");
            Valid.checkNotNull(button.title, "Missing 'Title' key from button: " + map);

            button.lore = map.getStringList("Lore");
            Valid.checkNotNull(button.lore, "Missing 'Lore' key from button: " + map);

            SerializedMap click = map.getMap("Click");
            click.setRemoveOnGet(true);

            button.playerCommand = click.getString("Player_Command");
            button.animation = click.getString("Animate");
            button.menuToOpen = click.getString("Menu");
            button.copyItem = click.getBoolean("Copy_Item", false);
            button.textPrompt = click.getString("TextPrompt");
            button.loadData = click.getString("LoadData");



            Valid.checkBoolean(click.isEmpty(), "Found unrecognized button click settings: " + click);
            Valid.checkBoolean(map.isEmpty(), "Found unrecognized button settings: " + map);

            return button;
        }
    }

    public static MenuData findMenu(String name) {
        return menus.findItem(name);
    }

    public static void loadMenus() {
        menus.loadItems();
    }

    public static Set<String> getMenuNames() {
        return menus.getItemNames();
    }
}