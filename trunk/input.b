fonksiyon closure(n) {
    yeni x = n;
    fonksiyon f() {
        x = x + 1;
        sonuc x;
    }
    sonuc f;
}

__yazss(closure(3)());

yeni g = closure(10);
__yazss(g());
__yazss(g());
__yazss(g());

__yazss( fonksiyon(n) { sonuc fonksiyon(m) { sonuc m + n; }; } (100) (50) );
