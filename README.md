# Step By Step
La seguente applicazione è stata realizzata da Arduino Leonardo, Meneghetti Federico e Visona' Francesco per il corso di Programmazione di sistemi Embedded.
E' un'applicazione Android in grado di gestire e rilevare le attività fisiche di un singolo utente, calcolando il quantitativo di calorie bruciate, km percorsi e passi fatti giornalmente, e rapportarli all'obbiettivo giornaliero.

## Esecuzione
Per installare ed eseguire l'applicazione è sufficiente possedere Android Studio, aprire il progetto e installarlo sul proprio dispositivo. Non è ancora stata pensata la creazione di un APK.

## Telemetria
Il focus dell'applicazione è sull'utilizzo di strumenti telemetrici per migliorare l'esperienza utente e prendere qualche dato. Il framework utilizzato è Firebase: eventi, proprietà utente e tracce personalizzate sono nominate in RecordsApplication.kt.
Il file google-services.json contiene le chiavi per collegare l'applicazione alla console di Firebase.
Il report sull'applicazione e sugli strumenti di telemetria è anche presente in questo repository.

## Maps API key
Nel file maps.properties è possibile inserire l'API key per il Maps SDK. Senza di esso, l'applicazione non permetterà di accedere alla mappa.

## Test
Sono stati implementati dei test locali per la classe Helpers.kt.
