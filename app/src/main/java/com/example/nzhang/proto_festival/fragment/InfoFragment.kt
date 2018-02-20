package com.example.nzhang.proto_festival.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageButton
import com.example.nzhang.proto_festival.InfoAdapter
import com.example.nzhang.proto_festival.R


class InfoFragment : Fragment() {

    lateinit private var recycleView: RecyclerView
    lateinit private var priceButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_info, container, false)
        val mLayoutManager = LinearLayoutManager(context)

        recycleView = view.findViewById(R.id.container_info)
        recycleView.layoutManager = mLayoutManager
        recycleView.adapter = InfoAdapter()

        return view
    }

    private fun browserAboutPage() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://festival-film-animation.fr/qui-sommes-nous.html"))
        startActivity(browserIntent)
    }
}
