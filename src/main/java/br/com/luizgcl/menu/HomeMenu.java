package br.com.luizgcl.menu;

import br.com.luizgcl.utils.ItemBuilder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HomeMenu extends Menu {

    public HomeMenu() {
        // Cria um menu de 3 linhas (27 slots)
        super(3, MiniMessage.miniMessage().deserialize("<gradient:blue:aqua>Teleportes</gradient>"));
        
        initItems();
    }

    private void initItems() {
        // Botão do Spawn (Slot 11)
        setItem(11, 
            new ItemBuilder(Material.BEACON)
                .name("<green><b>Ir para o Casa</b>")
                .lore("<gray>Clique para teleportar", "<gray>para o inicio.")
                .build(),
            
            // A AÇÃO (Lambda)
            event -> {
                Player p = (Player) event.getWhoClicked();
                p.sendMessage("Teleportando...");
                p.closeInventory();
                p.performCommand("home");
            }
        );

        // Botão Fechar (Slot 15)
        setItem(15,
            new ItemBuilder(Material.BARRIER)
                .name("<red>Fechar")
                .build(),
            
            event -> event.getWhoClicked().closeInventory()
        );
        
        // Exemplo: Preencher fundo com vidro cinza
        
        ItemStack vidro = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for(int i = 0; i < 27; i++) {
             if(inventory.getItem(i) == null) setItem(i, vidro);
        }
    }
}