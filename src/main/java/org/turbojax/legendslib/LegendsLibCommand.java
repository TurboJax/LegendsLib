package org.turbojax.legendslib;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class LegendsLibCommand {
    private static final Component helpMsg = MiniMessage.miniMessage()
            .deserialize("""
                     <dark_gray> ------------< <aqua>Legends<blue>Lib <dark_gray>>------------
                     <aqua>/legendslib <gold>give <player> <weapon> [count]<white>: Gives the player(s) the specified weapon.  "count" defaults to 1.
                     <aqua>/legendslib <gold>reload<white>: Reloads the weapons config
                     <aqua>/legendslib <gold>help<white>: Prints this help message
                     """);

    private final LegendsLib plugin;

    public LegendsLibCommand(LegendsLib plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> build(String label) {
        return Commands.literal(label)
                .then(Commands.literal("give")
                        .then(Commands.argument("players", ArgumentTypes.players())
                                .then(Commands.argument("weapon", StringArgumentType.string())
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes(this::giveWeapon)
                                        )
                                        .executes(this::giveWeapon)
                                )
                        )
                )
                .then(Commands.literal("reload")
                        .executes(this::reload)
                )
                .then(Commands.literal("help")
                        .executes(this::help)
                )
                .build();
    }

    public int giveWeapon(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        List<Player> players = List.of();
        try {
            players = ctx.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
        } catch (CommandSyntaxException err) {
            sender.sendMessage(Objects.requireNonNull(err.componentMessage()));
        }

        if (players.isEmpty()) {
            sender.sendMessage(Component.text("No players found", NamedTextColor.RED));
        }

        String weaponKey = ctx.getArgument("weapon", String.class);
        LegendaryWeapon weapon = plugin.getWeaponConfig().getWeapon(weaponKey);

        // Handling when the weapon doesn't exist
        if (weapon == null) {
            sender.sendMessage(Component.text("Weapon \"" + weaponKey + "\" does not exist.  Check the config.", NamedTextColor.RED));
            return 1;
        }

        // Preventing disabled weapons from being given out
        if (!weapon.getEnabled()) {
            sender.sendMessage(Component.text("You cannot give out disabled weapons.", NamedTextColor.RED));
            return 1;
        }

        // Making sure the weapon has a material
        if (weapon.getMaterial() == null) {
            sender.sendMessage(Component.text("Weapon \"" + weaponKey + "\" is missing a valid Material and cannot be created.", NamedTextColor.RED));
        }

        // Getting the amount of weapons the player should be given
        int count = 1;

        try {
            count = ctx.getArgument("count", Integer.class);
        } catch (IllegalArgumentException ignored) {}

        // Giving the players the weapon
        ItemStack item = weapon.createItem();
        item.setAmount(count);

        players.forEach(p -> p.give(item));

        if (players.size() > 1) {
            sender.sendMessage(Component.text("Gave " + count + " \"" + weaponKey + "\" to " + players.size() + " players", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Gave " + count + " \"" + weaponKey + "\" to ", NamedTextColor.GREEN).append(players.getFirst().displayName()));
        }

        return 1;
    }

    public int reload(CommandContext<CommandSourceStack> ctx) {
        plugin.getWeaponConfig().load();

        ctx.getSource().getSender().sendMessage(Component.text("Reloaded the weapons config", NamedTextColor.GREEN));
        return 1;
    }

    public int help(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getSender().sendMessage(helpMsg);
        return 1;
    }
}
