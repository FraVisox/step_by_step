package com.example.room.fragments.steps


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.room.R
import com.example.room.database.RecordsViewModel
import com.example.room.database.records.steps.Steps

class RecordsAdapter2(private val recordsViewModel: RecordsViewModel, private val stepsList: List<Steps>) : RecyclerView.Adapter<RecordsAdapter2.RecordsViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_records, parent, false)

        return RecordsViewHolder(view, recordsViewModel)
    }

    override fun getItemCount(): Int {
        return stepsList.size
    }


    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        val dailySteps = stepsList[position]

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
                progressBarSteps.progress = Helpers.calculatePercentage(countSteps.toInt(),currentGoal.steps.toDouble() )

            }


        }
    }

}


