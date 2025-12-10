package br.com.luizgcl.pet;

import br.com.luizgcl.Main;
import org.bukkit.DyeColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.Random;

public class PetListener implements Listener {

    private final Random random = new Random();

    // 1. Remove o pet quando o dono sai do servidor
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Main.getPetManager().removePet(event.getPlayer(), true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Main.getPetManager().restorePet(event.getPlayer());
    }

    // 2. Proteção contra danos (fogo, queda, etc)
    @EventHandler
    public void onEnvironmentalDamage(EntityDamageEvent event) {
        if (Main.getPetManager().isPet(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    // 3. Interação: Bater (Esquerdo) -> Pular e Mudar Cor
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Sheep sheep)) return;
        if (!Main.getPetManager().isPet(sheep)) return;

        event.setCancelled(true); // Cancela o dano físico

        // Lógica do Pulo
        sheep.setVelocity(new Vector(0, 2.5, 0));
        
        // Lógica da Cor Aleatória
        DyeColor[] colors = DyeColor.values();
        sheep.setColor(colors[random.nextInt(colors.length)]);
        
        sheep.getWorld().playSound(sheep.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1, 1);
    }

    // 4. Interação: Clicar (Direito) -> Corações
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        if (!(event.getRightClicked() instanceof Sheep sheep)) return;
        if (!Main.getPetManager().isPet(sheep)) return;

        event.setCancelled(true); // Previne abrir inventário/pintar se tiver corante

        if (event.getPlayer().isSneaking()) {
            Main.getPetManager().toggleSitting(event.getPlayer(), sheep);
        } else {
            if (Main.getPetManager().isSitting(sheep)) {
                event.getPlayer().sendMessage("§cA ovelha está descansando. Use Shift+Click para ela levantar.");
            } else {
                sheep.getWorld().spawnParticle(Particle.HEART, sheep.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5);
                sheep.getWorld().playSound(sheep.getLocation(), Sound.ENTITY_SHEEP_AMBIENT, 1, 1.5f);
                event.getPlayer().sendMessage("§d❤ A ovelha gosta de você! ❤");
            }
        }
    }
}