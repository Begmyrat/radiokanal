package com.example.radiokanal.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PagerAdapter(
    fm: FragmentActivity,
    private val tabLayout: TabLayout? = null,
    private val viewPager2: ViewPager2? = null
): FragmentStateAdapter(fm ) {

    private val fragmentList:ArrayList<Fragment> = arrayListOf()
    private val fragmentTitleList:ArrayList<String> = arrayListOf()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment:Fragment,title:String?="Title"){
        fragmentList.add(fragment)
        fragmentTitleList.add(title.toString())
    }

    private fun setTabLayoutMediator(){
        TabLayoutMediator(tabLayout!!, viewPager2!!) {
                tab, position ->
            tab.text = this.fragmentTitleList[position]
        }.attach()
    }


    fun attach(){
        viewPager2?.adapter = this
        tabLayout?.let {
            viewPager2?.let {
//                setTabLayoutMediator()
            }
        }
    }
}