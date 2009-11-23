import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.*;

public class Basla {

    public static final String PROMPT = "> ";

    static void printTree(Tree t, int n, String prefix) {
        String ind = prefix;
        for (int i = 0; i < n; ++i)
            ind += "  ";
        
        System.out.println(ind + t.getText());
        for (int i = 0; i < t.getChildCount(); ++i) {
            Tree ch = t.getChild(i);
            printTree(ch, n+1, prefix);
        }
    }
    
    public static Deger calistir(CharStream stream, Calistirici calistirici, CalismaOrtami ortam, boolean agacYaz) {
        basitLexer lex = new basitLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        basitParser parser = new basitParser(tokens);
        basitParser.calisma_birimi_return calismaBirimi = null;
        try {
            calismaBirimi = parser.calisma_birimi();
            Tree komutlar = (Tree) calismaBirimi.getTree();
            
            if (agacYaz) {
                System.out.println("===================");
                printTree(komutlar, 0, "#   ");
                System.out.println("===================");
            }
            
            return calistirici.calistir(komutlar, ortam);
        } catch (RecognitionException e) {
            System.out.println(e);
            return DTanimsiz.TANIMSIZ;
        }
    }

    public static void main(String args[]) throws Exception {
        Calistirici calistirici = new Calistirici();
        if (args.length > 0) {
            ArrayList parametreler = new ArrayList();
            for (int i = 1; i < args.length; ++i) {
                //parametreler.add(new DMetin(args[i]));
            }
            FonksiyonOrtami ortam = new FonksiyonOrtami(null, parametreler);
            Deger deger = null;
            if (args[0].equals("-")) {
                deger = calistir(new ANTLRInputStream(System.in), calistirici, ortam, true);
            } else {
                deger = calistir(new ANTLRFileStream(args[0]), calistirici, ortam, true);
            }
            System.out.println(deger.metin());
        } else {
            FonksiyonOrtami ortam = new FonksiyonOrtami(null, new ArrayList());
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String satir = null;
            System.out.print(PROMPT);
            while ((satir = in.readLine()) != null) {
                satir = satir.trim();
                if (satir.trim().length() > 0) {
                    if (satir.charAt(0) == '?') { // ifade icerigine bakmak icin
                        satir = "sonuc " + satir.substring(1) + ";";
                    }
                    try {
                        Deger deger = calistir(new ANTLRStringStream(satir), calistirici, ortam, false);
                        ortam.sonuc(null); // sonuc ifadesinden sonra calismaya izin vermek icin
                        if (deger != DTanimsiz.TANIMSIZ) {
                            System.out.println(deger.metin());
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    System.out.print(PROMPT);
                }
            }
        }
        //basitLexer lex = new basitLexer(new ANTLRFileStream("E:\\BASIT\\basit.c\\output\\__Test___input.txt", "UTF8"));
        /*basitLexer lex = new basitLexer(new ANTLRInputStream(System.in));
        CommonTokenStream tokens = new CommonTokenStream(lex);
        basitParser g = new basitParser(tokens);
        basitParser.calisma_birimi_return p = null;
        try {
            p = g.calisma_birimi();
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
        Tree t = (Tree) p.getTree();
        printTree(t, 0);
        System.out.println("===================");
        Calistirici c = new Calistirici();
        System.out.println(c.calistir(t).metin());*/
    }
}
