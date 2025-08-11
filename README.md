# ConnectaWord - Android Klijent

Ovo je zvanični Android klijent za multiplayer igru pogađanja reči "ConnectaWord". Aplikacija je razvijena korišćenjem modernih Android tehnologija, sa fokusom na čistu arhitekturu i reaktivni UI.

## ✨ Ključne Funkcionalnosti

* **Autentifikacija Korisnika:**
    * Ekran za registraciju i prijavu.
    * Komunikacija sa backend serverom za verifikaciju.
* **Lobi za Igru:**
    * Prikaz liste dostupnih soba dobijenih sa servera.
    * Mogućnost kreiranja nove sobe za igru.
* **Real-time Soba za Igru:**
    * Ulazak u sobu uspostavlja WebSocket konekciju.
    * Prikaz poruka od servera u realnom vremenu (npr. ko je ušao/izašao iz sobe).
    * Navigacija između ekrana.

## 🛠️ Tehnološki Stek

* **Jezik:** [Kotlin](https://kotlinlang.org/)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
* **Arhitektura:** MVVM (Model-View-ViewModel) + Repository Pattern
* **Asinhrono programiranje:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
* **Mrežni Sloj:**
    * [Retrofit](https://square.github.io/retrofit/) za HTTP zahteve.
    * [Ktor Client](https://ktor.io/docs/client-websockets.html) za WebSocket komunikaciju.
* **Navigacija:** [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation)
* **Dependency Management:** [Gradle Version Catalog (libs.versions.toml)](https://docs.gradle.org/current/userguide/platforms.html)

## 🚀 Pokretanje

1.  **Pokrenite Server:**
    * Pre pokretanja klijenta, neophodno je da [ConnectaWord Server](link-do-vaseg-server-repozitorijuma) bude pokrenut lokalno.
2.  **Podesite Adresu Servera:**
    * U fajlu `api/RetrofitInstance.kt` i `api/WebSocketService.kt`, proverite da li je adresa servera podešena na `10.0.2.2` (za Android Emulator).
3.  **Pokrenite Aplikaciju:**
    * Otvorite projekat u Android Studiju i pokrenite na emulatoru ili fizičkom uređaju.

## 🚧 Status Projekta

* Trenutno u fazi razvoja.
* Implementirani su ekrani za autentifikaciju i lobi. Uspostavljena je veza sa serverom.
* Sledeći korak: Dizajn i implementacija ekrana za samu igru i slanje/primanje poruka o toku igre.