package com.killua.ranky.features.details

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.killua.data.models.Club
import com.killua.ranky.R
import com.killua.ranky.base.BaseFragment
import com.killua.ranky.databinding.FragmentDetailsBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import timber.log.Timber


private const val USER_ID_PRAM = "user_id_pram"

class DetailsFragment : BaseFragment(R.layout.fragment_details) {
    private var paramId: String? = null
    private lateinit var viewModel: DetailsViewModel
    private val binding by viewBinding(FragmentDetailsBinding::bind)
    private val image by lazy {
        binding.ivClubImage
    }
    private val description by lazy {
        binding.tvDescription
    }
    private val countryName by lazy {
        binding.tvCountry
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = fragmentViewModel()
        arguments?.let {
            paramId = it.getString(USER_ID_PRAM)
            Timber.e(paramId)
        }
        paramId?.let {
            viewModel.getClub(it)
        }
        viewModel.clubLiveData.observe(viewLifecycleOwner) {
            onSubmitClub(it)
        }
    }

    private fun onSubmitClub(club: Club) {
        Glide.with(this)
            .load(club.image)
            .fitCenter()
            .placeholder(R.drawable.ic_launcher_background)
            .into(image)
        val text = getString(R.string.description_text, club.name, club.country, club.value)
        val ss = SpannableString(text)
        val boldSpan = StyleSpan(Typeface.BOLD)

        (requireActivity() as AppCompatActivity).supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.title = club.name
        }
        setHasOptionsMenu(true)
        ss.setSpan(
            boldSpan,
            text.indexOf(club.name),
            text.indexOf(club.name) + club.name.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        description.text = ss
        countryName.text = club.country

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().supportFragmentManager.popBackStack(
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                );
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.dispose()
    }

    companion object {

        @JvmStatic
        fun newInstance(userId: String) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_ID_PRAM, userId)
                }
            }
    }
}