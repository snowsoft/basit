import org.antlr.runtime.tree.*;

abstract class Deger {
    public String tip() { throw new RuntimeException("unimplemented"); }
    public String metin() { throw new RuntimeException("unimplemented"); }
    public Deger topla(Deger deger) { throw new RuntimeException("unimplemented"); }
    public Deger cikar(Deger deger) { throw new RuntimeException("unimplemented"); }
    public Deger carp(Deger deger) { throw new RuntimeException("unimplemented"); }
    public Deger bol(Deger deger) { throw new RuntimeException("unimplemented"); }
    public Deger kalan(Deger deger) { throw new RuntimeException("unimplemented"); }
    public Deger eksi() { throw new RuntimeException("unimplemented"); }
    public DMantiksal esittir(Deger deger) { throw new RuntimeException("unimplemented"); }
    public DMantiksal kucuktur(Deger deger) { throw new RuntimeException("unimplemented"); }
    public DMantiksal buyuktur(Deger deger) { throw new RuntimeException("unimplemented"); }
    public DMantiksal mantiksalDeger() { return mantiksal(true); }
    
    static DMantiksal mantiksal(boolean b) {
        return b ? DMantiksal.EVET : DMantiksal.HAYIR;
    }
}

class DMantiksal extends Deger {
    private boolean deger;
    
    private DMantiksal(boolean deger) {
        this.deger = deger;
    }
    public boolean deger() {
        return deger;
    }
    
    public static final DMantiksal EVET  = new DMantiksal(true);
    public static final DMantiksal HAYIR = new DMantiksal(false);

    public String tip() { return "Mantiksal"; }
    public String metin() { return deger ? "evet" : "hayir"; }
   
    public DMantiksal esittir(Deger deger) {
        if (deger instanceof DMantiksal)
            return mantiksal(this.deger == ((DMantiksal)deger).deger);
        return mantiksal(false);
    }
    public DMantiksal mantiksalDeger() { return this; }
    
    public static DMantiksal degil(Deger deger) {
        return mantiksal(! deger.mantiksalDeger().deger);
    }
    public static DMantiksal ve(Deger sol, Deger sag) {
        return mantiksal(sol.mantiksalDeger().deger && sag.mantiksalDeger().deger);
    }
    public static DMantiksal veya(Deger sol, Deger sag) {
        return mantiksal(sol.mantiksalDeger().deger || sag.mantiksalDeger().deger);
    }
}

class DTanimsiz extends Deger {
    public static final DTanimsiz TANIMSIZ = new DTanimsiz();
    
    private DTanimsiz() {}
    
    public String tip() { return "Tanimsiz"; }
    public String metin() { return "tanimsiz"; }
    public DMantiksal mantiksalDeger() { return mantiksal(false); }
    public DMantiksal esittir(Deger deger) { return mantiksal(deger==TANIMSIZ); }
}

class DTamSayi extends Deger {
    private int sayi;
    
    public DTamSayi(int sayi) {
        this.sayi = sayi;
    }
    public int sayi() {
        return sayi;
    }
    
    public String tip() { return "DTamSayi"; }
    
    public String metin() { return "" + sayi; }
    
    public Deger topla(Deger deger) {
        if (deger instanceof DTamSayi)
            return new DTamSayi(sayi + ((DTamSayi)deger).sayi);
        throw new RuntimeException("gecersiz islem");
    }
    public Deger cikar(Deger deger) {
        if (deger instanceof DTamSayi)
            return new DTamSayi(sayi - ((DTamSayi)deger).sayi);
        throw new RuntimeException("gecersiz islem");
    }
    
    public Deger carp(Deger deger) {
        if (deger instanceof DTamSayi)
            return new DTamSayi(sayi * ((DTamSayi)deger).sayi);
        throw new RuntimeException("gecersiz islem");
    }
    
    public Deger bol(Deger deger) {
        if (deger instanceof DTamSayi)
            return new DTamSayi(sayi / ((DTamSayi)deger).sayi);
        throw new RuntimeException("gecersiz islem");
    }
    
    public Deger kalan(Deger deger) {
        if (deger instanceof DTamSayi)
            return new DTamSayi(sayi % ((DTamSayi)deger).sayi);
        throw new RuntimeException("gecersiz islem");
    }
    
    public Deger eksi() {
        return new DTamSayi(-sayi);
    }
    
    public DMantiksal esittir(Deger deger) {
        if (deger instanceof DTamSayi)
            return mantiksal(sayi == ((DTamSayi)deger).sayi);
        return mantiksal(false);
    }
    public DMantiksal kucuktur(Deger deger) {
        if (deger instanceof DTamSayi)
            return mantiksal(sayi < ((DTamSayi)deger).sayi);
        throw new RuntimeException("gecersiz islem");
    }
    public DMantiksal buyuktur(Deger deger) {
        if (deger instanceof DTamSayi)
            return mantiksal(sayi > ((DTamSayi)deger).sayi);
        throw new RuntimeException("gecersiz islem");
    }
    public DMantiksal mantiksalDeger() { return mantiksal(sayi != 0); }
}

class DFonksiyon extends Deger {
    public CalismaOrtami ortam;
    public Tree parametreIsimleri;
    public Tree komutlar;
    
    public DFonksiyon(CalismaOrtami ortam, Tree parametreIsimleri, Tree komutlar) {
        this.ortam = ortam;
        this.parametreIsimleri = parametreIsimleri;
        this.komutlar = komutlar;
    }
    
    public String tip() { return "Fonksiyon"; }
    public String metin() { return "[fonksiyon]"; }
    public DMantiksal esittir(Deger deger) { return mantiksal(deger == this); }
}
