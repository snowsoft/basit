import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.*;

class Calistirici {

    public Deger calistir(Tree tree) {
        return calistir(tree, new FonksiyonOrtami(null, new ArrayList()));
    }
    
    public Deger calistir(Tree tree, CalismaOrtami ortam) {
        switch(tree.getType()) {
            case basitLexer.T_CALISMA_BIRIMI:
                return komutlariCalistir(tree, ortam);
            case basitLexer.T_SONUC:
                return sonucCalistir(tree, ortam);
            case basitLexer.T_YENI:
                return yeniDegisken(tree, ortam);
            case basitLexer.T_ATAMA:
                return degiskenAta(tree, ortam);
            case basitLexer.T_KOSUL:
                return kosulCalistir(tree, ortam);
            case basitLexer.T_IKEN:
                return ikenCalistir(tree, ortam);
            case basitLexer.T_DEFA:
                return defaCalistir(tree, ortam);
            case basitLexer.SOL_PARANTEZ:
                return fonksiyonCalistir(tree, ortam);
            case basitLexer.T_FONKSIYON_TANIMLAMA:
                return fonksiyonTanimla(tree, ortam);
            case basitLexer.T_FONKSIYON_IFADESI:
                return fonksiyonIfadesi(tree, ortam);
            
            // 'literal' degerler
            case basitLexer.T_TAMSAYI:
                return tamsayiCalistir(tree, ortam);
            case basitLexer.T_ISIM:
                return degiskenBul(tree, ortam);
            case basitLexer.EVET:
                return DMantiksal.EVET;
            case basitLexer.HAYIR:
                return DMantiksal.HAYIR;
            
            // islecler
            case basitLexer.ARTI:
                return topla(tree, ortam);
            case basitLexer.EKSI:
                return cikar(tree, ortam);
            case basitLexer.CARPI:
                return carp(tree, ortam);
            case basitLexer.BOLU:
                return bol(tree, ortam);
            case basitLexer.KALAN:
                return kalan(tree, ortam);
            case basitLexer.ESIT:
                return esittir(tree, ortam);
            case basitLexer.FARKLI:
                return farklidir(tree, ortam);
            case basitLexer.KUCUK:
                return kucuktur(tree, ortam);
            case basitLexer.BUYUK:
                return buyuktur(tree, ortam);
            case basitLexer.KUCUKESIT:
                return kucukVeyaEsittir(tree, ortam);
            case basitLexer.BUYUKESIT:
                return buyukVeyaEsittir(tree, ortam);
            case basitLexer.VE:
                return ve(tree, ortam);
            case basitLexer.VEYA:
                return veya(tree, ortam);
            case basitLexer.T_DEGIL:
                return degil(tree, ortam);
        }
        throw new RuntimeException("tanimsiz kod agaci: " + tree);
    }
    
    private Deger komutlariCalistir(Tree tree, CalismaOrtami ortam) {
        FonksiyonOrtami fonksiyonOrtami = ortam.fonksiyonOrtami();
        for (int i = 0, n = tree.getChildCount(); i < n && ! fonksiyonOrtami.sonucVar(); ++i)
            calistir(tree.getChild(i), ortam);
        return fonksiyonOrtami.sonucVar() ? fonksiyonOrtami.sonuc() : DTanimsiz.TANIMSIZ;
    }
    
    private Deger sonucCalistir(Tree tree, CalismaOrtami ortam) {
        FonksiyonOrtami fonksiyonOrtami = ortam.fonksiyonOrtami();
        fonksiyonOrtami.sonuc(calistir(tree.getChild(0), ortam));
        return DTanimsiz.TANIMSIZ;
    }
    
    private Deger kosulCalistir(Tree tree, CalismaOrtami ortam) {
        Tree eger = tree.getChild(0);
        if (! egerCalistir(eger, ortam)) {
            Tree yoksa = tree.getChild(1);
            yoksaCalistir(yoksa, ortam);
        }
        return DTanimsiz.TANIMSIZ;
    }
    
    private boolean egerCalistir(Tree tree, CalismaOrtami ortam) {
        int n = tree.getChildCount();
        for (int i = 0; i < n; i += 2) {
            Deger kosul = calistir(tree.getChild(i), ortam);
            if (kosul.mantiksalDeger().deger()) {
                Tree komutlar = tree.getChild(i+1);
                if (komutlar.getChildCount() > 0) {
                    komutlariCalistir(komutlar, new CalismaOrtami(ortam));
                }
                return true;
            }
        }
        return false;
    }
    private void yoksaCalistir(Tree komutlar, CalismaOrtami ortam) {
        if (komutlar.getChildCount() > 0) {
            komutlariCalistir(komutlar, new CalismaOrtami(ortam));
        }
    }
    
    private Deger ikenCalistir(Tree tree, CalismaOrtami ortam) {
        FonksiyonOrtami fonksiyonOrtami = ortam.fonksiyonOrtami();
        Tree kosul = tree.getChild(0);
        Tree komutlar = tree.getChild(1);
        
        while (! fonksiyonOrtami.sonucVar() && calistir(kosul, ortam).mantiksalDeger().deger()) {
            if (komutlar.getChildCount() > 0) {
                komutlariCalistir(komutlar, new CalismaOrtami(ortam));
            }
        }
        return DTanimsiz.TANIMSIZ;
    }
    
    private Deger defaCalistir(Tree tree, CalismaOrtami ortam) {
        FonksiyonOrtami fonksiyonOrtami = ortam.fonksiyonOrtami();
        Deger defaDeger = calistir(tree.getChild(0), ortam);
        if (! (defaDeger instanceof DTamSayi))
            throw new RuntimeException("'defa' dongusu sadece TamSayi tipiyle calisir.");
        int defa = ((DTamSayi) defaDeger).sayi();
        Tree komutlar = tree.getChild(1);
        
        for (int i = 0; i < defa && ! fonksiyonOrtami.sonucVar(); ++i) {
            if (komutlar.getChildCount() > 0) {
                komutlariCalistir(komutlar, new CalismaOrtami(ortam));
            }
        }
        return DTanimsiz.TANIMSIZ;
    }

    private Deger fonksiyonCalistir(Tree tree, CalismaOrtami ortam) {
        ArrayList parametreler = new ArrayList();
        for (int i = 1, n = tree.getChildCount(); i < n; ++i) {
            parametreler.add(calistir(tree.getChild(i), ortam));
        }
        
        Tree fonksiyonIfadesiTree = tree.getChild(0);
        if (fonksiyonIfadesiTree.getType() == basitLexer.T_ISIM) {
            String fonksiyonIsmi = fonksiyonIfadesiTree.getChild(0).getText();
            if (fonksiyonIsmi.startsWith("__")) { // sistem fonksiyonu
                return Sistem.fonksiyonCalistir(fonksiyonIsmi, parametreler, ortam);
            }
        }
        
        Deger fonksiyonDeger = calistir(fonksiyonIfadesiTree, ortam);
        if (! (fonksiyonDeger instanceof DFonksiyon))
            throw new RuntimeException("Sadece 'Fonksiyon' turundeki nesneler calistirilabilir (" + fonksiyonDeger.tip() + ")");
        DFonksiyon fonksiyon = (DFonksiyon) fonksiyonDeger;
        FonksiyonOrtami yeniOrtam = new FonksiyonOrtami(fonksiyon.ortam, parametreler, fonksiyon);
        yeniOrtam.parametreleriKopyala(fonksiyon.parametreIsimleri);
        return komutlariCalistir(fonksiyon.komutlar, yeniOrtam);
    }
    
    private Deger fonksiyonTanimla(Tree tree, CalismaOrtami ortam) {
        String fonksiyonIsmi = tree.getChild(0).getText();
        Tree parametreIsimleri = tree.getChild(1);
        Tree komutlar = tree.getChild(2);
        
        DFonksiyon fonksiyon = new DFonksiyon(ortam, parametreIsimleri, komutlar);
        ortam.degiskenTanimla(fonksiyonIsmi);
        ortam.degiskenAta(fonksiyonIsmi, fonksiyon);
        
        return DTanimsiz.TANIMSIZ;
    }
    
    private Deger fonksiyonIfadesi(Tree tree, CalismaOrtami ortam) {
        Tree parametreIsimleri = tree.getChild(0);
        Tree komutlar = tree.getChild(1);
        
        return new DFonksiyon(ortam, parametreIsimleri, komutlar);
    }
    
    private Deger tamsayiCalistir(Tree tree, CalismaOrtami ortam) {
        return new DTamSayi(Integer.parseInt(tree.getChild(0).getText()));
    }
    
    private Deger topla(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        Deger sag = calistir(tree.getChild(1), ortam);
        return sol.topla(sag);
    }
    
    private Deger cikar(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        Deger sag = calistir(tree.getChild(1), ortam);
        return sol.cikar(sag);
    }
    
    private Deger carp(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        Deger sag = calistir(tree.getChild(1), ortam);
        return sol.carp(sag);
    }
    
    private Deger bol(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        Deger sag = calistir(tree.getChild(1), ortam);
        return sol.bol(sag);
    }
    
    private Deger kalan(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        Deger sag = calistir(tree.getChild(1), ortam);
        return sol.kalan(sag);
    }
    
    private DMantiksal esittir(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        Deger sag = calistir(tree.getChild(1), ortam);
        return sol.esittir(sag);
    }
    
    private DMantiksal farklidir(Tree tree, CalismaOrtami ortam) {
        return DMantiksal.degil(esittir(tree, ortam));
    }
    
    private DMantiksal kucuktur(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        Deger sag = calistir(tree.getChild(1), ortam);
        return sol.kucuktur(sag);
    }
    
    private DMantiksal buyuktur(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        Deger sag = calistir(tree.getChild(1), ortam);
        return sol.buyuktur(sag);
    }
    
    private DMantiksal kucukVeyaEsittir(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        Deger sag = calistir(tree.getChild(1), ortam);
        return DMantiksal.veya(sol.kucuktur(sag), sol.esittir(sag));
    }
    
    private DMantiksal buyukVeyaEsittir(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        Deger sag = calistir(tree.getChild(1), ortam);
        return DMantiksal.veya(sol.buyuktur(sag), sol.esittir(sag));
    }
    
    private DMantiksal ve(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        if (! sol.mantiksalDeger().deger())
            return DMantiksal.HAYIR;
        Deger sag = calistir(tree.getChild(1), ortam);
        return sag.mantiksalDeger();
    }
    
    private DMantiksal veya(Tree tree, CalismaOrtami ortam) {
        Deger sol = calistir(tree.getChild(0), ortam);
        if (sol.mantiksalDeger().deger())
            return DMantiksal.EVET;
        Deger sag = calistir(tree.getChild(1), ortam);
        return sag.mantiksalDeger();
    }
    
    private DMantiksal degil(Tree tree, CalismaOrtami ortam) {
        return DMantiksal.degil(calistir(tree.getChild(0), ortam));
    }
    
    private Deger degiskenBul(Tree tree, CalismaOrtami ortam) {
        return ortam.degiskenBul(tree.getChild(0).getText());
    }
    
    private Deger yeniDegisken(Tree tree, CalismaOrtami ortam) {
        ortam.degiskenTanimla(tree.getChild(0).getText());
        return DTanimsiz.TANIMSIZ;
    }
    
    private Deger degiskenAta(Tree tree, CalismaOrtami ortam) {
        Deger deger = calistir(tree.getChild(1), ortam);
        ortam.degiskenAta(tree.getChild(0).getText(), deger);
        return DTanimsiz.TANIMSIZ;
    }
}