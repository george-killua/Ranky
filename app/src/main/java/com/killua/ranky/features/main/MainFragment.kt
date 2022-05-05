package com.killua.ranky.features.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.killua.data.models.Club
import com.killua.data.utils.RepoResult
import com.killua.ranky.R
import com.killua.ranky.base.BaseFragment
import com.killua.ranky.databinding.FragmentMainBinding
import com.killua.ranky.features.details.DetailsFragment
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

private const val ORIENTATION_PRAM = "n4n4j23n4j2"

enum class SortType {
    NAME,
    VALUE
}

class MainFragment : BaseFragment(R.layout.fragment_main) {
    private var sortType = SortType.NAME
    private val binding by viewBinding(FragmentMainBinding::bind)
    private lateinit var viewModel: MainViewModel
    private val clubsAdapter = ClubsAdapter { club -> adapterOnClick(club) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sortType = SortType.valueOf(
            savedInstanceState?.getString(ORIENTATION_PRAM)
                ?: sortType.name
        )
        val rv = binding.rvClubs
        rv.adapter = clubsAdapter
        rv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        (requireActivity() as AppCompatActivity).supportActionBar?.let {
            it.setDisplayShowHomeEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.title = getString(R.string.main_fragment_title)
        }
        setHasOptionsMenu(true)
        rv.itemAnimator = DefaultItemAnimator()
        viewModel = fragmentViewModel()
        viewModel.getClubs(isNetworkConnected(requireContext()), R.string.database_need_to_update)
        binding.btnRetry.setOnClickListener {
            viewModel.getClubs(
                isNetworkConnected(requireContext()),
                R.string.database_need_to_update
            )
        }
        viewModel.clubsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                RepoResult.Loading -> binding.progressBar.visibility = VISIBLE
                is RepoResult.Success -> {

                    binding.rvClubs.visibility = VISIBLE

                    clubsAdapter.submitList(it.data).also {
                        clubsAdapter.sort(sortType)
                        binding.noNetView.visibility = GONE
                        binding.progressBar.visibility = GONE
                    }
                }
                is RepoResult.Failure -> {
                    Toast.makeText(
                        requireContext(),
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.noNetView.visibility = VISIBLE.also {
                        binding.rvClubs.visibility = GONE
                        binding.progressBar.visibility = GONE
                    }
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ORIENTATION_PRAM, sortType.name)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.m_item_value -> {
                sortType = SortType.VALUE
                clubsAdapter.sortByValue()
            }
            R.id.m_item_name -> {
                sortType = SortType.NAME
                clubsAdapter.sortByName()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun ClubsAdapter.sort(sortType: SortType) {
        when (sortType) {
            SortType.NAME -> sortByName()
            SortType.VALUE -> sortByValue()
        }
    }

    //Scrolling not working
    private fun ClubsAdapter.sortByName(): Boolean {
        submitList(currentList.sortedBy { club -> club.name })
        val smoothScroller: SmoothScroller = object : LinearSmoothScroller(activity) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 0

        binding.rvClubs.layoutManager?.startSmoothScroll(smoothScroller)
        return true
    }


    //Scrolling not working

    private fun ClubsAdapter.sortByValue(): Boolean {
        submitList(currentList.sortedByDescending { club -> club.value })
        val smoothScroller: SmoothScroller = object : LinearSmoothScroller(activity) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 0

        binding.rvClubs.layoutManager?.startSmoothScroll(smoothScroller)
        return true
    }

    private fun adapterOnClick(club: Club) {
        val detailsFragment = DetailsFragment.newInstance(club.id)
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            .replace(R.id.container, detailsFragment).addToBackStack(this::class.java.name).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.dispose()
    }

    private fun isNetworkConnected(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
}

