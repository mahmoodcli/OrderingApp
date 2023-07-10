package com.example.orderApp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.orderApp.fragment.DashboardActivity
import com.example.orderApp.fragment.AcceptedFragment
import com.example.orderApp.fragment.RejectedFragment


class ViewPagerAdapter(supportFragmentManager: FragmentManager?) :FragmentPagerAdapter(supportFragmentManager!!,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    override fun getCount(): Int =3

    override fun getItem(position: Int): Fragment {
        return when(position){
            0-> DashboardActivity()
            1->AcceptedFragment()
            2 ->RejectedFragment()
            else-> DashboardActivity()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0->"Pending"
            1->"Accepted"
            2->"Rejected"
            else ->"Pending"
        }
    }
}