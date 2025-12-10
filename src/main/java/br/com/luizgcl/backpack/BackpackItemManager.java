package br.com.luizgcl.backpack;

import br.com.luizgcl.utils.SerializerUtils;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class BackpackItemManager {

    private final NamespacedKey key;
    private final NamespacedKey uuidKey; // Para garantir que cada mochila seja única
    private final NamespacedKey sizeKey;

    public BackpackItemManager(JavaPlugin plugin) {
        this.key = new NamespacedKey(plugin, "backpack_content");
        this.uuidKey = new NamespacedKey(plugin, "backpack_uuid");
        this.sizeKey = new NamespacedKey(plugin, "backpack_size");
    }

    // Abre a mochila baseada no ITEM que está na mão
    public void openBackpack(Player player, ItemStack backpackItem) {
        ItemMeta meta = backpackItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        int size = container.getOrDefault(sizeKey, PersistentDataType.INTEGER, 27);

        // Cria o inventário
        Inventory inventory = Bukkit.createInventory(player, size, 
            MiniMessage.miniMessage().deserialize("<dark_gray>Mochila"));

        // Se já tem itens salvos, carrega
        if (container.has(key, PersistentDataType.STRING)) {
            String data = container.get(key, PersistentDataType.STRING);
            try {
                ItemStack[] items = SerializerUtils.itemStackArrayFromBase64(data);
                inventory.setContents(items);
            } catch (IOException e) {
                player.sendMessage("Erro ao ler dados da mochila.");
            }
        }

        player.openInventory(inventory);
    }

    // Salva o inventário DENTRO do item
    public void saveBackpack(ItemStack backpackItem, Inventory inventory) {
        ItemMeta meta = backpackItem.getItemMeta();
        
        try {
            String data = SerializerUtils.itemStackArrayToBase64(inventory.getContents());
            
            // Salva na NBT do item
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, data);
            
            // Atualiza o item
            backpackItem.setItemMeta(meta);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Cria uma nova mochila para dar ao player
    public ItemStack createNewBackpack(BackpackType type) {
        ItemStack item = new ItemStack(Material.BUNDLE);
        ItemMeta meta = item.getItemMeta();

        String backpackId = UUID.randomUUID().toString();
        
        meta.displayName(MiniMessage.miniMessage().deserialize("Mochila de " + type.getName()).color(type.getColor()));

        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.lore(
            List.of(
                MiniMessage.miniMessage().deserialize("Mochila: #" + backpackId.charAt(12)).color(TextColor.color(200, 200, 200)),
                MiniMessage.miniMessage().deserialize("Tamanho: " + type.getInventorySize() + " Espaços").color(TextColor.color(200, 200, 200)),
                MiniMessage.miniMessage().deserialize("Clique para abrir!").color(TextColor.color(150, 150, 150))
            )
        );
        
        // Dá um ID único para ela (opcional, mas bom para evitar stackar mochilas diferentes)
        meta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, backpackId);
        meta.getPersistentDataContainer().set(sizeKey, PersistentDataType.INTEGER, type.getInventorySize());
        
        item.setItemMeta(meta);
        return item;
    }
    
    // Verifica se o item é uma mochila nossa
    public boolean isBackpack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(uuidKey, PersistentDataType.STRING);
    }
}