package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsDBHelper
import kotlinx.android.synthetic.main.settings.view.*

class SettingsRecyclerViewAdapter(
    private val settingsList: List<SettingsDBHelper.SettingsData>,
    private val listener: OnItemClickListener
    ):
    RecyclerView.Adapter<SettingsRecyclerViewAdapter.SettingsViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val itemView =LayoutInflater.from(parent.context)
            .inflate(R.layout.settings, parent, false)
        return SettingsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val currentItem = settingsList[position]
        holder.counts.text = currentItem.getCounts().toString()
        holder.bombsPercentage.text = currentItem.bombsPercentage.toString()
    }

    override fun getItemCount() = settingsList.count()

    inner class SettingsViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener
    {
        init {
            itemView.setOnClickListener(this)
        }
        val counts = itemView.tv_counts!!
        val bombsPercentage = itemView.tv_bombsPercentage!!

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}