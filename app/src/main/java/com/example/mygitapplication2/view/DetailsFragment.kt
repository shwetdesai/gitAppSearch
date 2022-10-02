package com.example.mygitapplication2.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.mygitapplication.infra.model.GitData
import com.example.mygitapplication2.MyGitApplication
import com.example.mygitapplication2.R
import com.example.mygitapplication2.databinding.FragmentDetailsBinding
import com.example.mygitapplication2.di.AppViewModelFactory
import com.example.mygitapplication2.view.MainFragment.Companion.KEY_USER
import com.example.mygitapplication2.view.adapter.UserAdapter
import com.example.mygitapplication2.viewModel.DetailsFragmentViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DetailsFragment : Fragment(), UserAdapter.UserCardListener {

    private var _binding: FragmentDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var user: GitData.User? = null
    lateinit var adapter: UserAdapter

    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private lateinit var viewModel: DetailsFragmentViewModel

    companion object{
        private const val TAG = "DetailsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if(!it.getString(KEY_USER).isNullOrEmpty()){
                user = Gson().fromJson(
                    it.getString(KEY_USER,"")
                    , object : TypeToken<GitData.User>() {}.type
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyGitApplication.getInstance()?.appComponents?.inject(this)
        initMvvm()
        setData()
        setUpAdapter()
        setUpObserver()
        setUpFloatingButton()
    }

    private fun initMvvm(){
        if (activity != null && context != null) {
            viewModel =
                ViewModelProvider(this, viewModelFactory)[DetailsFragmentViewModel::class.java]
            binding.viewModel = viewModel
        }
    }

    private fun setData(){
        if(activity != null && context != null && user != null){
            binding.tvUserName.text = user!!.userName
            val image = user!!.userImage

            Glide.with(requireContext())
                .load(image)
                .transform(CenterCrop(),RoundedCorners(12))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(binding.ivUserImage)
        }
    }


    private fun setUpAdapter(){
        if (activity != null && context != null) {
            adapter = UserAdapter(requireContext(),ArrayList<GitData.User>(),this)
            binding.rvFollowers.adapter = adapter
            binding.rvFollowers.isNestedScrollingEnabled = false

            getFollowers()
        }
    }

    private fun getFollowers(){
        if(activity != null && context != null && user != null && !user!!.followersUrl.isNullOrEmpty()){
            viewModel.getAllFollowers(user!!.followersUrl!!)
        }
    }

    private fun setUpObserver(){
        viewModel.data.observe(viewLifecycleOwner){
            if (activity != null && context != null && it != null) {
                adapter.clearList()
                adapter.updateList(it)
                binding.rvFollowers.animate()
            }
        }

        viewModel.noInternet.observe(viewLifecycleOwner) {
            if (it)
                Toast.makeText(requireContext(), "No Internet", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpFloatingButton(){
        if(activity != null && context != null){
            binding.ftBack.setOnClickListener{
                findNavController().navigateUp()
            }
        }
    }

    override fun onCardClick(user: GitData.User) {
        if(activity != null && context != null){
            Log.i(TAG, "onCardClick")
            var bundle = Bundle()
            bundle.putString(KEY_USER, Gson().toJson(user))
            findNavController().navigate(R.id.action_SecondFragment_to_SecondFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}