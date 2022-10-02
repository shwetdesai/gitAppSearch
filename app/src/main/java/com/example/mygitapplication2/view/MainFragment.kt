package com.example.mygitapplication2.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mygitapplication.infra.model.GitData
import com.example.mygitapplication2.MyGitApplication
import com.example.mygitapplication2.R
import com.example.mygitapplication2.databinding.FragmentMainBinding
import com.example.mygitapplication2.di.AppViewModelFactory
import com.example.mygitapplication2.infra.network.ApiStatus.HIDE_PROGRESS
import com.example.mygitapplication2.infra.network.ApiStatus.SHOW_PROGRESS
import com.example.mygitapplication2.infra.utils.Utils
import com.example.mygitapplication2.view.adapter.UserAdapter
import com.example.mygitapplication2.viewModel.MainFragmentViewModel
import com.google.gson.Gson
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment(), UserAdapter.UserCardListener {

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private lateinit var viewModel: MainFragmentViewModel
    lateinit var adapter: UserAdapter
    private var list = ArrayList<GitData.User>()
    private var page = 0
    private var searchString = ""

    companion object {
        private const val TAG = "MainFragment"

        const val KEY_USER = "KEY_USER"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyGitApplication.getInstance()?.appComponents?.inject(this)
        initMvvm()
        setUpObserver()
        setUpAdapter()
        setUpTextWatcher()
        setUpScrollViewListener()
        binding.clSearch.setOnClickListener {
            closeKeyboard()
            getUserData()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initMvvm() {
        if (activity != null && context != null) {
            viewModel =
                ViewModelProvider(this, viewModelFactory)[MainFragmentViewModel::class.java]
            binding.viewModel = viewModel
        }
    }

    private fun getUserData() {
        if (activity != null && context != null) {
            viewModel.getAllGitUsers(searchString, page)
        }
    }

    private fun setUpAdapter() {
        if (activity != null && context != null) {
            adapter = UserAdapter(requireContext(), list, this)
            binding.rvUsers.adapter = adapter
            binding.rvUsers.isNestedScrollingEnabled = false
        }
    }

    private fun setUpTextWatcher() {
        if (activity != null && context != null) {
            binding.etSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s != null) {
                        searchString = s.toString()
                        page = 0
                        if (searchString.isEmpty()) {
                            adapter.clearList()
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    Log.i(TAG, "beforeTextChanged $s")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.i(TAG, "onTextChanged $s")
                }
            })
        }
    }

    private fun setUpScrollViewListener() {
        if (activity != null && context != null) {
            binding.rvUsers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1)) {
                        page++
                        getUserData()
                    }
                }
            })
        }
    }

    private fun setUpObserver() {
        viewModel.data.observe(viewLifecycleOwner) {
            if (activity != null && context != null && it != null) {
                if (page == 0)
                    adapter.clearList()
                adapter.updateList(it)
                binding.rvUsers.animate()
            }
        }

        viewModel.noInternet.observe(viewLifecycleOwner) {
            if (it)
                Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_LONG).show()
        }

        viewModel.apiStatus.observe(viewLifecycleOwner) {
            try {
                if (SHOW_PROGRESS.equals(it.getStatus())) {
                    showProgressbar()
                } else if (HIDE_PROGRESS.equals(it.getStatus())) {
                    endProgressDialog()
                }
            } catch (e: java.lang.Exception) {
                Log.e(TAG,e.toString())
            }
        }
    }

    private fun closeKeyboard() {
        activity?.currentFocus?.let { view ->
            val imm =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showProgressbar() {
        if (context != null)
            Utils.showProgress(requireContext(), false)
    }

    fun endProgressDialog() {
        if (context != null)
            Utils.stopProgress()
    }

    override fun onCardClick(user: GitData.User) {
        Log.i(TAG, "onCardClick")
        var bundle = Bundle()
        bundle.putString(KEY_USER, Gson().toJson(user))
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
    }
}