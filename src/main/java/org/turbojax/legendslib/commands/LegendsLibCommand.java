package org.turbojax.legendslib.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.turbojax.legendslib.LegendsLib;

import java.util.List;

public class LegendsLibCommand {
    private static final MessageComponentSerializer msgSerializer = MessageComponentSerializer.message();
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
                                                .executes(c -> giveWeapon(c, c.getArgument("players", PlayerSelectorArgumentResolver.class), c.getArgument("weapon", String.class), c.getArgument("count", Integer.class)))
                                        )
                                        .executes(c -> giveWeapon(c, c.getArgument("players", PlayerSelectorArgumentResolver.class), c.getArgument("weapon", String.class), 1))
                                        .suggests((ctx, builder) -> {
                                            plugin.getWeaponConfig().getWeapons().stream().forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
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

    public int giveWeapon(CommandContext<CommandSourceStack> ctx, PlayerSelectorArgumentResolver resolver, String weaponKey, int count) {
        CommandSender sender = ctx.getSource().getSender();

        List<Player> players = List.of();
        try {
            players = resolver.resolve(ctx.getSource());
        } catch (CommandSyntaxException err) {
            sender.sendMessage(msgSerializer.deserialize(err.getRawMessage()));
        }

        if (players.isEmpty()) {
            sender.sendMessage(Component.text("No players found", NamedTextColor.RED));
        }

        // Handling when the weapon doesn't exist
        if (!plugin.getWeaponConfig().hasWeapon(weaponKey)) {
            sender.sendMessage(Component.text("Weapon \"" + weaponKey + "\" does not exist.  Check the config.", NamedTextColor.RED));
            return 1;
        }

        // Preventing disabled weapons from being given out
        if (!plugin.getWeaponConfig().getEnabled(weaponKey)) {
            sender.sendMessage(Component.text("You cannot give out disabled weapons.", NamedTextColor.RED));
            return 1;
        }

        // Making sure the weapon has a material
        if (plugin.getWeaponConfig().getMaterial(weaponKey) == null) {
            sender.sendMessage(Component.text("Weapon \"" + weaponKey + "\" is missing a valid Material and cannot be created.", NamedTextColor.RED));
        }

        // Giving the players the weapon
        ItemStack weapon = plugin.getWeaponConfig().createItem(weaponKey);

        // Handling when max stack size is too large
        int maxStackSize = weapon.getMaxStackSize();
        while (count > maxStackSize) {
            count -= maxStackSize;

            weapon.setAmount(maxStackSize);
            players.forEach(p -> p.give(weapon));
        }

        weapon.setAmount(count);
        players.forEach(p -> p.give(weapon));

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
