package com.example.mygitapplication2.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mygitapplication.infra.model.GitData
import com.example.mygitapplication2.R

class UserAdapter(val context: Context, val list: ArrayList<GitData.User>,
val listener: UserCardListener) : RecyclerView.Adapter<UserAdapter.UserDataViewHolder>() {

    companion object{
        private const val TAG = "UserAdapter"
    }

    inner class UserDataViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var ivProfile: AppCompatImageView = view.findViewById(R.id.ivUserImages)
        val tvLogin: AppCompatTextView = view.findViewById(R.id.tvLogin)
        val tvId: AppCompatTextView = view.findViewById(R.id.tvId)
        val tvUserType: AppCompatTextView = view.findViewById(R.id.tvUserType)
        val cvMain: CardView = view.findViewById(R.id.cvMain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_git_users, parent, false)
        return UserDataViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: UserDataViewHolder, position: Int) {
        if(context != null && position < list.size){
            val item = list[position]
            val image = item.userImage
            val title = item.userName
            val id = item.id
            val type = item.type

            holder.tvLogin.text = title
            holder.tvId.text = id
            holder.tvUserType.text = type


            Glide.with(context)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.ivProfile)

            holder.cvMain.setOnClickListener{
                listener.onCardClick(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return if(list.isNotEmpty())
            list.size
        else
            0
    }

    fun updateList(list: ArrayList<GitData.User>){
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun clearList(){
        this.list.clear()
        notifyDataSetChanged()
    }

    interface UserCardListener{
        fun onCardClick(
            user: GitData.User
        )
    }
}