# Activity Tracker
La seguente applicazione è stata realizzata da Arduino Leonardo, Meneghetti Federico e Visona' Francesco per il corso di Programmazione di sistemi Embedded.

# telmetria

--- Durata dei Fragment: ad esempio a noi interessa se la gente usa spesso la schemata week (vuol dire che usa l'app spesso, ossia aggiunge spesso workout),
    se usa spesso all invece vuol dire che usa l'app saltuariamente (tipo in vacanza). Inoltre vediamo se l'utente nella sezione workout info si va a rivedere i propri percorsi. Magari evidenziare che si puo fare sia con la 
    traccia si in modo autonomo.
--- Da dove vengono i wokout? da add_workout l'utente è interessato ad aggiungere i workout e usare l'app come 
    una sorta di diario. invece se vengono da activity allora l'utente è interessato a registrare il workout con la nostra app e la mappa
--- In media quantiWorkout hanno gli utenti: ci da una chiara idea di quanto l'untete utilizzi l'app.
--- Quanto usano i goals e i settings. Quante volte ce li cambiano: ci dice se sono interessati a fare dei progressi, o se non cambiano mai i setting forse ha più senso mettere solo una schermata iniziale e non dedicare un intero fragment ai setting
--- crash: ci serve per vedere se ci sono bug nascosti e fixare
-   far vedere tutti i workout che fa e non salva
-   quanti workout elimina: li elimina per quale motivo? Magari funzionano male e dobbiamo rivederli. Magari non mostriamo bene la mappa zoomata o cose simili.
--- velocita media: vediamo se le persone usano i workout per allenementi di corsa, oppure magari per registrare le proprie passeggiate in montagna.
--- altezza, peso, eta: per vedere se il nostro pubblico è giovane o meno, in forma, vedere se possiamo aggiungere funzionalita in caso all'utenza (sono comuqnue informazioni generali anche se sensibili, telemetria malevola?)
-   position precise: telemetria malevola? considera che firebase lo fa vedere già
--- mediamente dopo qunato tempo gli utenti inseriscono il proprio workout? se mette roba vechia magari le persone voglio aggiungere workout da altre app o aggiungere un modo comodo per 
    aggiungere piu cose vecchie contemporaneamente. 
--- l'applicazione è usata principalmenti in background o in foreground.
---   La notifica del service è usata tanto? Quando cliccano è in pausa? in caso ci stà mettere il bottone.
--- far vedere tutto quello firebase mostra autonomamente.
- QUANTO CI METTIAMO A INSERIRE E RIMUOVERE DATI NEL DATABASE