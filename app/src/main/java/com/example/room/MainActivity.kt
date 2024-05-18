package com.example.room


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.room.database.RecordsRoomDatabase
import com.example.room.databinding.ActivityMainBinding
import com.example.room.fragments.maps.MapsFragment
import com.example.room.fragments.steps.StepsFragment
import com.example.room.fragments.workouts.WorkoutsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var database: RecordsRoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.post {
            binding.bottomNavigationView.selectedItemId = R.id.passi
        }

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){
                R.id.passi -> replaceFragment(StepsFragment())
                R.id.attività -> replaceFragment(MapsFragment())
                R.id.allenementi -> replaceFragment(WorkoutsFragment())
                else ->{

                }
            }
            true
        }
       populateDatabaseAndPrintContents()
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activity_main_nav_host_fragment,fragment)
        fragmentTransaction.commitAllowingStateLoss()
    }


    private fun populateDatabaseAndPrintContents() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                withContext(Dispatchers.IO) {
                    val database = RecordsRoomDatabase.getDatabase(applicationContext, this)
                    val allUser = database.userDao().getAllUsers()
                    // Stampa il contenuto del database nel logcat
                    allUser.collect { user ->
                        Log.d("Banana", "user: ${user}")
                    }
                    val allSteps = database.stepsDao().getAllStepsOrderedByDate()
                    // Stampa il contenuto del database nel logcat
                    allSteps.collect { step ->
                        Log.d("Banana", "Step: ${step}")
                    }
                    val allDistance = database.distanceDao().getAllDistancesOrderedByDate()
                    // Stampa il contenuto del database nel logcat
                    allDistance.collect { distance ->
                        Log.d("Banana", "distance: ${distance}")
                    }
                    val allCalories = database.caloriesDao().getAllCaloriesOrderedByDate()
                    // Stampa il contenuto del database nel logcat
                    allCalories.collect { calories ->
                        Log.d("Banana", "calorie: ${calories}")
                    }
                }
            } catch (e: Exception) {
                // Gestisci eventuali eccezioni
                e.printStackTrace()
            }
        }
    }
}
/*
    private fun initializeDatabase() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                withContext(Dispatchers.IO) {
                    val database = RecordsRoomDatabase.getDatabase(applicationContext, this)
                    Log.d("MainActivity0", "Database initialized")
                    logDatabaseContents(database)
                }
            } catch (e: Exception) {
                // Gestisci eventuali eccezioni
                e.printStackTrace()
            }
        }
    }

    private suspend fun logDatabaseContents(database: RecordsRoomDatabase) {
        val userDao = database.userDao()
        val stepsDao = database.stepsDao()
        val caloriesDao = database.caloriesDao()
        val distanceDao = database.distanceDao()

        // Raccogli i dati dai Flows dei DAO e stampali nel logcat
        userDao.getAllUsers().collect { users ->
            Log.d("MainActivity1", "Users: $users")
        }

        stepsDao.getAllStepsOrderedByDate().collect { steps ->
            Log.d("MainActivity2", "Steps: $steps")
        }

        caloriesDao.getAllCaloriesOrderedByDate().collect { calories ->
            Log.d("MainActivity3", "Calories: $calories")
        }

        distanceDao.getAllDistancesOrderedByDate().collect { distances ->
            Log.d("MainActivity4", "Distances: $distances")
        }
    }
 */



/*
class MainActivity : AppCompatActivity() {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(this) }

    // Sostituisci "providerPackageName" con il nome del pacchetto di HealthConnect
    private val providerPackageName = "com.example.healthconnect"

    // Funzione per controllare lo stato di HealthConnect e gestire gli aggiornamenti del provider
    private fun checkHealthConnect() {

        // Ottieni lo stato di disponibilità dello SDK di HealthConnectClient
        val availabilityStatus = HealthConnectClient.getSdkStatus(this, providerPackageName)

        // Controlla se lo SDK è indisponibile
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            // Ritorno anticipato poiché non c'è un'integrazione valida
            return
        }

        // Controlla se è necessario un aggiornamento da parte del provider
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            // Opzionalmente, reindirizza all'installer del pacchetto per trovare un provider
            navigateToProviderInStore()
            // Ritorno anticipato dopo aver reindirizzato
            return
        }

    }

    // Funzione per reindirizzare all'installer del provider per gli aggiornamenti
    private fun navigateToProviderInStore() {
        val uriString = "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
        startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setPackage("com.android.vending")
                data = Uri.parse(uriString)
                putExtra("overlay", true)
                putExtra("callerId", packageName)
            }
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<TextView>(R.id.database_button)
        button.setOnClickListener {
            val intent = Intent(this@MainActivity, DatabaseActivity::class.java)
            startActivity(intent)
        }

        // verifica che Healt Connect sia installato
        checkHealthConnect()
        // Verifica le autorizzazioni e avvia il processo
        checkPermissionsAndRun()




    }

    // Avvia il processo di richiesta delle autorizzazioni
    private fun checkPermissionsAndRun() {
        val permissions = PERMISSIONS.toTypedArray()
        Log.d("HealthConnectLog", "prima del lancio della richiesta delle autorizzazioni")

        // avvia l'attività che gestisce la richiesta di autorizzazioni
        requestPermissionActivityContract.launch(permissions)
    }

    // Set di autorizzazioni richieste per i tipi di dati necessari
    private val PERMISSIONS = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(SleepSessionRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class)
    )

    // Variabile che stabilisce di aver richiesto le autorizzazioni
    private val requestPermissionActivityContract = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

        // "permissions" è un insieme di autorizzazioni, rappresentate come coppie (autorizzazione, valore).
        // Il valore è booleano e indica se l'autorizzazione è stata concessa o meno.
        if (permissions.all { it.value }) {
            val healthConnectClient = HealthConnectClient.getOrCreate(this)

            Log.d("HealthConnectLog", "prima del lancio della richiesta dei passi")

            lifecycleScope.launch {
                //readSleepSessions()
                //readStepSessions()
                aggregateStepsIntoMonths(healthConnectClient, LocalDateTime.now().minusDays(1), LocalDateTime.now())
            }

        } else {

            // Mancano alcune autorizzazioni richieste
            // Richiedi nuovamente le autorizzazioni mancanti
            requestPermissions()
        }
    }


    // Funzione che serve a richiedere le autorizzazioni mancanti
    private fun requestPermissions() {

        // Lista per le autorizzazioni mancanti
        val permissionsToRequest = mutableListOf<String>()

        // Verifica quali autorizzazioni sono ancora mancanti ed in caso le aggiunge alla lista
        PERMISSIONS.forEach { permission ->
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }

        // Dunque le richiede
        val permissions = permissionsToRequest.toTypedArray()
        requestPermissionActivityContract.launch(permissions)
    }
/*
    // Le applicazioni possono leggere i dati di Connessione Salute fino a 30 giorni prima della prima concessione dell'autorizzazione.
    suspend fun aggregateStepsIntoMonths(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            // Convert LocalDateTime to Instant (UTC)
            val startInstant = startTime.atZone(ZoneId.systemDefault()).toInstant()
            val endInstant = endTime.atZone(ZoneId.systemDefault()).toInstant()

            val response =
                healthConnectClient.aggregateGroupByPeriod(
                    AggregateGroupByPeriodRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant),
                        timeRangeSlicer = Period.ofMonths(1)
                    )
                )
            for (monthlyResult in response) {
                // The result may be null if no data is available in the time range
                val totalSteps = monthlyResult.result[StepsRecord.COUNT_TOTAL] ?: 0L
                Log.d("HealthConnectLog", "$totalSteps")
            }
        } catch (e: Exception) {
            Log.e("HealthConnectLog", "Error while aggregating steps", e)
        }
    }
    */
/*
    suspend fun readStepSessions(healthConnectClient: HealthConnectClient): List<StepSessionData> {
        val lastDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val firstDay = lastDay.minusDays(7)

        val sessions = mutableListOf<StepSessionData>()
        val stepSessionRequest = ReadRecordsRequest(
            recordType = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(firstDay.toInstant(), lastDay.toInstant()),
            ascendingOrder = false
        )
        val stepSessions = healthConnectClient.readRecords(stepSessionRequest)
        stepSessions.records.forEach { session ->
            sessions.add(
                StepSessionData(
                    uid = session.metadata.id,
                    startTime = session.startTime,
                    endTime = session.endTime,
                    stepCount = session.count  // Assumendo che ci sia un campo count
                )
            )
        }
        for(session in sessions)
            Log.d("HealthConnectLog", "$session")
        return sessions
    }
*/


    suspend fun aggregateStepsIntoMonths(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        Log.d("HealthConnectLog", "inizio")

        try {
            val response =
                healthConnectClient.aggregateGroupByPeriod(
                    AggregateGroupByPeriodRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                        timeRangeSlicer = Period.ofMonths(1)
                    )
                )
            Log.d("HealthConnectLog", "response: $response")
            for (monthlyResult in response) {
                // The result may be null if no data is available in the time range
                val totalSteps = monthlyResult.result[StepsRecord.COUNT_TOTAL]
                Log.d("HealthConnectLog", "totalSteps: $totalSteps")
            }
        } catch (e: Exception) {
            Log.d("HealthConnectLog", "errore nella lettura dei passi")
        }
    }







}

/*


 // Inserisci operazioni
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.data.TimeRangeFilter
import java.time.LocalDateTime
import java.time.ZoneId

suspend fun aggregateStepsIntoDays(
    healthConnectClient: HealthConnectClient,
    startTime: LocalDateTime,
    endTime: LocalDateTime
) {
    try {
        val startInstant = startTime.atZone(ZoneId.systemDefault()).toInstant()
        val endInstant = endTime.atZone(ZoneId.systemDefault()).toInstant()

        val response = healthConnectClient.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startInstant, endInstant),
                timeRangeSlicer = Period.ofDays(1)
            )
        )
        for (dailyResult in response) {
            val totalSteps = dailyResult.result[StepsRecord.COUNT_TOTAL] ?: 0L
            // Qui potresti salvare i dati nel database
            saveDailyStepsToDatabase(dailyResult.period.start, totalSteps)
        }
    } catch (e: Exception) {
        // Gestisci l'errore qui
        println("Error aggregating daily steps: ${e.message}")
    }
}

fun saveDailyStepsToDatabase(date: Instant, steps: Long) {
    // Codice per salvare i dati nel database
    // Ad esempio, utilizzare Room o qualsiasi ORM che preferisci
}
suspend fun readSleepSessions(): List<SleepSessionData> {
        val lastDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            .minusDays(1)
            .withHour(12)
        val firstDay = lastDay
            .minusDays(7)

        val sessions = mutableListOf<SleepSessionData>()
        val sleepSessionRequest = ReadRecordsRequest(
            recordType = SleepSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(firstDay.toInstant(), lastDay.toInstant()),
            ascendingOrder = false
        )
        val sleepSessions = healthConnectClient.readRecords(sleepSessionRequest)
        sleepSessions.records.forEach { session ->
            val sessionTimeFilter = TimeRangeFilter.between(session.startTime, session.endTime)
            val durationAggregateRequest = AggregateRequest(
                metrics = setOf(SleepSessionRecord.SLEEP_DURATION_TOTAL),
                timeRangeFilter = sessionTimeFilter
            )
            val aggregateResponse = healthConnectClient.aggregate(durationAggregateRequest)
            sessions.add(
                SleepSessionData(
                    uid = session.metadata.id,
                    title = session.title,
                    notes = session.notes,
                    startTime = session.startTime,
                    startZoneOffset = session.startZoneOffset,
                    endTime = session.endTime,
                    endZoneOffset = session.endZoneOffset,
                    duration = aggregateResponse[SleepSessionRecord.SLEEP_DURATION_TOTAL],
                    stages = session.stages
                )
            )
        }
        for(session in sessions)
            Log.d("HealthConnectLog", "$session")
        return sessions
    }
    suspend fun readStepSessions(): List<StepSessionData> {
        val lastDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            .minusDays(1)
            .withHour(12)
        val firstDay = lastDay.minusDays(7)

        val sessions = mutableListOf<StepSessionData>()
        val stepSessionRequest = ReadRecordsRequest(
            recordType = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(firstDay.toInstant(), lastDay.toInstant()),
            ascendingOrder = false
        )
        val stepSessions = healthConnectClient.readRecords(stepSessionRequest)
        Log.d("HealthConnectLog", "$stepSessions")
        stepSessions.records.forEach { session ->
            val sessionTimeFilter = TimeRangeFilter.between(session.startTime, session.endTime)
            val countAggregateRequest = AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = sessionTimeFilter
            )
            val aggregateResponse = healthConnectClient.aggregate(countAggregateRequest)
            sessions.add(
                StepSessionData(
                    uid = session.metadata.id,
                    startTime = session.startTime,
                    startZoneOffset = session.startZoneOffset,
                    endTime = session.endTime,
                    endZoneOffset = session.endZoneOffset,
                    stepCount = aggregateResponse[StepsRecord.COUNT_TOTAL] ?: 0L // Assuming COUNT_TOTAL is a valid metric
                )
            )
        }
        for(session in sessions)
            Log.d("HealthConnectLog", "$session")
        return sessions
    }






 data class StepSessionData(
        val uid: String,
        val startTime: Instant,
        val startZoneOffset: ZoneOffset?,
        val endTime: Instant,
        val endZoneOffset: ZoneOffset?,
        val stepCount: Long
    )
    data class SleepSessionData(
        val uid: String,
        val title: String?,
        val notes: String?,
        val startTime: Instant,
        val startZoneOffset: ZoneOffset?,
        val endTime: Instant,
        val endZoneOffset: ZoneOffset?,
        val duration: Duration?,
        val stages: List<SleepSessionRecord.Stage> = listOf()
    )

 */
 */