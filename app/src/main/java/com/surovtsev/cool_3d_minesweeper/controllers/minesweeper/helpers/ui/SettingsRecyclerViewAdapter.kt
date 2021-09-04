package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surovtsev.cool_3d_minesweeper.R
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsDBHelper
import kotlinx.android.synthetic.main.settings.view.*

class SettingsRecyclerViewAdapter(
    private val settingsList: MutableList<SettingsDBHelper.SettingsData>,
    private val listener: ISettingsRVEventListener
    ):
    RecyclerView.Adapter<SettingsRecyclerViewAdapter.SettingsViewHolder>()
{
    fun isValidPosition(position: Int): Boolean {
        if (position >= settingsList.count()) {
            Log.e("Minesweeper", "settings list position error")
            return false
        }
        return true
    }

    fun get(position: Int) = settingsList[position]

    fun removeAt(position: Int) {
        settingsList.removeAt(position)
        this.notifyItemRemoved(position)
        this.notifyItemRangeChanged(
            position,
            settingsList.count()
        )
    }

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
            itemView.btn_delete.setOnClickListener {
                callIfTruePosition(listener::onItemDelete)
            }
        }
        val counts = itemView.tv_counts!!
        val bombsPercentage = itemView.tv_bombsPercentage!!

        override fun onClick(v: View?) {
            callIfTruePosition(listener::onItemClick)
        }

        private fun callIfTruePosition(f: (Int) -> Unit) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                f(position)
            }
        }
    }

    interface ISettingsRVEventListener {
        fun onItemClick(position: Int)
        fun onItemDelete(position: Int)
    }
}