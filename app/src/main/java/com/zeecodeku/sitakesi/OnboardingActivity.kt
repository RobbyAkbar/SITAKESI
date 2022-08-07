package com.zeecodeku.sitakesi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.squareup.picasso.Picasso

class OnboardingActivity : AppCompatActivity() {

    /**
     * Views required for on-boarding fragments.
     */
    private lateinit var viewPager: ViewPager
    private lateinit var dotsLayout: LinearLayout
    private lateinit var btnSkip: Button
    private lateinit var btnNext: Button

    /**
     * Stores the image resource id that is used to display on-boarding items.
     */
    internal lateinit var imageResources: IntArray

    //  viewpager change listener
    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                addBottomDots(position)

                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == imageResources.size - 1) {
                    // last page. make button text to GOT IT
                    btnNext.setText(R.string.onboarding_got_it)
                    btnSkip.visibility = View.GONE
                } else {
                    // still pages are left
                    btnNext.setText(R.string.onboarding_next)
                    btnSkip.visibility = View.VISIBLE
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
                // Do nothing.
            }

            override fun onPageScrollStateChanged(arg0: Int) {
                // Do nothing.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        setOnboardingSeen()

        viewPager = findViewById(R.id.view_pager)
        dotsLayout = findViewById(R.id.layoutDots)
        btnSkip = findViewById(R.id.btn_skip)
        btnNext = findViewById(R.id.btn_next)

        // Contains images that will be shown on different onoarding screens.
        imageResources = intArrayOf(
            R.mipmap.ic_launcher,
            R.mipmap.ic_efisien,
            R.mipmap.ic_transparansi
        )
        // adding bottom dots
        addBottomDots(0)

        val myViewPagerAdapter = MyViewPagerAdapter()
        viewPager.adapter = myViewPagerAdapter
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)

        // Listen to skip events.
        btnSkip.setOnClickListener { launchHomeScreen() }

        btnNext.setOnClickListener {
            // checking for last page
            // if last page home screen will be launched
            val current = getItem()
            if (current < imageResources.size) {
                // move to next screen
                viewPager.currentItem = current
            } else {
                launchHomeScreen()
            }
        }
    }

    /**
     * Sets the field that onboarding has been displayed so that it is not displayed again.
     */
    private fun setOnboardingSeen() {
        val ed = PreferenceManager.getDefaultSharedPreferences(this).edit()
        ed.putBoolean(getString(R.string.pref_has_user_seen_onboarding), true)
        ed.apply()
    }

    /**
     * Adds the bottom dots to the current page.
     *
     * @param currentPage page number, zero based.
     */
    private fun addBottomDots(currentPage: Int) {
        val dots = arrayOfNulls<TextView>(imageResources.size)

        val colorActiveDot = ContextCompat.getColor(this, R.color.dot_light_active)
        val colorInactiveDot = ContextCompat.getColor(this, R.color.dot_dark_inactive)

        dotsLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)
            dots[i]!!.textSize = (35f)
            dots[i]!!.setTextColor(colorInactiveDot)
            dotsLayout.addView(dots[i])
        }
        if (dots.isNotEmpty())
            dots[currentPage]!!.setTextColor(colorActiveDot)
    }

    private fun getItem(): Int {
        return viewPager.currentItem + 1
    }

    /**
     * Launches the home screen.
     */
    private fun launchHomeScreen() {
        finish()
    }

    /**
     * View pager adapter
     */
    inner class MyViewPagerAdapter internal constructor() : PagerAdapter() {

        private var layoutInflater: LayoutInflater? = null

        private val titles: Array<String> = resources.getStringArray(R.array.onboarding_titles)
        private val descriptions: Array<String> = resources.getStringArray(R.array.onboarding_descriptions)

        override fun getCount(): Int {
            return imageResources.size
        }

        init {
            // For error checking.
            if (titles.size != descriptions.size || titles.size != imageResources.size) {
                finish()
            }
        }

        @NonNull
        override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            if (layoutInflater == null) {
                finish()
            }
            val view = layoutInflater!!.inflate(R.layout.fragment_onboarding, container, false)
            val imageView = view.findViewById<ImageView>(R.id.image)
            val titleView = view.findViewById<TextView>(R.id.titleView)
            val descriptionView = view.findViewById<TextView>(R.id.descriptionView)
            // Set the data on views.
            Picasso.get()
                .load(imageResources[position])
                .into(imageView)
            titleView.text = titles[position]
            descriptionView.text = descriptions[position]

            container.addView(view)
            return view
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}
