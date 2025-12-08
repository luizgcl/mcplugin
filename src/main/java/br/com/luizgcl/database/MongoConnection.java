package br.com.luizgcl.database;

import br.com.luizgcl.BasePlugin;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

public class MongoConnection {

  private static MongoClient mongoClient;

  @Getter
  private static MongoDatabase mongoDatabase;

  private BasePlugin plugin;

  public MongoConnection(BasePlugin plugin) {
    this.plugin = plugin;
  }

  public void connect() {
    String uri = System.getenv("MONGO_URI");
    String dbName = System.getenv("MONGO_DB");

    mongoClient = MongoClients.create(uri);
    mongoDatabase = mongoClient.getDatabase(dbName);

    plugin.getLogger().info("Connected to MongoDB");
  }

  public void disconnect() {
    if (mongoClient != null) {
      mongoClient.close();
      plugin.getLogger().info("MongoDB connection closed");
    }
  }
}
