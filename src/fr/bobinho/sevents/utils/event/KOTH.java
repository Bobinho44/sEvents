package fr.bobinho.sevents.utils.event;

import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import fr.bobinho.sevents.utils.format.BDurationFormat;
import fr.bobinho.sevents.utils.location.BLocationUtil;
import fr.bobinho.sevents.utils.scheduler.BScheduler;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private static final int LOOT_TABLE_SIZE = 45;
    private static final Random RANDOM = new Random();

    private final Location corner;
    private final Location oppositeCorner;
    private final Map<UUID, Integer> gettersTime = new HashMap<>();
    private UUID actualGetter;
    private final ItemStack[] lootTable = new ItemStack[LOOT_TABLE_SIZE];
    private int captureCooldown;
    private JGlobalMethodBasedScoreboard scoreboard;

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

    public void setActualGetter(@Nonnull UUID actualGetter) {
        Validate.notNull(actualGetter, "actualGetter is null");

        this.actualGetter = actualGetter;
    }

    public void increaseActualGetterTime() {
        if (getActualGetter() != null) {
            getGettersTime().put(actualGetter, (getGettersTime().get(actualGetter) == null ? 0 : getGettersTime().get(actualGetter)) + 1);
        }
    }

    public void searchTheNewActualGetter() {
        List<Player> actualGetters = Bukkit.getOnlinePlayers().stream().filter(this::isInCaptureZone).collect(Collectors.toList());

        if (actualGetters.size() == 1 && !getActualGetter().equals(actualGetters.get(0).getUniqueId())) {
            setActualGetter(actualGetters.get(0).getUniqueId());
            resetCaptureCooldown();
        }
    }

    public ItemStack[] getLootTable() {
        return lootTable;
    }

    public List<ItemStack> getShuffleledLootTable() {
        List<ItemStack> shuffleledLootTable = Arrays.asList(getLootTable());
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
        captureCooldown = 0;
    }

    public void showScoreBoard() {
        scoreboard.setTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Event");
        scoreboard.setLines(
                ChatColor.AQUA + "" + ChatColor.BOLD + "KOTH:",
                ChatColor.WHITE + getName() + ": " + ChatColor.RED + getTimer(),
                ChatColor.WHITE + "(" + ((int) getCorner().getX() + getOppositeCorner().getX()) / 2 + " , " + ((int) getCorner().getZ() + getOppositeCorner().getZ()) / 2 + ")",
                " ",
                ChatColor.GREEN + "luxepvp.net"
        );
    }

    public void hideScoreBoard() {
        scoreboard.destroy();
    }

    public void giveKOTHItems() {
        getGettersTime().entrySet().stream()
                .filter(getter -> Bukkit.getPlayer(getter.getKey()) != null)
                .max(Map.Entry.comparingByValue()).ifPresent(winner -> {
                    int itemNumber = RANDOM.nextInt(MINIMUM_LOOTED_ITEM_NUMBER) + MAXIMUM_BONUS_LOOTED_ITEM_NUMBER;
                    for (int i = 0; i < itemNumber && getShuffleledLootTable().size() > i; i++) {
                        Objects.requireNonNull(Bukkit.getPlayer(winner.getKey())).getInventory().addItem(getShuffleledLootTable().get(i));
                    }
                });
    }

    @Override
    public void start() {
        scoreboard = new JGlobalMethodBasedScoreboard();

        BScheduler.asyncScheduler().every(1, TimeUnit.SECONDS).run(() -> {

            if (getCaptureCooldown() >= COOLDOWN) {
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
        giveKOTHItems();
        hideScoreBoard();
    }

}