package com.example.nzhang.proto_festival

import android.graphics.drawable.TransitionDrawable
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

/**
 * Created by nathalie on 14/02/2018.
 */

class InfoAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_PRICE: Int = 0
    private val TYPE_ABOUT: Int = 1
    private val TYPE_PARTNER: Int = 2
    private var mExpandedPosition: Int = -1
    private var previousExpandedPosition: Int = -1

    override fun getItemCount(): Int = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)

        if (viewType == TYPE_PRICE) {
            val view = layoutInflater.inflate(R.layout.fragment_info_price, parent, false)
            return InfoAdapter.PriceViewHolder(view)
        } else if (viewType == TYPE_ABOUT) {
            val view = layoutInflater.inflate(R.layout.fragment_info_about, parent, false)
            return InfoAdapter.AboutViewHolder(view)
        } else if (viewType == TYPE_PARTNER) {
            val view = layoutInflater.inflate(R.layout.fragment_info_partner, parent, false)
            return InfoAdapter.PartnerViewHolder(view)
        }
        throw RuntimeException("Not match type for" + viewType + ".")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val isExpanded = position == mExpandedPosition

        if (holder is PriceViewHolder){
            holder.details.visibility = (if (isExpanded) View.VISIBLE else View.GONE)
            holder.itemView.isActivated = isExpanded

            if (isExpanded)
                previousExpandedPosition = position

            holder.itemView.setOnClickListener {
                //val drawable: TransitionDrawable = holder.imageButton.drawable as TransitionDrawable
                //drawable.startTransition(200)

                mExpandedPosition = if (isExpanded) -1 else position
                notifyItemChanged(previousExpandedPosition)
                notifyItemChanged(position)
            }
        }
        if (holder is AboutViewHolder){
            holder.details.visibility = (if (isExpanded) View.VISIBLE else View.GONE)
            holder.itemView.isActivated = isExpanded

            if (isExpanded)
                previousExpandedPosition = position

            holder.itemView.setOnClickListener {
                mExpandedPosition = if (isExpanded) -1 else position
                notifyItemChanged(previousExpandedPosition)
                notifyItemChanged(position)
            }
        }
        if (holder is PartnerViewHolder){
            holder.details.visibility = (if (isExpanded) View.VISIBLE else View.GONE)
            holder.itemView.isActivated = isExpanded

            if (isExpanded)
                previousExpandedPosition = position

            holder.itemView.setOnClickListener {
                mExpandedPosition = if (isExpanded) -1 else position
                notifyItemChanged(previousExpandedPosition)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> TYPE_PRICE
            1 -> TYPE_ABOUT
            2 -> TYPE_PARTNER
            else -> -1
        }
    }

    class PriceViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val group = view.findViewById<ConstraintLayout>(R.id.item_group_price)
        val details = view.findViewById<ConstraintLayout>(R.id.item_details_price)
        //val imageButton = view.findViewById<ImageButton>(R.id.info_price_button_arrow)
    }

    class AboutViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val group = view.findViewById<ConstraintLayout>(R.id.item_group_about)
        val details = view.findViewById<ConstraintLayout>(R.id.item_details_about)
    }

    class PartnerViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val group = view.findViewById<ConstraintLayout>(R.id.item_group_partner)
        val details = view.findViewById<ConstraintLayout>(R.id.item_details_partner)
    }
}
