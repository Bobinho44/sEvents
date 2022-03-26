package fr.bobinho.sevents.utils.event;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import fr.bobinho.sevents.utils.format.BDurationFormat;
import fr.bobinho.sevents.utils.location.BLocationUtil;
import fr.bobinho.sevents.utils.scheduler.BScheduler;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KOTH extends Event {

    private static final int MINIMUM_LOOTED_ITEM_NUMBER = 3;
    private static final int MAXIMUM_BONUS_LOOTED_ITEM_NUMBER = 2;
    private static final int COOLDOWN = 600;
    public static final int LOOT_TABLE_SIZE = 45;
    private static final Random RANDOM = new Random();

    private final Location corner;
    private final Location oppositeCorner;
    private final Map<UUID, Integer> gettersTime = new HashMap<>();
    private UUID actualGetter;
    private final ItemStack[] lootTable = new ItemStack[LOOT_TABLE_SIZE];
    private int captureCooldown;
    private JGlobalMethodBasedScoreboard scoreboard;

    public KOTH(@Nonnull String name, @Nonnull Location corner, @Nonnull Location oppositeCorner, @Nonnull ItemStack[] lootTable) {
        this(name, corner, oppositeCorner);

        Validate.notNull(lootTable, "lootTable is null");
        setLootTable(lootTable);
    }

    public KOTH(@Nonnull String name, @Nonnull Location corner, @Nonnull Location oppositeCorner) {
        super(name);

        Validate.notNull(corner, "corner is null");
        Validate.notNull(oppositeCorner, "oppositeCorner is null");

        this.corner = corner;
        this.oppositeCorner = oppositeCorner;
    }

    public Location getCorner() {
        return corner;
    }

    public Location getOppositeCorner() {
        return oppositeCorner;
    }

    public boolean isInCaptureZone(@Nonnull Player player) {
        Validate.notNull(player, "player is null");

        return BLocationUtil.isBetweenTwo2DPoint(getCorner(), getOppositeCorner(), player.getLocation());
    }

    public Map<UUID, Integer> getGettersTime() {
        return gettersTime;
    }

    public UUID getActualGetter() {
        return actualGetter;
    }

    public void setActualGetter(UUID actualGetter) {
        this.actualGetter = actualGetter;
    }

    public void increaseActualGetterTime() {
        if (getActualGetter() != null) {
            getGettersTime().put(actualGetter, (getGettersTime().get(actualGetter) == null ? 0 : getGettersTime().get(actualGetter)) + 1);
        }
    }

    public void searchTheNewActualGetter() {
        List<Player> actualGetters = Bukkit.getOnlinePlayers().stream()
                .filter(player -> isInCaptureZone(player) && player.getGameMode() == GameMode.SURVIVAL).collect(Collectors.toList());

        if (getActualGetter() != null && actualGetters.stream().noneMatch(getter -> getter.getUniqueId().equals(getActualGetter()))) {
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.RED + Bukkit.getOfflinePlayer(getActualGetter()).getName() + " has lost control of the area!"));
            resetCaptureCooldown();
            setActualGetter(null);
        }

        if (actualGetters.size() == 1 && getActualGetter() == null) {
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.GREEN + actualGetters.get(0).getName() + " has taken control of the area!"));
            setActualGetter(actualGetters.get(0).getUniqueId());
        }
    }

    public ItemStack[] getLootTable() {
        return lootTable;
    }

    public List<ItemStack> getShuffleledLootTable() {
        List<ItemStack> shuffleledLootTable = Arrays.stream(getLootTable()).filter(Objects::nonNull).collect(Collectors.toList());
        Collections.shuffle(shuffleledLootTable);
        return shuffleledLootTable;
    }

    public void setLootTable(@Nonnull ItemStack[] lootTable) {
        Validate.notNull(lootTable, "lootTable is null");

        System.arraycopy(lootTable, 0, this.lootTable, 0, LOOT_TABLE_SIZE);
    }

    public int getCaptureCooldown() {
        return captureCooldown;
    }

    public String getTimer() {
        return BDurationFormat.getAsMinuteSecondFormat(COOLDOWN - getCaptureCooldown());
    }

    public void increaseCaptureCooldown() {
        if (getActualGetter() != null) {
            captureCooldown++;
        }
    }

    public void resetCaptureCooldown() {
        getGettersTime().clear();
        captureCooldown = 0;
    }

    public void showScoreBoard() {
        scoreboard.setTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Event");
        scoreboard.setLines(
                " ",
                ChatColor.AQUA + "" + ChatColor.BOLD + " KOTH:",
                ChatColor.WHITE + getName() + ": " + ChatColor.RED + getTimer(),
                ChatColor.WHITE + "(" + ((int) getCorner().getX() + getOppositeCorner().getX()) / 2 + " , " + ((int) getCorner().getZ() + getOppositeCorner().getZ()) / 2 + ")",
                " ",
                ChatColor.GREEN + "luxepvp.net"

        );
        Bukkit.getOnlinePlayers().forEach(player -> scoreboard.addPlayer(player));
    }

    public void hideScoreBoard() {
        Bukkit.getOnlinePlayers().forEach(player -> scoreboard.removePlayer(player));
        scoreboard.destroy();
    }

    public void giveKOTHItems() {
        if (getCaptureCooldown() < COOLDOWN) {
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageKOTH.NO_WINNER_KOTH.getMessage(getName())));
            return;
        }

        getGettersTime().entrySet().stream()
                .filter(getter -> Bukkit.getPlayer(getter.getKey()) != null)
                .max(Map.Entry.comparingByValue()).ifPresent(winner -> {
                    int itemNumber = RANDOM.nextInt(MINIMUM_LOOTED_ITEM_NUMBER) + MAXIMUM_BONUS_LOOTED_ITEM_NUMBER;
                    Player KOTHPlayerWinner = Objects.requireNonNull(Bukkit.getPlayer(winner.getKey()));
                    for (int i = 0; i < itemNumber && getShuffleledLootTable().size() > i; i++) {
                        KOTHPlayerWinner.getInventory().addItem(getShuffleledLootTable().get(i));
                    }

                    Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageKOTH.WINNER_KOTH.getMessage(KOTHPlayerWinner.getName(), getName())));
                });

    }

    @Override
    public void start() {
        scoreboard = new JGlobalMethodBasedScoreboard();
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(MessageKOTH.START_KOTH.getMessage(getName())));

        BScheduler.syncScheduler().every(1, TimeUnit.SECONDS).run(task -> {

            if (getCaptureCooldown() >= COOLDOWN*1000) {
                return;
            }

            if (getCaptureCooldown() >= COOLDOWN) {
                EventManager.stopAnEvent(this);
                task.cancel();
                return;
            }

            increaseActualGetterTime();
            increaseCaptureCooldown();
            searchTheNewActualGetter();
            showScoreBoard();
        });
    }

    @Override
    public void stop() {
        EventManager.deleteAnEvent(this);
        EventManager.createAnEvent(new KOTH(getName(), getCorner(), getOppositeCorner(), getLootTable()));
        giveKOTHItems();
        captureCooldown = COOLDOWN*1000;
        hideScoreBoard();
    }

}