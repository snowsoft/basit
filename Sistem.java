import java.util.*;

interface SistemFonksiyonu {
    public Deger calistir(ArrayList parametreler, CalismaOrtami ortam);
}

public class Sistem {
    
    private static final Map sistemFonksiyonlari = new HashMap();
    
    static {
        sistemFonksiyonlari.put("__yaz", new SistemFonksiyonu() {
            public Deger calistir(ArrayList parametreler, CalismaOrtami ortam) {
                for (int i = 0, n = parametreler.size(); i < n; ++i) {
                    if (i != 0)
                        System.out.print(" ");
                    System.out.print(((Deger)parametreler.get(i)).metin());
                }
                return DTanimsiz.TANIMSIZ;
            }
        });
        
        sistemFonksiyonlari.put("__yazss", new SistemFonksiyonu() {
            public Deger calistir(ArrayList parametreler, CalismaOrtami ortam) {
                for (int i = 0, n = parametreler.size(); i < n; ++i) {
                    if (i != 0)
                        System.out.print(" ");
                    System.out.print(((Deger)parametreler.get(i)).metin());
                }
                System.out.println();
                return DTanimsiz.TANIMSIZ;
            }
        });
    }

    public static Deger fonksiyonCalistir(String isim, ArrayList parametreler, CalismaOrtami ortam) {
        SistemFonksiyonu fonksiyon = (SistemFonksiyonu) sistemFonksiyonlari.get(isim);
        if (fonksiyon != null) {
            return fonksiyon.calistir(parametreler, ortam);
        }
        
        System.out.println("Sistem fonksiyonu '" + isim + "' tanimli degil.");
        return DTanimsiz.TANIMSIZ;
    }
}