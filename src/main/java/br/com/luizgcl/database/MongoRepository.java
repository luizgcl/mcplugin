package br.com.luizgcl.database;

import br.com.luizgcl.Main;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.lang.reflect.ParameterizedType;
import java.util.UUID;
import org.bson.Document;

public abstract class MongoRepository<T extends MongoEntity> implements RepositoryContract<T> {

  MongoDatabase database;
  MongoCollection<Document> collection;

  public MongoRepository(String collectionName) {
    this.database = MongoConnection.getMongoDatabase();
    this.collection = database.getCollection(collectionName);

    if (this.database == null) {
      throw new RuntimeException("Database not connected");
    }
  }

  @Override
  public T findOne(UUID id) {
    Document document = collection.find(Filters.eq("id", id.toString())).first();

    if (document == null) {
      return null;
    }

    return documentToEntity(document);
  }

  @Override
  public T findByField(String field, Object value) {
    Document document = collection.find(Filters.eq(field, value)).first();

    if (document == null) {
      return null;
    }

    return documentToEntity(document);
  }

  @Override
  public void create(T entity) {
    try {
      Document document = collection.find(Filters.eq("id", entity.getId().toString())).first();
      if (document == null) {
        collection.insertOne(this.entityToDocument(entity));
      }
    } catch (Exception e) {
      this.logException(e);
    }
  }

  @Override
  public void update(T entity) {
    try {
      collection.findOneAndUpdate(Filters.eq("id", entity.getId().toString()),
          new Document("$set", this.entityToDocument(entity)));
    } catch (Exception e) {
      this.logException(e);
    }
  }

  @Override
  public void destroy(UUID id) {
    try {
      collection.deleteOne(Filters.eq("id", id.toString()));
    } catch (Exception e) {
      this.logException(e);
    }
  }

  private Document entityToDocument(T entity) {
    return Document.parse(new Gson().toJson(entity));
  }


  private T documentToEntity(Document document) {
    T entity = new Gson().fromJson(
        document.toJson(),
        ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]
    );
    

    return entity;
  }

  private void logException(Exception e) {
    Main.getInstance().getLogger().severe(e.getMessage());
  }
}
