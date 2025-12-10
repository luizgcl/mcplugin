package br.com.luizgcl.manager;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class PetManager {

    private final JavaPlugin plugin;
    private final NamespacedKey petKey;
    private final NamespacedKey sittingKey;

    private final NamespacedKey hasPetKey;   // O jogador tinha pet quando saiu?
    private final NamespacedKey petColorKey; // Qual a cor do pet?
    private final NamespacedKey petSitKey;   // Ele estava sentado?
    
    // Guarda o ID do jogador e a Ovelha dele
    private final Map<UUID, Sheep> activePets = new HashMap<>();
    // Guarda a tarefa de "seguir" de cada ovelha para poder cancelar depois
    private final Map<UUID, BukkitTask> followTasks = new HashMap<>();

    public PetManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.petKey = new NamespacedKey(plugin, "is_custom_pet");
        this.sittingKey = new NamespacedKey(plugin, "pet_is_sitting");
        this.hasPetKey = new NamespacedKey(plugin, "player_has_pet");
        this.petColorKey = new NamespacedKey(plugin, "player_pet_color");
        this.petSitKey = new NamespacedKey(plugin, "player_pet_sitting");
    }

    public void spawnPet(Player player, DyeColor color, boolean isSitting) {
        // 1. Se já tiver pet, remove o antigo
        removePet(player, false);

        // 2. Spawna a ovelha
        Location spawnLoc = player.getLocation();
        Sheep sheep = player.getWorld().spawn(spawnLoc, Sheep.class, s -> {
            s.setCustomName("§dOvelha de " + player.getName());
            s.setCustomNameVisible(true);
            s.setAgeLock(true); // Não cresce nem morre de velhice
            s.setCollidable(false); // Não empurra o player
            
            // Marca a ovelha com nossos dados persistentes para reconhecê-la nos eventos
            s.getPersistentDataContainer().set(petKey, PersistentDataType.STRING, player.getUniqueId().toString());
            s.getPersistentDataContainer().set(sittingKey, PersistentDataType.BYTE, (byte) 0);
        });

        // 3. Registra
        activePets.put(player.getUniqueId(), sheep);
        startFollowTask(player, sheep);

        if (isSitting) {
            sheep.getPathfinder().stopPathfinding();
        }
    }

    public void spawnPet(Player player) {
        spawnPet(player, DyeColor.WHITE, false);
    }

    public void restorePet(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        // Verifica se ele tinha um pet salvo
        if (pdc.has(hasPetKey, PersistentDataType.BYTE)) {

            for (org.bukkit.entity.Entity entity : player.getNearbyEntities(50, 50, 50)) {
                if (entity instanceof Sheep && isPet(entity)) {
                    // Verifica se o pet pertence a esse jogador
                    String ownerUUID = entity.getPersistentDataContainer().get(petKey, PersistentDataType.STRING);
                    if (ownerUUID != null && ownerUUID.equals(player.getUniqueId().toString())) {
                        entity.remove(); // Remove a velha antes de criar a nova
                    }
                }
            }
            
            // Recupera a cor
            String colorName = pdc.getOrDefault(petColorKey, PersistentDataType.STRING, "WHITE");
            DyeColor color = DyeColor.valueOf(colorName);
            
            // Recupera se estava sentado
            byte sittingByte = pdc.getOrDefault(petSitKey, PersistentDataType.BYTE, (byte) 0);
            boolean isSitting = sittingByte == 1;

            // Respawn!
            spawnPet(player, color, isSitting);
            player.sendMessage("§d❤ Sua ovelhinha sentiu sua falta e voltou!");
        }
    }

    public void removePet(Player player, boolean save) {
        UUID uuid = player.getUniqueId();
        
        if (activePets.containsKey(uuid)) {
            Sheep sheep = activePets.get(uuid);
            
            // --- A MÁGICA DE SALVAR ---
            if (save && sheep != null && !sheep.isDead()) {
                PersistentDataContainer pdc = player.getPersistentDataContainer();
                
                // 1. Marca que ele tem pet
                pdc.set(hasPetKey, PersistentDataType.BYTE, (byte) 1);
                // 2. Salva a cor (como String)
                pdc.set(petColorKey, PersistentDataType.STRING, sheep.getColor().name());
                // 3. Salva se estava sentada
                boolean sitting = isSitting(sheep);
                pdc.set(petSitKey, PersistentDataType.BYTE, (byte) (sitting ? 1 : 0));
            }
            // --------------------------

            if (sheep != null) sheep.remove();
            activePets.remove(uuid);
        } else if (save) {
            // Se ele não tem pet ativo na hora de sair, removemos os dados do player
            // para que ele não nasça com pet da próxima vez
            player.getPersistentDataContainer().remove(hasPetKey);
        }

        if (followTasks.containsKey(uuid)) {
            followTasks.get(uuid).cancel();
            followTasks.remove(uuid);
        }
    }

    public void removeAllPets() {
        // Precisamos criar uma cópia da lista de players para evitar erro de modificação concorrente
        for (UUID uuid : new HashSet<>(activePets.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                // Remove a ovelha mas SALVA (true) os dados no player
                // Assim, quando o server voltar, ele sabe que tem que restaurar
                removePet(player, true);
            }
        }
    }
    
    public void toggleSitting(Player player, Sheep sheep) {
        boolean isSitting = isSitting(sheep);
        
        if (isSitting) {
            // LEVANTAR (Voltar a seguir)
            sheep.getPersistentDataContainer().set(sittingKey, PersistentDataType.BYTE, (byte) 0);
            player.sendMessage("§aSua ovelha está te seguindo novamente!");
            player.playSound(sheep.getLocation(), Sound.ENTITY_SHEEP_AMBIENT, 1, 1.2f);
            
            // Efeito visual (Notas musicais)
            sheep.getWorld().spawnParticle(Particle.NOTE, sheep.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5);
            
        } else {
            // SENTAR (Ficar parada)
            sheep.getPersistentDataContainer().set(sittingKey, PersistentDataType.BYTE, (byte) 1);
            
            // Para o movimento atual imediatamente
            sheep.getPathfinder().stopPathfinding();
            
            player.sendMessage("§eSua ovelha vai esperar aqui.");
            player.playSound(sheep.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.5f);
            
            // Efeito visual (Fumaça no pé)
            sheep.getWorld().spawnParticle(Particle.CLOUD, sheep.getLocation(), 10, 0.2, 0.1, 0.2, 0.05);
        }
    }

    public boolean isSitting(Sheep sheep) {
        PersistentDataContainer pdc = sheep.getPersistentDataContainer();
        if (!pdc.has(sittingKey, PersistentDataType.BYTE)) return false;
        return pdc.get(sittingKey, PersistentDataType.BYTE) == 1;
    }

    private void startFollowTask(Player player, Sheep sheep) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (sheep.isDead() || !player.isOnline()) {
                removePet(player, false);
                return;
            }

            // --- AQUI ESTÁ A MUDANÇA PRINCIPAL ---
            // Se estiver sentada, não faz nada de movimento
            if (isSitting(sheep)) {
                // Opcional: Forçar ela a olhar pro player mesmo parada
                /* Location lookAt = player.getLocation();
                lookAt.setPitch(sheep.getLocation().getPitch());
                Vector dir = lookAt.toVector().subtract(sheep.getLocation().toVector());
                sheep.getLocation().setDirection(dir);
                */
                return; 
            }
            // -------------------------------------

            double distance = sheep.getLocation().distance(player.getLocation());

            if (distance > 15) {
                sheep.teleport(player.getLocation());
            } else if (distance > 3) {
                sheep.getPathfinder().moveTo(player.getLocation());
            } 
        }, 0L, 20L);

        followTasks.put(player.getUniqueId(), task);
    }

    // Verifica se a entidade é um dos nossos pets
    public boolean isPet(org.bukkit.entity.Entity entity) {
        return entity.getPersistentDataContainer().has(petKey, PersistentDataType.STRING);
    }
}