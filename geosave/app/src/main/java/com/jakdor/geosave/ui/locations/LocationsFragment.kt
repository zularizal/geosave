/*
 * GeoSave - app for easy sharing and collaborating on GPS related data
 * Copyright (C) 2018  Jakub Dorda
 *
 * Software under GPLv3 licence - full copyright notice available at:
 * https://github.com/jakdor/geosave/blob/master/README.md
 */

package com.jakdor.geosave.ui.locations

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakdor.geosave.R
import com.jakdor.geosave.di.InjectableFragment
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages other fragments in saved locations tab
 */
class LocationsFragment: Fragment(), InjectableFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var viewModel: LocationsViewModel? = null

    private val fragmentMap: MutableMap<String, androidx.fragment.app.Fragment> = mutableMapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_locations, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(viewModel == null){
            viewModel = ViewModelProviders.of(this, viewModelFactory)
                    .get(LocationsViewModel::class.java)
        }

        viewModel?.requestUpdatesOnCurrentFragment()
        viewModel?.observeChosenRepositoryIndex()
        observeCurrentFragmentId()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentMap.clear()
    }

    /**
     * Observe [LocationsViewModel] updates on current child fragment id
     */
    fun observeCurrentFragmentId(){
        viewModel?.currentFragmentId?.observe(this, Observer {
            handleNewCurrentFragmentId(it)
        })
    }

    /**
     * Handle new current fragment id received from [LocationsViewModel] currentFragmentId
     */
    fun handleNewCurrentFragmentId(id: String?){
        if(id != null){
            when(id){
                ReposBrowserFragment.CLASS_TAG -> switchToReposBrowserFragement()
                RepoFragment.CLASS_TAG -> switchToRepoFragment()
            }
        }
    }

    /**
     * Create or reattach [ReposBrowserFragment]
     */
    fun switchToReposBrowserFragement(){
        if (!fragmentMap.containsKey(ReposBrowserFragment.CLASS_TAG)) {
            fragmentMap[ReposBrowserFragment.CLASS_TAG] = ReposBrowserFragment.newInstance()
            childFragmentManager.beginTransaction()
                    .add(R.id.locations_fragment_layout, fragmentMap[ReposBrowserFragment.CLASS_TAG]!!,
                            ReposBrowserFragment.CLASS_TAG)
                    .commit()
            Timber.i("Created %s", ReposBrowserFragment.CLASS_TAG)
        }

        hideAllFragments()

        childFragmentManager.beginTransaction()
                .show(fragmentMap[ReposBrowserFragment.CLASS_TAG]!!)
                .commit()
        
        Timber.i("Attached child fragment: %s", ReposBrowserFragment.CLASS_TAG)
    }

    /**
     * Create or reattach [RepoFragment]
     */
    fun switchToRepoFragment(){
        if (!fragmentMap.containsKey(RepoFragment.CLASS_TAG)) {
            fragmentMap[RepoFragment.CLASS_TAG] = RepoFragment.newInstance()
            childFragmentManager.beginTransaction()
                    .add(R.id.locations_fragment_layout, fragmentMap[RepoFragment.CLASS_TAG]!!,
                            RepoFragment.CLASS_TAG)
                    .commit()
            Timber.i("Created %s", RepoFragment.CLASS_TAG)
        }

        hideAllFragments()

        childFragmentManager.beginTransaction()
                .show(fragmentMap[RepoFragment.CLASS_TAG]!!)
                .commit()

        Timber.i("Attached child fragment: %s", RepoFragment.CLASS_TAG)
    }

    /**
     * Hide all fragments stored by supportFragmentManager
     */
    private fun hideAllFragments(){
        for(fragment in childFragmentManager.fragments){
            childFragmentManager.beginTransaction()
                    .hide(fragment)
                    .commit()
        }
    }

    companion object {

        const val CLASS_TAG = "LocationsFragment"

        fun newInstance(): LocationsFragment{
            val args = Bundle()
            val fragment = LocationsFragment()
            fragment.arguments = args
            fragment.retainInstance = true
            return fragment
        }
    }
}