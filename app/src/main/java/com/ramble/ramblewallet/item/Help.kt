package com.ramble.ramblewallet.item

import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.bean.FaqInfos
import com.ramble.ramblewallet.bean.QueryFaqInfos
import com.ramble.ramblewallet.databinding.ItemHelpHeaderBinding
import com.ramble.ramblewallet.databinding.ItemHelpTodoBinding
import com.ramble.ramblewallet.wight.CheckableSimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.SimpleRecyclerItem
import com.ramble.ramblewallet.wight.adapter.ViewHolder

/**
 * 时间　: 2021/12/17 9:09
 * 作者　: potato
 * 描述　:
 */
class Help {
    class Header(val title: String) : CheckableSimpleRecyclerItem() {

        override fun getLayout(): Int = R.layout.item_help_header

        override fun bind(holder: ViewHolder) {
            val binding: ItemHelpHeaderBinding = holder.binding()
            binding.txtTitle.text = title
        }
    }

    class HotFaqList(val data: FaqInfos.CategoryReq) : SimpleRecyclerItem() {
        override fun getLayout(): Int = R.layout.item_help_todo

        override fun bind(holder: ViewHolder) {
            val binding: ItemHelpTodoBinding = holder.binding()
            binding.itemHelpTodo.text = data.name
            holder.attachOnClickListener(R.id.item_help_todo)
        }
    }

//    class adviceList(val data: String) : SimpleRecyclerItem() {
//        override fun getLayout(): Int = R.layout.item_help_todo
//
//        override fun bind(holder: ViewHolder) {
//            val binding: ItemHelpTodoBinding = holder.binding()
//            binding.itemHelpTodo.text = data
//            holder.attachOnClickListener(R.id.item_help_todo)
//        }
//    }
//
//    class coo(val data: String) : SimpleRecyclerItem() {
//        override fun getLayout(): Int = R.layout.item_help_todo
//
//        override fun bind(holder: ViewHolder) {
//            val binding: ItemHelpTodoBinding = holder.binding()
//            binding.itemHelpTodo.text = data
//            holder.attachOnClickListener(R.id.item_help_todo)
//        }
//    }
//
    class FaqAllTypeList(val data: QueryFaqInfos) : SimpleRecyclerItem() {
        override fun getLayout(): Int = R.layout.item_help_todo

        override fun bind(holder: ViewHolder) {
            val binding: ItemHelpTodoBinding = holder.binding()
            binding.itemHelpTodo.text = data.categoryName
            holder.attachOnClickListener(R.id.item_help_todo)
        }
    }
//
//    class HotFaqMainList(val data: Faq) : SimpleRecyclerItem() {
//        override fun getLayout(): Int = R.layout.item_sub_help_todo
//
//        override fun bind(holder: ViewHolder) {
//            val binding: com.square.pie.databinding.ItemSubHelpTodoBinding = holder.binding()
//            binding.itemSubHelpTodo.text = data.title
//            holder.attachOnClickListener(R.id.item_sub_help_todo)
//        }
//    }
//
    class FaqTypeList(val data: FaqInfos.NoviceReq) : SimpleRecyclerItem() {
        override fun getLayout(): Int = R.layout.item_help_todo

        override fun bind(holder: ViewHolder) {
            val binding: ItemHelpTodoBinding = holder.binding()
            binding.itemHelpTodo.text = data.categoryName
            holder.attachOnClickListener(R.id.item_help_todo)
        }
    }
//
//    class FaqTypeMainList(val data: Faq) : SimpleRecyclerItem() {
//        override fun getLayout(): Int = R.layout.item_sub_help_todo
//
//        override fun bind(holder: ViewHolder) {
//            val binding: com.square.pie.databinding.ItemSubHelpTodoBinding = holder.binding()
//            binding.itemSubHelpTodo.text = data.title
//            holder.attachOnClickListener(R.id.item_sub_help_todo)
//        }
//    }
}