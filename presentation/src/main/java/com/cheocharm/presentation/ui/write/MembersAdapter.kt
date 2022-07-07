package com.cheocharm.presentation.ui.write

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cheocharm.domain.GroupMember
import com.cheocharm.presentation.databinding.ItemWriteGroupMemberBinding

class MembersAdapter : ListAdapter<GroupMember, MembersAdapter.ViewHolder>(MemberDiffCallback) {

    class ViewHolder(val binding: ItemWriteGroupMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var currentMember: GroupMember? = null

        fun bind(member: GroupMember) {
            currentMember = member

            // TODO: 프로필 이미지 설정
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWriteGroupMemberBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        getItem(position).run {
            viewHolder.bind(this)
        }
    }
}

object MemberDiffCallback : DiffUtil.ItemCallback<GroupMember>() {
    override fun areItemsTheSame(oldItem: GroupMember, newItem: GroupMember): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: GroupMember, newItem: GroupMember): Boolean =
        oldItem.imageUrl == newItem.imageUrl
}
