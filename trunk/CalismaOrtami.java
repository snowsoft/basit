import java.util.*;
import org.antlr.runtime.tree.*;

class CalismaOrtami {

    protected CalismaOrtami ustOrtam;
    protected Map degiskenler = new HashMap(); // Map<String, Deger>
    protected FonksiyonOrtami fonksiyonOrtami;
    
    protected CalismaOrtami() {}
    
    public CalismaOrtami(CalismaOrtami ustOrtam) {
        if (ustOrtam == null) {
            throw new IllegalArgumentException(
                "ustOrtam null olamaz. Sanirim bir FonksiyonOrtami nesnesine ihtiyaciniz var.");
        }
        this.ustOrtam = ustOrtam;
        this.fonksiyonOrtami = ustOrtam.fonksiyonOrtami;
    }
    
    public FonksiyonOrtami fonksiyonOrtami() {
        return fonksiyonOrtami;
    }
    
    public final void degiskenTanimla(String degiskenIsmi) {
        if (degiskenler.containsKey(degiskenIsmi))
            throw new RuntimeException("Degisken '" + degiskenIsmi + "' zaten tanimliydi.");
        degiskenler.put(degiskenIsmi, DTanimsiz.TANIMSIZ);
    }
    
    private final CalismaOrtami ortamBul(String degiskenIsmi) {
        CalismaOrtami ortam = this;
        while (ortam != null && ! ortam.degiskenler.containsKey(degiskenIsmi))
            ortam = ortam.ustOrtam;
        
        return ortam;
    }
    
    public final void degiskenAta(String degiskenIsmi, Deger deger) {
        CalismaOrtami ortam = ortamBul(degiskenIsmi);
        
        if (ortam == null)
            throw new RuntimeException("Degisken '" + degiskenIsmi + "' tanimli degil.");
        ortam.degiskenler.put(degiskenIsmi, deger);
    }
    
    public final Deger degiskenBul(String degiskenIsmi) {
        CalismaOrtami ortam = ortamBul(degiskenIsmi);
        
        if (ortam == null)
            throw new RuntimeException("Degisken '" + degiskenIsmi + "' tanimli degil.");
        return (Deger)ortam.degiskenler.get(degiskenIsmi);
    }
}

final class FonksiyonOrtami extends CalismaOrtami {

    private ArrayList parametreler; // ArrayList<Deger>
    private Deger sonuc; // bu calisma ortaminda calisan kodun sonucu
    private Deger calisanFonksiyon = DTanimsiz.TANIMSIZ;
    
    public FonksiyonOrtami(CalismaOrtami ustOrtam, ArrayList parametreler) {
        this.ustOrtam = ustOrtam;
        this.fonksiyonOrtami = this;
        this.parametreler = parametreler;
    }
    
    public FonksiyonOrtami(CalismaOrtami ustOrtam, ArrayList parametreler, DFonksiyon calisanFonksiyon) {
        this(ustOrtam, parametreler);
        if (calisanFonksiyon != null)
            this.calisanFonksiyon = calisanFonksiyon;
    }
    
    public int parametreSayisi() {
        return parametreler.size();
    }
    
    public Deger parametre(int i) {
        return i < parametreler.size() ? (Deger) parametreler.get(i) : 
                                         DTanimsiz.TANIMSIZ;
    }
    
    public boolean sonucVar() {
        return sonuc != null;
    }
    
    public Deger sonuc() {
        return sonuc;
    }
    
    public void sonuc(Deger deger) {
        this.sonuc = deger;
    }
    
    public void parametreleriKopyala(Tree parametreIsimleri) {
        for (int i = 0, n = parametreIsimleri.getChildCount(); i < n; ++i) {
            String isim = parametreIsimleri.getChild(i).getText();
            degiskenTanimla(isim);
            degiskenAta(isim, parametre(i));
        }
    }
}