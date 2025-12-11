package br.com.luizgcl.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.TextDecoration;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public static ItemBuilder skull() {
        return new ItemBuilder(Material.PLAYER_HEAD);
    }

    public ItemBuilder name(String name) {
        // Remove itálico padrão do Minecraft e aplica MiniMessage
        meta.displayName(mm.deserialize(name).decoration(TextDecoration.ITALIC, false));
        return this;
    }

    public ItemBuilder lore(String... lines) {
        List<Component> lore = new ArrayList<>();
        for (String line : lines) {
            lore.add(mm.deserialize(line).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(lore);
        return this;
    }

    public ItemBuilder skullOwner(String playerName) {
        // Verifica se o meta atual é de uma caveira
        if (this.meta instanceof SkullMeta skullMeta) {
            // No Paper moderno, o jeito mais correto e assíncrono-safe
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
        }
        return this;
    }

    public ItemBuilder customSkullWithBase64(String base64) {
        if (base64 == null || base64.isEmpty()) return this;
        
        if (this.meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", base64));
            skullMeta.setPlayerProfile(profile);
        }
        return this;
    }

    public ItemStack build() {
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

        item.setItemMeta(meta);
        return item;
    }
}