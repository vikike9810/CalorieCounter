package hu.aut.bme.kaloriaszamlalo.Model;

import com.orm.SugarRecord;

public class Item extends SugarRecord {

    public String name="";
    public Category category=Category.Reggeli;
    public int kcal=0;
    public int mennyiseg=0;
    public String date="";



    public enum Category {
        Reggeli, Ebéd, Vacsora;

        public static Category getByOrdinal(int ordinal) {
            Category ret = null;
            for (Category cat : Category.values()) {
                if (cat.ordinal() == ordinal) {
                    ret = cat;
                    break;
                }
            }
            return ret;
        }
    }
}