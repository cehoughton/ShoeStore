import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.sql2o.*;

public class Store {
  private int mId;
  private String mName;

  public int getId() {
    return mId;
  }

  public String getName() {
    return mName;
  }

  public Store(String name) {
    mName = name;
  }

  public static List<Store> all() {
    String sql = "SELECT id AS mId, name AS mName FROM stores";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Store.class);
    }
  }

  @Override
  public boolean equals(Object otherStore){
    if (!(otherStore instanceof Store)) {
      return false;
    } else {
      Store newStore = (Store) otherStore;
      return this.getName().equals(newStore.getName());
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO stores(name) VALUES (:name)";
      this.mId = (int) con.createQuery(sql, true)
        .addParameter("name", this.mName)
        .executeUpdate()
        .getKey();
    }
  }

  public static Store find(int searchId) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT id as mId, name as mName FROM stores where id=:id";
      Store Store = con.createQuery(sql)
        .addParameter("id", searchId)
        .executeAndFetchFirst(Store.class);
      return Store;
    }
  }

  public void update(String newName) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE stores SET name = :name WHERE id = :id";
      con.createQuery(sql)
        .addParameter("name", newName)
        .addParameter("id", mId)
        .executeUpdate();
    }
    mName = newName;
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String deleteRelationships = "DELETE FROM stores_brands WHERE store_id = :id";
      con.createQuery(deleteRelationships)
        .addParameter("id", mId)
        .executeUpdate();
    }

    try(Connection con = DB.sql2o.open()) {
    String deleteStore = "DELETE FROM stores WHERE id = :id;";
    con.createQuery(deleteStore)
      .addParameter("id", mId)
      .executeUpdate();
    }
  }

  public void addBrand(Brand brand) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO stores_brands(store_id, brand_id) VALUES (:storeid, :brandid)";
      con.createQuery(sql)
        .addParameter("storeid", this.mId)
        .addParameter("brandid", brand.getId())
        .executeUpdate();
    }
  }

  public List<Brand> getBrands() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT brand_id AS mId, description as mDescription,  FROM brands INNER JOIN stores_brands ON  brands.id = stores_brands.brand_id WHERE stores_brands.store_id = :store_id ORDER BY brands.is_done, brands.due_date";
      return con.createQuery(sql)
        .addParameter("store_id", this.mId)
        .executeAndFetch(Brand.class);
    }
  }
}
