# 📱 InstaGrab — GitHub Actions ile APK Derleme

Bu repo'yu GitHub'a yükleyince **Actions otomatik APK derler**, indirip telefona kurarsın.

---

## 🚀 Adım Adım (10 dakika)

### 1. GitHub hesabı aç (yoksa)
→ github.com/signup — ücretsiz

### 2. Yeni repo oluştur
- Sağ üst + → New repository → İsim: InstaGrab → Create

### 3. Dosyaları yükle
Repo sayfasında "uploading an existing file" → bu klasördeki TÜM dosyaları sürükle-bırak:
- .github/ klasörü (gizli olabilir — Mac: Cmd+Shift+. ile göster)
- app/ gradle/ .gitignore build.gradle gradle.properties gradlew settings.gradle

→ Commit changes

### 4. Actions sekmesi
- Repo → Actions sekmesi → "Build APK" workflow başlar (3-5 dk)
- Bitince: workflow'a tıkla → Artifacts → InstaGrab-APK → İndir

### 5. Telefona kur
APK'yı telefona aktar → dokun → "Bilinmeyen kaynak" uyarısında Yine de kur → ✅ Tamam!

---

## Uygulamayı Kullanmak

Sadece bulut API: Linki yapıştır → Getir → İndir (bazen Instagram kısıtlar)

Yerel sunucu (en güvenilir):
1. PC'de: python app.py
2. Uygulamada Ayarlar → http://192.168.1.X:5000 gir
3. PC IP: Windows=ipconfig, Mac=ifconfig

---

## Sorun Çözme

- Actions başlamıyor → Repo Settings → Actions → "Allow all actions"
- Telefona kurulmuyor → Ayarlar → Güvenlik → Bilinmeyen kaynaklar → Aç
- Sunucuya bağlanmıyor → Aynı Wi-Fi'da mısın? IP doğru mu?
