package com.example.nzhang.proto_festival.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.TextView
import com.example.nzhang.proto_festival.R


class InfoFragment : Fragment(), View.OnClickListener {

    private val contentMapping = hashMapOf<Int, Int>(
            R.id.info_price_button to R.id.info_price_content,
            R.id.info_about_button to R.id.info_about_content,
            R.id.info_partners_button to R.id.info_partners_content
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_info, container, false)
        val infoPriceButton = view.findViewById<Button>(R.id.info_price_button)
        val infoAboutButton = view.findViewById<Button>(R.id.info_about_button)
        val infoAboutLink = view.findViewById<TextView>(R.id.info_about_link)
        val infoPartnersButton = view.findViewById<Button>(R.id.info_partners_button)

        infoPriceButton.setOnClickListener(this)
        infoAboutButton.setOnClickListener(this)
        infoPartnersButton.setOnClickListener(this)
        infoAboutLink.setOnClickListener(this)
        collapseAll(view, -1)

        return view
    }


    override fun onClick(view: View) {
        if (view is Button) {
            val id = view.id
            val parentView = view.parent as View
            val expandableLayout: ExpandableRelativeLayout = parentView.findViewById(contentMapping[id]!!)
            expandableLayout.toggle()
            collapseAll(view.parent as View, id)
        } else if (view is TextView) {
            if (view.id == R.id.info_about_link) {
                browserAboutPage()
            }
        }
    }

    private fun collapseAll(view: View, id: Int) {
        for (item in contentMapping) {
            if (item.value != id) {
                val expandableLayout = view.findViewById<ExpandableRelativeLayout>(item.value)
                expandableLayout.collapse()
            }
        }
    }

    private fun browserAboutPage() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://festival-film-animation.fr/qui-sommes-nous.html"))
        startActivity(browserIntent)
    }
}
