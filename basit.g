grammar basit;

options {
    output = AST;
}

// Burada lexing islemi icin kullanilmayan, sadece kod agacinda
// isaret icin kullanilacak token'lar var.
tokens {
	T_EKSI;
	T_TAMSAYI; // (ing: integer)
	T_REELSAYI; // (ing: real)
	T_METIN; // (ing: string)
	T_ISIM; // (ing: identifier)
	T_DEGIL; // (ing: not)
	T_FONKSIYON_CAGIRMA; // (ing: function call)
	T_PARAMETRELER;
	T_YENI; // (ing: variable declaration)
	T_ATAMA; // (ing: assignment)
	T_SONUC; // (ing: return)
	T_IKEN; // (ing: while)
	T_DEFA; // belirli sayida tekrarlama (ing: times)
	T_KOMUTLAR; // (komut = ing. statement)
	T_KOSUL;
	T_EGER; // eger ... yok ... 
	T_YOKSA; // yoksa
	T_FONKSIYON_TANIMLAMA;
	T_FONKSIYON_IFADESI;
	T_CALISMA_BIRIMI; // (ing: execution unit, compile unit)
}

ACIKLAMA
	:	'//' ~('\n' | '\r')* '\r'? '\n' { $channel=HIDDEN; }
	|	'/*' ( options {greedy=false;} : . )* '*/' { $channel=HIDDEN; }
	;

BOSLUK
	:	(' ' | '\t' | '\r' | '\n') { $channel=HIDDEN; }
	;

YENI
	:	'yeni'
	;

FONKSIYON
	:	'fonksiyon'
	;

EGER
	:	'eger'
	;

ISE
	:	'ise'
	;

YOK
	:	'yok'
	;

YOKSA
	:	'yoksa'
	;

SONUC
	:	'sonuc'
	;

VE	
	:	've'
	;

VEYA
	:	'veya'
	;

IKEN
	:	'iken'
	;

DEFA
	:	'defa'
	;

EVET
	:	'evet'
	;

HAYIR
	:	'hayir'
	;

ARTI	:	'+'
	;

EKSI	:	'-'
	;

CARPI	:	'*'
	;

BOLU	:	'/'
	;

KALAN	:	'%'
	;

KUCUK	:	'<'
	;

BUYUK	:	'>'
	;

ESIT	:	'=='
	;

FARKLI	:	'!='
	;

KUCUKESIT
	:	'<='
	;

BUYUKESIT
	:	'>='
	;

SOL_PARANTEZ
    :   '('
    ;

ISIM
	:	('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' |'_')*
	;

TAMSAYI
	:	'0'..'9'+
	;

REELSAYI
	:	('0'..'9')+ '.' ('0'..'9')*
	|	'.' ('0'..'9')+
	;

METIN
	:	'"' HARF* '"'
	;

fragment
HARF
	:	ESCAPE
	|	~('\\' | '\"')
	;

fragment
ESCAPE 
	:	'\\' ('b' | 't' | 'n' | 'f' | 'r' | '\"' | '\'' | '\\')
	;

mantiksal
	:	karsilastirma ( (VE | VEYA)^ karsilastirma )*
	|	'!' mantiksal -> ^(T_DEGIL mantiksal)
	;

karsilastirma
	:	toplama ( ('<=' | '<' | '==' | '!=' | '>=' | '>')^ toplama )?
	;

toplama
	:	carpma ( ('+' | '-')^ carpma )*
	;

carpma 
	:	tekil ( ('*' | '/' | '%')^ tekil )*
	;

tekil
	:	fonCagir
	|	'-' fonCagir -> ^(T_EKSI fonCagir)
	;

fonCagir
	:	eksisizTekil
			(	/* bosluk */	//-> eksisizTekil
			|	( '('^ ifadeler ')'! )+ //-> ^(T_FONKSIYON_CAGIRMA eksisizTekil ^(T_PARAMETRELER ifadeler?))+
			)
	;

eksisizTekil
	:	TAMSAYI 		-> ^(T_TAMSAYI TAMSAYI)
	|	REELSAYI 		-> ^(T_REELSAYI REELSAYI)
	|	METIN 			-> ^(T_METIN METIN)
	|	ISIM 			-> ^(T_ISIM ISIM)
	|	EVET
	|	HAYIR
	|   fonksiyonIfadesi
	|	'(' mantiksal ')' 	-> mantiksal
	;

ifade
	:	mantiksal
	//|	fonksiyonIfadesi
	;

ifadeler
	:	ifade ( ',' ifade )* -> ifade+
	|	/* bosluk */
	;

fonksiyonCagirma
	:	ISIM '(' ifadeler ')' -> ^(T_FONKSIYON_CAGIRMA ^(T_ISIM ISIM) ^(T_PARAMETRELER ifadeler?))
	;

degiskenTanimlama
	: 	YENI ISIM ( '=' ifade )? -> ^(T_YENI ISIM) ^(T_ATAMA ISIM ifade)?
	;

degiskenAtama
	:	ISIM '=' ifade -> ^(T_ATAMA ISIM ifade)
	;

isimListesi
	: 	/* bosluk */
	|	ISIM ( ',' ISIM)* -> ISIM+
	;

fonksiyonTanimlama
	:	FONKSIYON ISIM '(' isimListesi ')' komut
        -> ^(T_FONKSIYON_TANIMLAMA ISIM ^(T_PARAMETRELER isimListesi?) ^(T_KOMUTLAR komut?))
	;

fonksiyonIfadesi
	:	FONKSIYON '(' isimListesi ')' '{' komutListesi '}'
        -> ^(T_FONKSIYON_IFADESI ^(T_PARAMETRELER isimListesi?) ^(T_KOMUTLAR komutListesi?))
	;

komutListesi
	:	komut*
	;

sonuc
	:	SONUC ifade -> ^(T_SONUC ifade)
	;

eger
	:	EGER ifade ISE basitKomut -> ifade ^(T_KOMUTLAR basitKomut?)
	;

yok
	:	YOK ifade ISE basitKomut ->  ifade ^(T_KOMUTLAR basitKomut?)
	;

yoksa
	:	YOKSA basitKomut -> basitKomut?
	;

kosulKomutu
	:	eger yok* yoksa? -> ^(T_KOSUL ^(T_EGER eger yok*) ^(T_YOKSA yoksa*))
	;

donguVeyaIfadeKomutu
	:	ifade 	(	';' -> ifade
			|	( IKEN basitKomut ) -> ^(T_IKEN ifade ^(T_KOMUTLAR basitKomut?))
			|	( DEFA basitKomut ) -> ^(T_DEFA ifade ^(T_KOMUTLAR basitKomut?))
		       	)
	;

basitKomut
	:	(	/* bosluk */
		|	degiskenTanimlama
		|	degiskenAtama
		|	sonuc
		) ';'!
	|	'{'! komutListesi '}'!
	|	donguVeyaIfadeKomutu
	;

komut
	:	basitKomut
	|	kosulKomutu
	|	fonksiyonTanimlama
	;

calisma_birimi
	:	komutListesi -> ^(T_CALISMA_BIRIMI komutListesi*)
	;