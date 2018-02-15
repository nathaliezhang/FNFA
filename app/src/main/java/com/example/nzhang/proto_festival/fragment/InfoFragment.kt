package com.example.nzhang.proto_festival.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageButton
import android.widget.TextView
import com.example.nzhang.proto_festival.InfoAdapter
import com.example.nzhang.proto_festival.R
import com.example.nzhang.proto_festival.ScrollingLinearLayoutManager


class InfoFragment : Fragment(), View.OnClickListener {

    lateinit private var recycleView: RecyclerView
    lateinit private var priceButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_info, container, false)
        val mLayoutManager = ScrollingLinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false, 100)

        recycleView = view.findViewById(R.id.container_info)
        recycleView.layoutManager = mLayoutManager
        recycleView.adapter = InfoAdapter()

        return view
    }

    override fun onClick(view: View) {
        if (view is ImageButton) {
            ///val id = view.id
            //val parentView = view.parent as View
            //val expandableLayout: ExpandableRelativeLayout = parentView.findViewById(contentMapping[id]!!)
            //view.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.next_on, 0)

            //val drawable: TransitionDrawable = priceButton.drawable as TransitionDrawable
            //drawable.startTransition(500)

            //expandableLayout.toggle()

            //collapseAll(view.parent as View, id)
        } else if (view is TextView) {
            //if (view.id == R.id.info_about_link) {
                //browserAboutPage()
            //}
        }
    }

    private fun browserAboutPage() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://festival-film-animation.fr/qui-sommes-nous.html"))
        startActivity(browserIntent)
    }
}
