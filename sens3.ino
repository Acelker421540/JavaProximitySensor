#define TRIG_PIN 9 // HC-SR04 Trig pini Arduino'nun 9 numaralı pinine bağlanacak
#define ECHO_PIN 10 // HC-SR04 Echo pini Arduino'nun 10 numaralı pinine bağlanacak
#define BUZZER_PIN 11 // Buzzer Arduino'nun 11 numaralı pinine bağlanacak

void setup() {
  Serial.begin(9600); // Seri iletişim başlatılır
  pinMode(TRIG_PIN, OUTPUT); // Trig pini çıkış olarak ayarlanır
  pinMode(ECHO_PIN, INPUT); // Echo pini giriş olarak ayarlanır
  pinMode(BUZZER_PIN, OUTPUT); // Buzzer çıkış olarak ayarlanır
}

void loop() {
  long duration;
  int distance;

  // Trig pinine 10 mikrosaniyelik bir darbe gönder
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);

  // Echo pininden dönüş süresini ölç
  duration = pulseIn(ECHO_PIN, HIGH);

  // Mesafeyi hesapla (cm cinsinden)
  distance = duration * 0.034 / 2;

  // Seri porta mesafe bilgisini gönder
  Serial.println(distance);

  // Mesafeye göre buzzer'ı kontrol et
  if (distance > 0 && distance <= 30) {
    tone(BUZZER_PIN, 1000); // Mesafe 30 cm'den azsa buzzer 1 kHz'de ses çıkarır
  } else if (distance > 30 && distance <= 60) {
    tone(BUZZER_PIN, 500); // Mesafe 30-60 cm arasındaysa buzzer 500 Hz'de ses çıkarır
  } else {
    noTone(BUZZER_PIN); // Mesafe 60 cm'den fazlaysa buzzer sessiz kalır
  }

  // Biraz bekle (örnekleme hızı için)
  delay(100);
}
