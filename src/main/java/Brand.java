import java.time.format.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class Brand {
  private int mId;
  private String mDescription;


  public int getId() {
    return mId;
  }

  public String getDescription() {
    return mDescription;
  }


  //CONSTRUCTOR
  public Brand(String description) {
    mDescription = description;
    mIsDone = false;
  }

  @Override
  public boolean equals(Object otherBrand){
    if (!(otherBrand instanceof Brand)) {
      return false;
    } else {
      Brand newBrand = (Brand) otherBrand;
      return this.getDescription().equals(newBrand.getDescription())
        && this.getId() == newBrand.getId();
    }
  }


  public static List<Brand> all() {
    String sql = "SELECT id AS mId, description AS mDescription, FROM brands ";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Brand.class);
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO brands(description) VALUES (:description)";
      mId = (int) con.createQuery(sql, true)
        .addParameter("description", mDescription)

        .executeUpdate()
        .getKey();
    }
  }

  public static Brand find(int number) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT id AS mId, description AS mDescription, FROM brands where id=:id";
      Brand brand = con.createQuery(sql)
        .addParameter("id", number)
        .executeAndFetchFirst(Brand.class);
      return brand;
    }
  }

  public void update(String newDescription) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE brands SET description = :description WHERE id = :id";
      con.createQuery(sql)
        .addParameter("description", newDescription)
        .addParameter("id", mId)
        .executeUpdate();
    }
    mDescription = newDescription;
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String deleteRelationships = "DELETE FROM stores_brands WHERE brand_id = :id";
      con.createQuery(deleteRelationships)
        .addParameter("id", mId)
        .executeUpdate();
    }

    try(Connection con = DB.sql2o.open()) {
    String deleteBrand = "DELETE FROM brands WHERE id = :id;";
    con.createQuery(deleteBrand)
      .addParameter("id", mId)
      .executeUpdate();
    }
  }

  public void addStore(Store store) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO stores_brands(store_id, brand_id) VALUES (:storeid, :brandid)";
      con.createQuery(sql)
        .addParameter("brandid", this.mId)
        .addParameter("storeid", store.getId())
        .executeUpdate();
    }
  }

  public ArrayList<Store> getStores() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT store_id FROM stores_brands WHERE brand_id = :brand_id";
      List<Integer> storeIds = con.createQuery(sql)
        .addParameter("brand_id", this.mId)
        .executeAndFetch(Integer.class);

     ArrayList<Store> associatedStores = new ArrayList<Store>();

     for (Integer storeId : storeIds) {
       String storeQuery = "SELECT id AS mId, name AS mName FROM stores WHERE id = :storeid";
       Store store = con.createQuery(storeQuery)
        .addParameter("storeid", storeId)
        .executeAndFetchFirst(Store.class);
       associatedStores.add(store);
     }
     return associatedStores;
   }
 }


}
