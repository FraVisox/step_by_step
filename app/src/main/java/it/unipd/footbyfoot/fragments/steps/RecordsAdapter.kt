package it.unipd.footbyfoot.fragments.steps


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.unipd.footbyfoot.R
import it.unipd.footbyfoot.database.RecordsViewModel
import it.unipd.footbyfoot.database.records.steps.Steps

class RecordsAdapter(private val recordsViewModel: RecordsViewModel) : ListAdapter<Steps, RecordsAdapter.RecordsViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Steps>() {
            override fun areItemsTheSame(oldItem: Steps, newItem: Steps): Boolean {

                // Logica per determinare se due elementi rappresentano lo stesso oggetto
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: Steps, newItem: Steps): Boolean {
                // Confronta se il contenuto di due oggetti è lo stesso
                // Potresti confrontare ogni campo o fare un confronto basato su una versione hash
                return oldItem.date == newItem.date
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_records, parent, false)

        return RecordsViewHolder(view, recordsViewModel)
    }



    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        val dailySteps = getItem(position)

        val dateOfRecords= dailySteps.date.toString()
        val countSteps= dailySteps.count.toString()
        holder.bind(dateOfRecords, countSteps)

    }

    class RecordsViewHolder(itemView: View, private val recordsViewModel: RecordsViewModel) : RecyclerView.ViewHolder(itemView) {

        private val dateOfRecords: TextView = itemView.findViewById(R.id.DateOfRecords)
        private val steps : TextView = itemView.findViewById(R.id.countSteps)
        private val progressBarSteps: ProgressBar = itemView.findViewById(R.id.progressbarSteps)

        fun bind(date: String, countSteps: String ) {

            dateOfRecords.text = date
            steps.text = countSteps

            val currentGoal = recordsViewModel.userGoal.value?.find { it.userId == 1 }

            if (currentGoal != null) {
                progressBarSteps.progress = Helpers.calculatePercentage(countSteps.toDouble(),currentGoal.steps.toDouble() )

            }


        }
    }

}


